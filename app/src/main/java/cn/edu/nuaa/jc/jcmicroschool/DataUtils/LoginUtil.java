package cn.edu.nuaa.jc.jcmicroschool.DataUtils;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import cn.edu.nuaa.jc.jcmicroschool.LoginActivity;

/**
 * LoginUtil.java
 * Version:1.0
 */

public class LoginUtil {

    public static final int OK = 0;
    public static final int USERNAME_DONOT_EXIST = 4;
    public static final int PASSWORD_ERROR = 2;
    public static final int CONNECTION_ERROR = 3;
    public static final int USERNAME_OR_PASSWORD_ERROR=1;

    private String username;
    private String password;

    public LoginUtil(String mUsername, String mPassword){
        username=mUsername;
        password=mPassword;
    }

    private void login(){

    }

    /**
     * 使用本地数据自动登录
     * @param context 上下文
     * @return 登录是否成功
     */
    public static int login(Context context){
        Log.i("database_username","//"+LocalDataUtil.read(context,LocalDataUtil.DATABASE_USERDATA,"username"));
        Log.i("database_password","//"+LocalDataUtil.read(context,LocalDataUtil.DATABASE_USERDATA,"password"));
        return login(
                LocalDataUtil.read(context,LocalDataUtil.DATABASE_USERDATA,"username"),
                LocalDataUtil.read(context,LocalDataUtil.DATABASE_USERDATA,"password"),
                context);
    }

    public static int login(String mUsername, String mPassword,Context context) {

        //构建列表
        Map<String, String> userInfoList = new HashMap<>();
        userInfoList.put("username", mUsername);
        userInfoList.put("password", mPassword);

        NetUtil netUtil = new NetUtil(NetUtil.ADDR_LOGIN, userInfoList);
//        String jsonData="";
//        try {
//            jsonData=NetUtil.getDataFromNet(Url);
//            jsonData=jsonData.substring(jsonData.indexOf("{"),jsonData.lastIndexOf("}")+1);
//            Log.i("JSON",jsonData);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        Map<String, String> returnData = null;
        try {
            returnData = netUtil.getData(null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //在此执行网络异常处理
        if (returnData != null) {
            if (returnData.get("code").equals("" + NetUtil.CODE_OK)) {//正确返回的时候，把数据写入到本地数据库中
                LocalDataUtil.write(context, LocalDataUtil.DATABASE_USERDATA, LocalDataUtil.NICKNAME, returnData.get("nickname"));
                LocalDataUtil.write(context, LocalDataUtil.DATABASE_USERDATA, LocalDataUtil.USERNAME, mUsername);
                LocalDataUtil.write(context, LocalDataUtil.DATABASE_USERDATA, LocalDataUtil.PASSWORD, mPassword);
                LocalDataUtil.write(context, LocalDataUtil.DATABASE_USERDATA, LocalDataUtil.TEMP_PASSWORD, returnData.get("temp_password"));
                LocalDataUtil.write(context, LocalDataUtil.DATABASE_USERDATA, LocalDataUtil.LOGIN_STATUS, "yes");

                String permission=returnData.get("permission");
                if(permission.equals("student")) permission="学生";
                else if(permission.equals("parent")) permission="家长";
                else if(permission.equals("teacher"))permission="教师";
                LocalDataUtil.write(context, LocalDataUtil.DATABASE_USERDATA, LocalDataUtil.PERMISSION, permission);
                return OK;
            } else if (returnData.get("code").equals("" + NetUtil.CODE_USERNAME_OR_PASSWORD_ERROR)) {
                LocalDataUtil.write(context, LocalDataUtil.DATABASE_USERDATA, LocalDataUtil.LOGIN_STATUS, "no");
                return NetUtil.CODE_USERNAME_OR_PASSWORD_ERROR;
            } else {
                LocalDataUtil.write(context, LocalDataUtil.DATABASE_USERDATA, LocalDataUtil.LOGIN_STATUS, "no");
                return 0;
            }
        } else {
            //LocalDataUtil.write(context,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.LOGIN_STATUS,"no");
            return CONNECTION_ERROR;
        }
    }
    public static int modifyPassword(String originalPassword, String newPassword,Context context){

        //构建列表
        Map<String,String> userInfoList=new HashMap<>();
        userInfoList.put("original_password",originalPassword);
        userInfoList.put("new_password",newPassword);

        NetUtil netUtil=new NetUtil(NetUtil.ADDR_MODIFY_PASSWORD,userInfoList);
//        String jsonData="";
//        try {
//            jsonData=NetUtil.getDataFromNet(Url);
//            jsonData=jsonData.substring(jsonData.indexOf("{"),jsonData.lastIndexOf("}")+1);
//            Log.i("JSON",jsonData);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        Map<String,String> returnData= null;
        try {
            returnData = netUtil.getData(null,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //在此执行网络异常处理
        if(returnData!=null) {
            if (returnData.get("code").equals(""+NetUtil.CODE_OK)) {//正确返回的时候，把数据写入到本地数据库中
                LocalDataUtil.write(context, LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.PASSWORD, newPassword);
                return OK;
            } else if (returnData.get("code").equals(""+NetUtil.CODE_USERNAME_OR_PASSWORD_ERROR)){
                LocalDataUtil.write(context,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.LOGIN_STATUS,"no");
                return NetUtil.CODE_USERNAME_OR_PASSWORD_ERROR;
            }else {
                LocalDataUtil.write(context,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.LOGIN_STATUS,"no");
                return 0;
            }
        }else {
            //LocalDataUtil.write(context,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.LOGIN_STATUS,"no");
            return CONNECTION_ERROR;
        }

    }


}
