package cn.edu.nuaa.jc.jcmicroschool.Fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import cn.edu.nuaa.jc.jcmicroschool.CurriculumScheduleActivity;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.CurriculumScheduleDataUtil;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.LocalDataUtil;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.NetUtil;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.ResultsQueryDataUtil;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.SignDataUtil;
import cn.edu.nuaa.jc.jcmicroschool.R;
import cn.edu.nuaa.jc.jcmicroschool.ResultsQueryActivity;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private int needToSign=SignDataUtil.DO_NOT_NEED_TO_SIGN;
    private LinearLayout linearLayout;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        linearLayout=getActivity().findViewById(R.id.linearLayout_home);


    }


    @Override
    public void onResume() {
        super.onResume();
        linearLayout.removeAllViews();
        new HomeAsync().execute();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        linearLayout.removeAllViews();
    }

    private void setViews(){

        linearLayout.removeAllViews();
        //获取当前星期
        int weekday= (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)+5)%7+1;
        Log.i("星期",weekday+"");

        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View v;
        TextView tvTitle;
        TextView tvInfo;


        //签到情况
        if(LocalDataUtil.read(getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.PERMISSION).equals("教师")) {
            SignDataUtil sdu=new SignDataUtil(HomeFragment.this.getActivity(),SignDataUtil.DB_SIGN, null,0);
            List<SignDataUtil.SignData> sData = sdu.getSignDataList();

            if (sData.size() != 0) {
                v = inflater.inflate(R.layout.item_home, linearLayout, false);
                tvTitle = v.findViewById(R.id.textView_home_title);
                tvInfo = v.findViewById(R.id.textView_home_info);

                tvTitle.setText("签到情况");
                tvInfo.setText("尚未签到：\n  ");
                for (SignDataUtil.SignData d : sData) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(d.schoolId学号).append(", ");
                    tvInfo.append(builder.toString());
                }
                tvInfo.setText(tvInfo.getText().toString().substring(0,tvInfo.getText().toString().length()-2));

                linearLayout.addView(v);
            }
        }else if(LocalDataUtil.read(getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.PERMISSION).equals("学生")||
                LocalDataUtil.read(getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.PERMISSION).equals("家长")){
            if(needToSign==SignDataUtil.NEED_TO_SIGN){
                v = inflater.inflate(R.layout.item_home, linearLayout, false);
                tvTitle = v.findViewById(R.id.textView_home_title);
                tvInfo = v.findViewById(R.id.textView_home_info);

                tvTitle.setText("签到");
                tvInfo.setText("请立即签到！");
                linearLayout.addView(v);
            }
        }

        //今日课程
        if(LocalDataUtil.read(getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.PERMISSION).equals("学生")||
                LocalDataUtil.read(getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.PERMISSION).equals("家长")) {
            CurriculumScheduleDataUtil csdu = new CurriculumScheduleDataUtil(getActivity(), CurriculumScheduleDataUtil.DB_CURRICULUM_SCHEDULE, null, 0);
            List<CurriculumScheduleDataUtil.CurriculumScheduleData> csData = new LinkedList<>();
            for (int daytime = 1; daytime <= 5; daytime++) {
                CurriculumScheduleDataUtil.CurriculumScheduleData d =
                        csdu.getCourseData(
                                Integer.parseInt(LocalDataUtil.read(getActivity(), LocalDataUtil.DATABASE_USERDATA, LocalDataUtil.CURRENT_WEEK)),
                                weekday,
                                daytime);
                if (d != null) {
                    csData.add(d);
                }
            }
            if (csData.size() != 0) {
                v = inflater.inflate(R.layout.item_home, linearLayout, false);
                tvTitle = v.findViewById(R.id.textView_home_title);
                tvInfo = v.findViewById(R.id.textView_home_info);

                tvTitle.setText("今日课程");
                tvInfo.setText("");
                for (CurriculumScheduleDataUtil.CurriculumScheduleData d : csData) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("\n课程：").append(d.name)
                            .append("\n教师：").append(d.teacher)
                            .append("\n地点：").append(d.location)
                            .append("\n时间：第").append(d.daytime).append("大课")
                            .append("\n");
                    tvInfo.append(builder.toString());
                }
                linearLayout.addView(v);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), CurriculumScheduleActivity.class));
                    }
                });
            }
        }
        //成绩查询
        if(LocalDataUtil.read(getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.PERMISSION).equals("学生")||
                LocalDataUtil.read(getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.PERMISSION).equals("家长")) {
            ResultsQueryDataUtil rqdu = new ResultsQueryDataUtil(getActivity(), ResultsQueryDataUtil.DB_RESULTS_QUERY, null, 0);
            List<ResultsQueryDataUtil.ResultsQueryData> rqData = rqdu.getResultsQueryDataList(ResultsQueryDataUtil.ORDER_DATE);

            if (rqData.size() != 0) {
                v = inflater.inflate(R.layout.item_home, linearLayout, false);
                tvTitle = v.findViewById(R.id.textView_home_title);
                tvInfo = v.findViewById(R.id.textView_home_info);

                tvTitle.setText("最近成绩");
                tvInfo.setText("");
                int i=0;
                for (ResultsQueryDataUtil.ResultsQueryData d : rqData) {
                    if(i>=3)break;
                    StringBuilder builder = new StringBuilder();
                    builder.append("\n课程：").append(d.name课程名)
                            .append("\n考试日期：").append(d.date考试日期)
                            .append("\n绩点：").append(d.gpa绩点)
                            .append("\n");
                    tvInfo.append(builder.toString());
                    i++;
                }
                linearLayout.addView(v);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), ResultsQueryActivity.class));
                    }
                });
            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public class HomeAsync extends AsyncTask<Void,Void,Integer> {


        /**
         *
         * @param param
         * @return 获取数据并返回错误代码，如果没错返回0
         */
        @Override
        protected Integer doInBackground(Void... param) {

            //CurriculumScheduleDataUtil csdu = new CurriculumScheduleDataUtil(getActivity(), CurriculumScheduleDataUtil.DB_CURRICULUM_SCHEDULE, null, 0);
            //csdu.achieveFromInternet(HomeFragment.this.getActivity());
            //ResultsQueryDataUtil rqdu = new ResultsQueryDataUtil(getActivity(), ResultsQueryDataUtil.DB_RESULTS_QUERY, null, 0);
            //rqdu.achieveFromInternet(HomeFragment.this.getActivity());

            SignDataUtil sdu=new SignDataUtil(HomeFragment.this.getActivity(),SignDataUtil.DB_SIGN, null,0);
            if(LocalDataUtil.read(getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.PERMISSION).equals("学生")||
                    LocalDataUtil.read(getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.PERMISSION).equals("家长")){
                return sdu.achieveFromInternet(HomeFragment.this.getActivity(),SignDataUtil.FUNCTION_签到查询,null,null);
            }else if(LocalDataUtil.read(getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.PERMISSION).equals("教师")){
                sdu.achieveFromInternet(HomeFragment.this.getActivity(),SignDataUtil.FUNCTION_未签到查询,null,null);
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result==SignDataUtil.NEED_TO_SIGN){
                needToSign=result;
            }else if(result==SignDataUtil.DO_NOT_NEED_TO_SIGN){
                needToSign=result;
            }
            setViews();
        }
    }
}
