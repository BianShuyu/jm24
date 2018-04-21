package cn.edu.nuaa.jc.jcmicroschool.DataUtils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JsonUtil.java
 * Version:1.0
 */

public class JsonUtil {
    /**
     * 解析一层JSON字符串为MAP
     * @param jsonStr
     * @return
     */
    public static Map<String,String> analyze(String jsonStr){
        Map<String,String> jsonlist=new LinkedHashMap<>();
        JSONObject jsonObject;
        Log.i("JsonUtil,jsonStr",jsonStr);
        try {
            jsonObject=new JSONObject(jsonStr);
            jsonlist=analyze(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonlist;
    }

    public static Map<String,String> analyze(JSONObject jsonObject){
        Map<String,String> jsonlist=new LinkedHashMap<>();
        try {
            Iterator keys=jsonObject.keys();
            while (keys.hasNext()){
                String key=keys.next().toString();
                String value= jsonObject.getString(key);
                jsonlist.put(key,value);
                Log.i("JsonList",key+':'+value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonlist;
    }

}
