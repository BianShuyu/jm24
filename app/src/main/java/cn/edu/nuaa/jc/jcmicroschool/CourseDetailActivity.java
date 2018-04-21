package cn.edu.nuaa.jc.jcmicroschool;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import cn.edu.nuaa.jc.jcmicroschool.DataUtils.CurriculumScheduleDataUtil;

public class CourseDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private Intent intent;
    private LinearLayout ll;
    private TextView tvCourseTime;
    private ProgressBar progressBar;
    private ImageView btBack;

    private CurriculumScheduleDataUtil util;
    private List<CurriculumScheduleDataUtil.CurriculumScheduleData> data;
    private int weekday;
    private int daytime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        ll=findViewById(R.id.linearLayout_course_detail);
        tvCourseTime=(TextView)findViewById(R.id.textView_course_time);
        progressBar=(ProgressBar)findViewById(R.id.progressBar_course_detail);
        btBack=(ImageView)findViewById(R.id.back_course_detail);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        intent=getIntent();
        weekday=intent.getIntExtra("weekday",0);
        daytime=intent.getIntExtra("daytime",0);
        tvCourseTime.setText(calcDayOfWeek(weekday)+" 第"+daytime+"大课");
        Log.i("weekday,daytime",weekday+","+daytime);

        util=new CurriculumScheduleDataUtil(this,CurriculumScheduleDataUtil.DB_CURRICULUM_SCHEDULE, null,0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new CourseDetailAsync().execute();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.linearLayout_course_add:
                intent=new Intent(this,CourseSettingsActivity.class);
                intent.putExtra("name","");
                intent.putExtra("teacher","");
                intent.putExtra("location","");
                intent.putExtra("weekday",weekday);
                intent.putExtra("daytime",daytime);
                startActivity(intent);
                break;
            case R.id.constraintLayout_item_results:
                intent=new Intent(this,CourseSettingsActivity.class);
                intent.putExtra("name",((CurriculumScheduleDataUtil.CurriculumScheduleData)v.getTag()).name);
                intent.putExtra("teacher",((CurriculumScheduleDataUtil.CurriculumScheduleData)v.getTag()).teacher);
                intent.putExtra("location",((CurriculumScheduleDataUtil.CurriculumScheduleData)v.getTag()).location);
                intent.putExtra("weekday",((CurriculumScheduleDataUtil.CurriculumScheduleData)v.getTag()).weekday);
                intent.putExtra("daytime",((CurriculumScheduleDataUtil.CurriculumScheduleData)v.getTag()).daytime);
                startActivity(intent);
                break;
        }
    }

    class CourseDetailAsync extends AsyncTask<Void,Void,Integer>{

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            data=null;
            data=util.getCourseData(weekday,daytime);
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            progressBar.setVisibility(View.INVISIBLE);
            ll.removeAllViews();
            LayoutInflater inflater=LayoutInflater.from(CourseDetailActivity.this);
            for(CurriculumScheduleDataUtil.CurriculumScheduleData d:data) {
                View v = inflater.inflate(R.layout.item_course_detail, ll, false);
                ConstraintLayout cl=v.findViewById(R.id.constraintLayout_item_results);
                cl.setTag(d);
                cl.setOnClickListener(CourseDetailActivity.this);
                TextView tvName = v.findViewById(R.id.textView_course_name);
                tvName.setText(d.name);
                TextView tvInfo=v.findViewById(R.id.textView_course_info);
                tvInfo.setText("地点："+d.location+"\n教师："+d.teacher+"\n上课周："+d.toString());
                ll.addView(v);
            }
            View add=inflater.inflate(R.layout.item_course_detail_add,ll,false);
            LinearLayout linearLayout=add.findViewById(R.id.linearLayout_course_add);
            linearLayout.setOnClickListener(CourseDetailActivity.this);
            ll.addView(add);
        }
    }


    private String calcDayOfWeek(int dayOfWeek){
        switch (dayOfWeek){
            case 1:
                return "星期一";
            case 2:
                return "星期二";
            case 3:
                return "星期三";
            case 4:
                return "星期四";
            case 5:
                return "星期五";
            case 6:
                return "星期六";
            case 7:
                return "星期日";
            default:
                return "";
        }
    }
}
