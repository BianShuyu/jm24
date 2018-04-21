package cn.edu.nuaa.jc.jcmicroschool.DataUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * NetUtil.java
 * Version:1.0
 */

/**
 * NetUtil：访问服务器专用类。
 * 使用方法：new实例，设置地址和参数，获取数据。
 * 只负责一层json解析
 */
public class NetUtil {
    private static final String ADDR="http://192.168.3.200/info.php";
    public static final String ADDR_LOGIN = "http://192.168.3.200/jcmicroschool/login.php";
    public static final String ADDR_SCHOOL_BUS= "http://192.168.3.200/jcmicroschool/schoolbus.php";
    public static final String ADDR_CURRICULUM_SCHEDULE= "http://192.168.3.200/jcmicroschool/schedule.php";
    public static final String ADDR_CURRENT_WEEK="http://192.168.3.200/jcmicroschool/week.php";
    public static final String ADDR_RESULTS="http://192.168.3.200/jcmicroschool/results.php";
    public static final String ADDR_MODIFY_PASSWORD="http://192.168.3.200/jcmicroschool/modifypassword.php";
    public static final String ADDR_SIGN="http://192.168.3.200/jcmicroschool/sign.php";
    public static final String ADDR_SIGN_TEACHER="http://192.168.3.200/jcmicroschool/sign_teacher.php";
    public static final String ADDR_SIGN_RESULT="http://192.168.3.200/jcmicroschool/sign_result.php";
    public static final String ADDR_BIND="http://192.168.3.200/jcmicroschool/bind.php";
    public static final String ADDR_GET_SIGN_INFO="http://192.168.3.200/jcmicroschool/get_sign_info.php";
    public static final String ADDR_CLASSES="http://192.168.3.200/jcmicroschool/classes.php";

    public static final int CODE_OK=200;
    public static final int CODE_CONNECTION_ERROR=-1;
    public static final int CODE_USERNAME_OR_PASSWORD_ERROR=1;


    private Map<String,String> datalist;
    private String addr;
    private Map<String,String> jsonlist;

//    /**
//     * 构建URL
//     * @param map Hashmap键值对
//     * @return
//     */
//    public static URL buildUrl(Map<String,String> map){
//        Uri.Builder buildUri=Uri.parse(ADDR).buildUpon();
//        for(Map.Entry<String,String> entry : map.entrySet()){
//            buildUri.appendQueryParameter(entry.getKey(),entry.getValue());
//        }
//
//        Uri uri=buildUri.build();
//
//        URL url=null;
//
//        try {
//            url=new URL(buildUri.toString());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//        Log.i("URL",url.toString());
//        return url;
//    }


    public NetUtil(String addr,@Nullable Map<String,String>  datalist){
        this.addr=addr;
        this.datalist = datalist;

        jsonlist=new HashMap<>();
        jsonlist.put("reserved","0");//防止空指针
    }

    public void setDatalist(Map<String, String> datalist) {
        this.datalist = datalist;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    /**
     *
     * @param localDataName 如果要把数据存在本地，设置为LocalData.常量，否则设为null
     * @return
     * @throws IOException
     */
    public Map<String,String> getData(@Nullable String localDataName, @Nullable  Context context) throws IOException{
        //初始化
        URL url=new URL(addr);
        Log.i("访问网址：",addr);
        HttpURLConnection conn=(HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        if(addr.equals(ADDR_BIND)||addr.equals(ADDR_CURRICULUM_SCHEDULE)){
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
        }
        //准备参数列表
        String strData="";
        if(datalist!=null) {
            for (Map.Entry entry : datalist.entrySet()) {
                strData += ('&' + (String) entry.getKey() + '=' + URLEncoder.encode((String) entry.getValue(), "UTF-8"));
            }
            strData = strData.substring(1);
            Log.i("NetUtil, 参数列表字符串", strData);
        }else {
            Log.i("NetUtil, 参数列表字符串", "无");
        }
        //发送数据
        conn.setDoOutput(true);
        OutputStream os=conn.getOutputStream();
        os.write(strData.getBytes());
        os.flush();
        os.close();
        //接受数据
        String strInput="";
        if(conn.getResponseCode()==CODE_OK){
            InputStream is=conn.getInputStream();
            Scanner scanner=new Scanner(is);
            scanner.useDelimiter("\\A");

            boolean hasInput=scanner.hasNext();
            if(hasInput){
                strInput=scanner.next();
            } else {
                strInput="";
            }
        }
        Log.i("NetUtil,Original Data",strInput);
        if(localDataName!=null && context!=null){
            LocalDataUtil.write(context,LocalDataUtil.DATABASE_USERDATA,localDataName,strInput);
        }
        //解析数据为Map
        jsonlist=JsonUtil.analyze(strInput);
//        JSONObject jsonObject = null;
//        try {
//            jsonObject=new JSONObject(strInput);
//            Iterator keys=jsonObject.keys();
//            while (keys.hasNext()){
//                String key=keys.next().toString();
//                Object value= jsonObject.get(key);
//                jsonlist.put(key,value);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        if(jsonlist==null){
            jsonlist.put("code",""+CODE_CONNECTION_ERROR);
        }
        return jsonlist;
    }

    /**
     * 直接调用该函数可以判断手机是否联网
     * @param context 上下文
     * @return
     */
    public static Boolean isNetworkConnected(Context context){
        if(context!=null){
            ConnectivityManager manager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info=manager.getActiveNetworkInfo();
            if(info!=null){
                return info.isConnected();
            }
        }
        return false;
    }




}
