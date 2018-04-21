package cn.edu.nuaa.jc.jcmicroschool.DataUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by bsy on 2017/9/16.
 */

public class ResultsQueryDataUtil extends SQLiteOpenHelper {

    public static final String DB_RESULTS_QUERY="db_results_query.db";
    public static final int ORDER_DATE=0;
    public static final int ORDER_GPA=1;
    public static final int ORDER_NAME=2;

    private List<ResultsQueryData> dataList;
    private SQLiteDatabase db;


    public ResultsQueryDataUtil(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, 1);
        dataList =new LinkedList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE results_query (name,gpa,score,credit,date,term,course_id,course_type,properties)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int achieveFromInternet(Context context){
        Map<String,String> loginInfo=new HashMap<>();
        loginInfo.put("username",LocalDataUtil.read(context,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.USERNAME));
        loginInfo.put("temp_password",LocalDataUtil.read(context,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.TEMP_PASSWORD));

        NetUtil netUtil=new NetUtil(NetUtil.ADDR_RESULTS,loginInfo);
        Map<String,String> returnData=null;
        try {
            returnData=netUtil.getData(null,null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(returnData!=null){
            delete();//清除全部网络获取的数据

            double credit=0;
            double score=0;

            for (Map.Entry<String, String> entry : returnData.entrySet()) {
                if (!entry.getKey().equals("code")){
                    Map<String,String> data=JsonUtil.analyze(entry.getValue());
                    if(data!=null){
                        ResultsQueryData rqd=new ResultsQueryData();
                        rqd.name课程名=data.get("course_name");
                        rqd.gpa绩点=data.get("gpa");
                        rqd.score成绩=data.get("score");
                        rqd.credit学分=data.get("credit");
                        rqd.date考试日期=data.get("date");
                        rqd.term学期=data.get("year")+"学年 第"+data.get("term")+"学期";
                        rqd.courseId课程编号 =data.get("course_id");
                        rqd.courseType课程类别=data.get("course_type");
                        rqd.properties考试性质=data.get("properties");
                        insert(rqd);//加载全部网络获取的数据
                        if(rqd.credit学分!=null &&!rqd.credit学分.equals("")) {
                            credit += Double.parseDouble(rqd.credit学分);
                            score += Double.parseDouble(rqd.credit学分) * Double.parseDouble(rqd.gpa绩点);
                        }
                    }
                }
            }

            LocalDataUtil.write(context,LocalDataUtil.DATABASE_RESULTS_SUMMARY,LocalDataUtil.GPA,String.format(Locale.ENGLISH,"%.2f",score/credit));
        }

        return 1;
    }

    public void insert(ResultsQueryData data){
        db=getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", data.name课程名);
        cv.put("gpa", data.gpa绩点);
        cv.put("score", data.score成绩);
        cv.put("credit", data.credit学分);
        cv.put("date", data.date考试日期);
        cv.put("term", data.term学期);
        cv.put("course_id", data.courseId课程编号);
        cv.put("course_type", data.courseType课程类别);
        cv.put("properties", data.properties考试性质);

        db.insert("results_query", null, cv);
    }

    public void delete(){
        db=getWritableDatabase();
        db.delete("results_query","",null );
    }

    public List<ResultsQueryData> getResultsQueryDataList(int order) {
        dataList.clear();
        db=getReadableDatabase();
        Cursor c;
        if(order==ORDER_NAME){
            c=db.rawQuery("SELECT * FROM results_query ORDER BY name",null);
            Log.i("按课程名排序",".");
        }else if(order==ORDER_GPA){
            c=db.rawQuery("SELECT * FROM results_query ORDER BY gpa DESC",null);
            Log.i("按GPA排序",".");
        }else {
            c=db.rawQuery("SELECT * FROM results_query ORDER BY date DESC",null);
            Log.i("按考试日期排序",".");
        }

        if(c.moveToFirst()){
            do{//该循环遍历数据库中所有数据
                ResultsQueryData d=new ResultsQueryData();
                d.name课程名=c.getString(c.getColumnIndex("name"));
                d.gpa绩点=c.getString(c.getColumnIndex("gpa"));
                d.score成绩=c.getString(c.getColumnIndex("score"));
                d.credit学分=c.getString(c.getColumnIndex("credit"));
                d.date考试日期=c.getString(c.getColumnIndex("date"));
                d.term学期=c.getString(c.getColumnIndex("term"));
                d.courseId课程编号=c.getString(c.getColumnIndex("course_id"));
                d.courseType课程类别=c.getString(c.getColumnIndex("course_type"));
                d.properties考试性质=c.getString(c.getColumnIndex("properties"));
                dataList.add(d);
                Log.i("成绩刷新：",d.name课程名+","+d.date考试日期+","+d.gpa绩点);
            }while (c.moveToNext());
        }
        c.close();

        return dataList;
    }



    public class ResultsQueryData{

        public String name课程名;//课程名
        public String gpa绩点;//绩点
        public String score成绩;//成绩（百分之or五级制）
        public String credit学分;//学分
        public String date考试日期;//考试日期
        public String term学期;//学期
        public String courseId课程编号;//编号
        public String courseType课程类别;//课程类别（选修课or必修课）
        public String properties考试性质;//考试性质（期末/补考/免修）


        public ResultsQueryData() {
            // TODO: 2017/9/16 默认值
            name课程名 ="计算机语言与程序设计基础（C）";
            gpa绩点 ="5.2";
            score成绩 ="92";
            credit学分 ="5.0";
            date考试日期 ="2016-06-06";
            term学期 ="2015-2016 第二学期";
            courseId课程编号 ="02200260";
            courseType课程类别 ="必修课";
            properties考试性质 ="免修";

        }


    }
}

