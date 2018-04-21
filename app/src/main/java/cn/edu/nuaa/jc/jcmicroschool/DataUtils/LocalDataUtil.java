package cn.edu.nuaa.jc.jcmicroschool.DataUtils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * LocalDataUtil.java
 * Version:1.0
 * 本地数据工具类，使用ShardPreferences实现本地数据存储
 */

public class LocalDataUtil {

    //数据库名常量定义
    public static final String DATABASE_USERDATA = "userdata";
    public static final String DATABASE_RESULTS_SUMMARY= "results_summary";
    //数据键名常量定义
    public static final String USERNAME= "username";//用户名
    public static final String NOTIFICATION= "notification";//通知设置,no/yes
    public static final String PASSWORD= "password";//密码
    public static final String TEMP_PASSWORD= "temp_password";//临时密码
    public static final String PERMISSION="permission";//账户权限
    public static final String JSON_BUS = "json_bus";//历史数据（JSON格式）
    public static final String JSON_LOCATION= "json_location";//地址数据（JSON格式）
    public static final String NICKNAME = "nickname";//昵称
    public static final String LOGIN_STATUS = "login_status";//登录状态，no/yes
    public static final String BIND_USERNAME="bind_username";
    public static final String BIND_PASSWORD="bind_password";
    public static final String CURRENT_WEEK="current_week";//当前周

    public static final String GPA="GPA";//当前周

    private static SharedPreferences data;

    public LocalDataUtil(){

    }

    public static void init(Context context){
        String data=read(context,DATABASE_USERDATA,NOTIFICATION);
        if(data==null){
            write(context,DATABASE_USERDATA,NOTIFICATION,"yes");
        }

        data=read(context,DATABASE_USERDATA,USERNAME);
        if(data==null){
            write(context,DATABASE_USERDATA,USERNAME,"");
        }

        data=read(context,DATABASE_USERDATA,PASSWORD);
        if(data==null){
            write(context,DATABASE_USERDATA,PASSWORD,"");
        }

        data=read(context,DATABASE_USERDATA,NICKNAME);
        if(data==null){
            write(context,DATABASE_USERDATA,NICKNAME,"");
        }

        data=read(context,DATABASE_USERDATA,TEMP_PASSWORD);
        if(data==null){
            write(context,DATABASE_USERDATA,TEMP_PASSWORD,"");
        }

        data=read(context,DATABASE_USERDATA,PERMISSION);
        if(data==null){
            write(context,DATABASE_USERDATA,PERMISSION,"无");
        }

        data=read(context,DATABASE_USERDATA, JSON_BUS);
        if(data==null){
            write(context,DATABASE_USERDATA, JSON_BUS,"");
        }

        data=read(context,DATABASE_USERDATA,JSON_LOCATION);
        if(data==null){
            write(context,DATABASE_USERDATA,JSON_LOCATION,"");
        }

        data=read(context,DATABASE_USERDATA,LOGIN_STATUS);
        if(data==null){
            write(context,DATABASE_USERDATA,LOGIN_STATUS,"no");
        }

        data=read(context,DATABASE_USERDATA,BIND_USERNAME);
        if(data==null){
            write(context,DATABASE_USERDATA,BIND_USERNAME,"");
        }
        data=read(context,DATABASE_USERDATA,BIND_PASSWORD);
        if(data==null){
            write(context,DATABASE_USERDATA,BIND_PASSWORD,"");
        }
        data=read(context,DATABASE_USERDATA,CURRENT_WEEK);
        if(data==null){
            write(context,DATABASE_USERDATA,CURRENT_WEEK,"1");
        }

        data=read(context,DATABASE_RESULTS_SUMMARY,GPA);
        if(data==null){
            write(context,DATABASE_RESULTS_SUMMARY,GPA,"1.00");
        }

    }

    public static String read(Context context, String database, String key){
        data=context.getSharedPreferences(database,Context.MODE_PRIVATE);
        return data.getString(key,null);
    }

    public static void write(Context context,String database,String key,String value){
        data = context.getSharedPreferences(database,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(key,value);
        editor.apply();
    }



}
