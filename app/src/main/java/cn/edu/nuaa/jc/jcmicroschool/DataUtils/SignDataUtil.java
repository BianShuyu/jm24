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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SignDataUtil extends SQLiteOpenHelper {
    public static final String DB_SIGN="db_sign.db";
    public static final int FUNCTION_发送口令=0;
    public static final int FUNCTION_班级签到=1;
    public static final int FUNCTION_未签到查询=2;
    public static final int FUNCTION_签到查询=3;//学生是否需要签到
    public static final int FUNCTION_可签到班级=4;

    public static final int ANSWER_CORRECT=200;
    public static final int ANSWER_WRONG=199;
    public static final int NEED_TO_SIGN=198;
    public static final int DO_NOT_NEED_TO_SIGN=197;

    private List<String> dataList=new ArrayList<>();
    private List<SignData> signDataList=new ArrayList<>();

    private SQLiteDatabase db;

    public SignDataUtil(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tb_students (school_id)");
        db.execSQL("CREATE TABLE tb_classes (class)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public int achieveFromInternet(Context context,int functionChoice,String signPwd,List<String> classes){
        NetUtil netUtil=null;
        Map<String,String> loginInfo=new HashMap<>();
        loginInfo.put("username",LocalDataUtil.read(context,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.USERNAME));
        loginInfo.put("temp_password",LocalDataUtil.read(context,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.TEMP_PASSWORD));
        switch (functionChoice){
            case FUNCTION_发送口令:
                loginInfo.put("sign",signPwd);
                netUtil=new NetUtil(NetUtil.ADDR_SIGN,loginInfo);
                break;
            case FUNCTION_未签到查询:
                netUtil=new NetUtil(NetUtil.ADDR_SIGN_RESULT,loginInfo);
                break;
            case FUNCTION_班级签到:
                loginInfo.put("sign",signPwd);
                loginInfo.put("count",classes.size()+"");
                for(int i=0;i<classes.size();i++){
                    loginInfo.put(""+i,classes.get(i));
                }
                netUtil=new NetUtil(NetUtil.ADDR_SIGN_TEACHER,loginInfo);
                break;
            case FUNCTION_签到查询:
                netUtil=new NetUtil(NetUtil.ADDR_GET_SIGN_INFO,loginInfo);
                break;
            case FUNCTION_可签到班级:
                netUtil=new NetUtil(NetUtil.ADDR_CLASSES,loginInfo);
                break;
        }

        Map<String,String> returnData=null;
        try {
            returnData=netUtil.getData(null,null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(returnData!=null){
            switch (functionChoice){
                case FUNCTION_发送口令:
                    for(Map.Entry<String,String> entry:returnData.entrySet()){
                        if(entry.getKey().equals("code") && entry.getValue().equals("200")) return ANSWER_CORRECT;
                        else if(entry.getKey().equals("code") && entry.getValue().equals("199")) return ANSWER_WRONG;
                    }
                    break;
                case FUNCTION_未签到查询:
                    deleteAll();//清除全部未签到学生的数据

                    for (Map.Entry<String, String> entry : returnData.entrySet()) {
                        if (!entry.getKey().equals("code")){
                            SignData sd=new SignData();
                            sd.schoolId学号=entry.getValue();
                            insertStudents(sd);
                            signDataList.add(sd);
                        }
                    }
                    break;
                case FUNCTION_班级签到:
                    return NetUtil.CODE_OK;
                case FUNCTION_签到查询:
                    for (Map.Entry<String, String> entry : returnData.entrySet()) {
                        if (entry.getKey().equals("code")){
                            if(entry.getValue().equals("200"))
                                return NEED_TO_SIGN;
                            else if(entry.getValue().equals("199"))
                                return DO_NOT_NEED_TO_SIGN;
                        }
                    }
                    break;
                case FUNCTION_可签到班级:
                    dataList=new ArrayList<>();
                    for (Map.Entry<String, String> entry : returnData.entrySet()) {
                        if (!entry.getKey().equals("code")){
                            dataList.add(entry.getValue());
                        }
                    }
                    insertClasses(dataList);
                    break;
            }


        }

        return 1;
    }

    public void insertStudents(SignData data){
        db=getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("school_id", data.schoolId学号);
        db.insert("tb_students", null, cv);
    }

    public void insertClasses(List<String> data){
        db=getWritableDatabase();
        ContentValues cv = new ContentValues();
        for(String s:data) {
            cv.put("class", s);
            db.insert("tb_classes", null, cv);
        }
    }

    public void deleteAll(){
        db=getWritableDatabase();
        db.delete("tb_students","",null );
        db.delete("tb_classes","",null);
    }

    public List<String> getClassesData(){
        return dataList;
    }

    public List<SignData> getSignDataList() {
        signDataList.clear();
        db=getReadableDatabase();
        Cursor c;
        c=db.rawQuery("SELECT * FROM tb_students ORDER BY school_id",null);


        if(c.moveToFirst()){
            do{//该循环遍历数据库中所有数据
                SignData d=new SignData();
                d.schoolId学号=c.getString(c.getColumnIndex("school_id"));
                signDataList.add(d);
            }while (c.moveToNext());
        }
        c.close();

        return signDataList;
    }

    public class SignData{

        public String schoolId学号;

        public SignData(){
            schoolId学号="0000000000";
        }
    }
}
