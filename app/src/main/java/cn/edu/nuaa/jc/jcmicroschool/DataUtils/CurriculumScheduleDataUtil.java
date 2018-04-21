package cn.edu.nuaa.jc.jcmicroschool.DataUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by bsy88 on 2018/2/3.
 */

public class CurriculumScheduleDataUtil extends SQLiteOpenHelper{

    public static final String DB_CURRICULUM_SCHEDULE="db_curriculum_schedule.db";

    public static final int DELETE_BANNED=0;
    public static final int DELETE_LOCAL=1;
    public static final int DELETE_REFRESH=2;

    private SQLiteDatabase db;

    private Context context;


    public CurriculumScheduleDataUtil(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE curriculum_schedule (name,week,weekday,daytime,location,teacher,fromnet)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int achieveFromInternet(Context context){

        Map<String,String> loginInfo=new HashMap<>();
        loginInfo.put("username",LocalDataUtil.read(context,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.USERNAME));
        loginInfo.put("temp_password",LocalDataUtil.read(context,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.TEMP_PASSWORD));

        NetUtil netUtil=new NetUtil(NetUtil.ADDR_CURRICULUM_SCHEDULE,loginInfo);
        Map<String,String> returnData=null;
        try {
            returnData=netUtil.getData(null,null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(returnData!=null){
            delete(DELETE_REFRESH, null);//清除全部网络获取的数据
            for (Map.Entry<String, String> entry : returnData.entrySet()) {
                if (!entry.getKey().equals("code")){
                    Map<String,String> data=JsonUtil.analyze(entry.getValue());
                    if(data!=null){
                        CurriculumScheduleData csd=new CurriculumScheduleData();
                        csd.name=data.get("course_name");
                        csd.location=data.get("location");
                        csd.teacher=data.get("teacher");
                        csd.singleWeek=Integer.parseInt(data.get("week"));
                        csd.weekday=Integer.parseInt(data.get("weekday"));
                        csd.daytime=Integer.parseInt(data.get("daytime"));
                        csd.fromNet=1;
                        insert(csd);//加载全部网络获取的数据
                    }
                }
            }
        }

        //insert(new CurriculumScheduleData("高等数学",1,1,1,"A1N101","??",0));
        getCourseData(1,1);


        return 1;
    }

    public void insert(CurriculumScheduleData data){
        db=getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", data.name);
        cv.put("week", data.singleWeek + "");
        cv.put("weekday", data.weekday + "");
        cv.put("daytime", data.daytime + "");
        cv.put("location", data.location);
        cv.put("teacher", data.teacher);
        cv.put("fromnet", data.fromNet);
        db.insert("curriculum_schedule", null, cv);
    }

    public void delete(CurriculumScheduleData oldData){
        db=getWritableDatabase();
        db.delete("curriculum_schedule",
                "name=? AND teacher=? AND location=? AND weekday=? AND daytime=?",
                new String[]{oldData.name, oldData.teacher, oldData.location, oldData.weekday + "", oldData.daytime + ""} );
    }

    public void delete(int deleteMode, CurriculumScheduleData data){
        db=getWritableDatabase();
        if(deleteMode==DELETE_REFRESH) {
            db.delete("curriculum_schedule","fromnet=1",null );
        }
    }

    public void save(CurriculumScheduleData oldData,CurriculumScheduleData newData){
        if(oldData.name!=null && !oldData.name.equals("")) {
            db = getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM curriculum_schedule " +
                            "WHERE name=? AND teacher=? AND location=? AND weekday=? AND daytime=?",
                    new String[]{oldData.name, oldData.teacher, oldData.location, oldData.weekday + "", oldData.daytime + ""});
            Boolean fromNet = false;
            if (c.moveToFirst()) {
                fromNet = (c.getInt(c.getColumnIndex("fromnet")) == 1);
                newData.fromNet = (fromNet ? 1 : 0);
            } else {
                newData.fromNet = 0;
            }
            c.close();
            db = getWritableDatabase();
            db.delete("curriculum_schedule",
                    "name=? AND teacher=? AND location=? AND weekday=? AND daytime=?",
                    new String[]{oldData.name, oldData.teacher, oldData.location, oldData.weekday + "", oldData.daytime + ""});
        }else{
            newData.fromNet=0;
        }
        for (int i=1;i<=30;i++){
            if(newData.week[i]){
                newData.singleWeek=i;
                insert(newData);
            }
        }
    }
    public CurriculumScheduleData getCourseData(int week,int weekday,int daytime){
        db=getReadableDatabase();
        CurriculumScheduleData data=null;
        Cursor c=db.rawQuery("SELECT * FROM curriculum_schedule WHERE week=? AND weekday=? AND daytime=?",new String[]{week+"",weekday+"",daytime+""});
        if(c.moveToFirst()) {
            data=new CurriculumScheduleData();

            data.name = c.getString(c.getColumnIndex("name"));
            data.singleWeek = c.getInt(c.getColumnIndex("week"));
            data.weekday = c.getInt(c.getColumnIndex("weekday"));
            data.daytime = c.getInt(c.getColumnIndex("daytime"));
            data.location = c.getString(c.getColumnIndex("location"));
            data.teacher = c.getString(c.getColumnIndex("teacher"));
        }
        c.close();
        return data;
    }

    public List<CurriculumScheduleData> getCourseData(int weekday,int daytime){
        db=getReadableDatabase();
        List<CurriculumScheduleData> dataList=new LinkedList<>();
        Cursor c=db.rawQuery("SELECT * FROM curriculum_schedule WHERE weekday=? AND daytime=?",new String[]{weekday+"",daytime+""});
        if(c.moveToFirst()){
            do{//该循环遍历数据库中所有数据

                String tName=c.getString(c.getColumnIndex("name"));
                int tWeek=c.getInt(c.getColumnIndex("week"));
                int tWeekday=c.getInt(c.getColumnIndex("weekday"));
                int tDaytime=c.getInt(c.getColumnIndex("daytime"));
                String tLocation=c.getString(c.getColumnIndex("location"));
                String tTeacher=c.getString(c.getColumnIndex("teacher"));

                Boolean error=false;
                for(CurriculumScheduleData d:dataList){
                    if(d.name.equals(tName) && d.teacher.equals(tTeacher) && d.location.equals(tLocation)){
                        error=true;
                        d.week[tWeek]=true;
                        //Log.i("CurriculumScheduleData",d.toString());
                    }
                }
                if(!error){
                    CurriculumScheduleData data=new CurriculumScheduleData();
                    data.name=tName;
                    data.teacher=tTeacher;
                    data.location=tLocation;
                    data.weekday=weekday;
                    data.daytime=daytime;
                    data.week[tWeek]=true;
                    dataList.add(data);
                    //Log.i("CurriculumScheduleData",data.toString());
                }




            }while (c.moveToNext());
        }
        c.close();
        return dataList;
    }

    public int getCurrentWeek(){
        NetUtil util=new NetUtil(NetUtil.ADDR_CURRENT_WEEK, null);
        Map<String,String> data=null;
        try {
            data=util.getData(null,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(data!=null) {
            Log.i("CURRENT_WEEK",data.get("week"));
            LocalDataUtil.write(context,LocalDataUtil.DATABASE_USERDATA, LocalDataUtil.CURRENT_WEEK,data.get("week"));
            return Integer.parseInt(data.get("week"));
        }
        return 0;
    }

    public CurriculumScheduleData getEmptyCurriculumScheduleData(){
        return new CurriculumScheduleData();
    }

    public class CurriculumScheduleData{
        public String name;
        public Boolean[] week;
        public int singleWeek;
        public int weekday;
        public int daytime;
        public String location;
        public String teacher;
        public int fromNet;


        public CurriculumScheduleData() {
            week=new Boolean[50];
            for (int i=0;i<50;i++) week[i]=false;
        }

        public CurriculumScheduleData(String name, Boolean[] week, int weekday, int daytime, String location, String teacher, int fromNet) {
            this.name = name;
            this.week = week;
            this.weekday = weekday;
            this.daytime = daytime;
            this.location = location;
            this.teacher = teacher;
            this.fromNet = fromNet;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder=new StringBuilder();
            Boolean gap=false;
            int previous=0;
            Boolean first=true;
            Boolean cntinue=false;
            for(int i=1;i<30;i++){
                if(week[i]){
                    if(first){
                        stringBuilder.append(""+i);
                        previous=i;
                        first=false;
                        gap=false;
                    }else {
                        if(gap==true){
                            previous=i;
                            stringBuilder.append(","+i);
                            gap=false;
                        }else {
                            if(i>previous && cntinue==false){
                                stringBuilder.append("-");
                                cntinue=true;
                            }
                        }
                    }
                }else {
                    if(gap==false && cntinue==true){
                        stringBuilder.append((i-1)+"");
                        cntinue=false;
                        gap=true;
                    }else if(gap==false) gap=true;
                }
            }
            String sWeek="第"+stringBuilder.toString()+"周";
            return sWeek;
        }
    }

}
