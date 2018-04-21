package cn.edu.nuaa.jc.jcmicroschool.DataUtils;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by bsy on 2017/9/24.
 */

public class SchoolBusDataUtil {
    private List<SchoolBusData> schoolBusDataList;

    public SchoolBusDataUtil() {
        schoolBusDataList=new ArrayList<>();
    }

    public int achieveFromInternet(Context context){
        Boolean connectSucceed=true;
        NetUtil netUtil=new NetUtil(NetUtil.ADDR_SCHOOL_BUS,null);
        Map<String,String> originalData;
        try {
            originalData=netUtil.getData(LocalDataUtil.JSON_BUS,context);
        } catch (IOException e) {
            e.printStackTrace();
            connectSucceed=false;
            originalData=JsonUtil.analyze(LocalDataUtil.read(context,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.JSON_BUS));
        }

        for (Map.Entry<String,String> entry :originalData.entrySet()) {
            Map<String, String> unit = JsonUtil.analyze(entry.getValue());
            Log.i("SchoolBusUtil,unitData", unit.get("line_id"));
            Boolean error = true;//用于判断是否在locationDatas里没出现过
            for (SchoolBusData schoolBusData : schoolBusDataList) {
                if (unit.get("line_id").equals(schoolBusData.getLineId()+"")) {
                    schoolBusData.getDaytime().add(unit.get("daytime").substring(0,5));
                    error = false;
                }
            }
            if (error) {
                SchoolBusData temp = new SchoolBusData(Integer.parseInt(unit.get("line_id")),unit.get("route"),new LinkedList<String>());
                temp.getDaytime().add(unit.get("daytime").substring(0,5));
                schoolBusDataList.add(temp);
            }
        }





        if(connectSucceed) {
            return NetUtil.CODE_OK;
        }else {
            return NetUtil.CODE_CONNECTION_ERROR;
        }
    }


    public List<SchoolBusData> getSchoolBusDataList() {
        return schoolBusDataList;
    }

    public class SchoolBusData{//此处不提供日期变量，每次只从服务器读取一天的数据，因此不需要获取日期
        private int lineId;
        private String route;
        private List<String> daytime;

        public int getLineId() {
            return lineId;
        }

        public String getRoute() {
            return route;
        }

        public List<String> getDaytime() {
            return daytime;
        }

        public SchoolBusData(int lineId,String route, List<String> daytime) {
            this.lineId=lineId;
            this.route=route;
            this.daytime=daytime;
        }
    }
}

