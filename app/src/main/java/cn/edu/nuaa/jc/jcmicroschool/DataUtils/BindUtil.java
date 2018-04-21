package cn.edu.nuaa.jc.jcmicroschool.DataUtils;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BindUtil {

    public static int bind(Context context,String username,String password){
        Boolean connectSucceed=true;
        Map<String,String> loginInfo=new HashMap<>();
        loginInfo.put("username",LocalDataUtil.read(context,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.USERNAME));
        loginInfo.put("temp_password",LocalDataUtil.read(context,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.TEMP_PASSWORD));
        loginInfo.put("school_name",username);
        loginInfo.put("school_pwd",password);
        NetUtil netUtil=new NetUtil(NetUtil.ADDR_BIND,loginInfo);

        Map<String,String> originalData=null;
        try {
            originalData=netUtil.getData(LocalDataUtil.JSON_BUS,context);
        } catch (IOException e) {
            e.printStackTrace();
            connectSucceed=false;
            originalData=JsonUtil.analyze(LocalDataUtil.read(context,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.JSON_BUS));
        }






        if(connectSucceed) {
            return NetUtil.CODE_OK;
        }else {
            return NetUtil.CODE_CONNECTION_ERROR;
        }
    }
}
