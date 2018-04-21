package cn.edu.nuaa.jc.jcmicroschool;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import cn.edu.nuaa.jc.jcmicroschool.DataUtils.CurriculumScheduleDataUtil;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.LocalDataUtil;


public class CurriculumScheduleActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener,View.OnClickListener{

    public static final int TAG_ADD=1;
    public static final int TAG_NORMAL=0;
    private ImageView btBack;
    private Toolbar titleBar;
    private TextView tvWeek;
    private ImageView btLeft;
    private ImageView btRight;
    private ProgressBar progressBar;
    private GridLayout glCurriculumSchedule;
    private int week;
    private int currentWeek;
    private View[] views;

    private CurriculumScheduleDataUtil util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curriculum_schedule);

        views=new View[40];

        titleBar=(Toolbar)findViewById(R.id.toolbar_curriculum_schedule);
        titleBar.inflateMenu(R.menu.menu_curriculum_schedule);
        titleBar.setOnMenuItemClickListener(this);

        progressBar=(ProgressBar)findViewById(R.id.progressBar_curriculum_schedule);

        //返回键
        btBack=(ImageView)findViewById(R.id.back_curriculum_schedule);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        glCurriculumSchedule=(GridLayout)findViewById(R.id.gridLayout_curriculum_schedule);

        tvWeek=(TextView)findViewById(R.id.textView_curriculum_schedule_week);
        btLeft=(ImageView)findViewById(R.id.button_curriculum_schedule_left);
        btLeft.setOnClickListener(this);
        btRight=(ImageView)findViewById(R.id.button_curriculum_schedule_right);
        btRight.setOnClickListener(this);
        util=new CurriculumScheduleDataUtil(CurriculumScheduleActivity.this,CurriculumScheduleDataUtil.DB_CURRICULUM_SCHEDULE,null,0);
        new WeekSetupAsync().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_curriculum_schedule_refresh:
                refresh();
                break;
            case R.id.action_curriculum_schedule_current_week:
                week=currentWeek;
                refreshUI();
                break;
            case R.id.action_curriculum_schedule_add:
                Intent intent=new Intent(this,CourseSettingsActivity.class);
                intent.putExtra("weekday", 1);
                intent.putExtra("daytime", 1);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_curriculum_schedule_left:
                week--;
                if(week<=0)week=1;
                if(week>=30)week=30;
                refreshUI();
                break;
            case R.id.button_curriculum_schedule_right:
                week++;
                if(week<=0)week=1;
                if(week>=30)week=30;
                refreshUI();
                break;
            case R.id.textView_course:
                Intent intent;
                if(((int[])v.getTag())[2]==TAG_ADD){
                    intent =new Intent(this,CourseSettingsActivity.class);
                }else {
                    intent = new Intent(this, CourseDetailActivity.class);
                }
                intent.putExtra("weekday", ((int[]) v.getTag())[0]);
                intent.putExtra("daytime", ((int[]) v.getTag())[1]);
                startActivity(intent);
                break;
        }
    }

    /**
     * 刷新课程表
     */
    private void refresh(){
        new CurriculumScheduleAsync().execute();
    }

    private void refreshUI(){
        LayoutInflater inflater=LayoutInflater.from(CurriculumScheduleActivity.this);

        for(int i=1;i<=35;i++){
            if(views[i]!=null){
                glCurriculumSchedule.removeView(views[i]);
                views[i]=null;
            }
        }

        for(int daytime=1;daytime<=5;daytime++) {
            for(int weekday=1;weekday<=7;weekday++) {

                List<CurriculumScheduleDataUtil.CurriculumScheduleData> data=util.getCourseData(weekday,daytime);
                View v = inflater.inflate(R.layout.item_curriculum_schedule, glCurriculumSchedule, false);
                TextView tv=v.findViewById(R.id.textView_course);
                tv.setTag(new int[]{weekday,daytime,TAG_NORMAL});
                tv.setOnClickListener(this);
                if(data.size()==0){
                    tv.setText("");
                    tv.setTag(new int[]{weekday,daytime,TAG_ADD});
                    tv.setBackground(getDrawable(R.drawable.add));
                }else {
                    Boolean error=true;
                    for(CurriculumScheduleDataUtil.CurriculumScheduleData d:data){//寻找正好本星期有课的课
                        if(d.week[week]){
                            error=false;

                            tv.setText(d.name+(d.location.equals("")?"":" @"+d.location)+" "+d.teacher);
                            tv.setBackground(getDrawable(R.drawable.frame_single_curriculum_schedule_true));
                            break;
                        }
                    }
                    if(error){//寻找接下来星期有课的课
                        error=true;
                        outer:for(int i=week;i<30;i++){
                            for(CurriculumScheduleDataUtil.CurriculumScheduleData d:data){
                                if(d.week[i]){
                                    error=false;
                                    tv.setText(d.name+(d.location.equals("")?"":" @"+d.location)+" "+d.teacher);
                                    tv.setBackground(getDrawable(R.drawable.frame_single_curriculum_schedule_false));
                                    break outer;
                                }
                            }
                        }

                    }
                    if(error){
                        outer:for(int i=week-1;i>0;i--) {
                            for (CurriculumScheduleDataUtil.CurriculumScheduleData d : data) {
                                if (d.week[i]) {
                                    tv.setText(d.name+(d.location.equals("")?"":" @"+d.location)+" "+d.teacher);
                                    tv.setBackground(getDrawable(R.drawable.frame_single_curriculum_schedule_false));
                                    break outer;
                                }
                            }
                        }

                    }

                }
                GridLayout.Spec rowSpec = GridLayout.spec(daytime);
                GridLayout.Spec columnSpec = GridLayout.spec(weekday);
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
                layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
                //glCurriculumSchedule.removeViewAt(9);
                glCurriculumSchedule.addView(v,layoutParams);
                views[(daytime-1)*7+weekday]=v;
            }
        }
        if(week==currentWeek){
            tvWeek.setText("第"+week+"周（当前周）");
        }else {
            tvWeek.setText("第"+week+"周");
        }
    }



    class WeekSetupAsync extends AsyncTask<Void,Void,Integer>{
        @Override
        protected Integer doInBackground(Void... voids) {
            currentWeek=util.getCurrentWeek();
            if(currentWeek!=0){
            }else {
                currentWeek= Integer.parseInt(LocalDataUtil.read(CurriculumScheduleActivity.this,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.CURRENT_WEEK));
            }
            week=currentWeek;
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            tvWeek.setText("第"+week+"周（当前周）");
            refreshUI();
        }
    }



    class CurriculumScheduleAsync extends AsyncTask<Void,Void,Integer>{


        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * 从网络和本地获取数据
         * @param voids
         * @return
         */
        @Override
        protected Integer doInBackground(Void... voids) {
            return util.achieveFromInternet(CurriculumScheduleActivity.this);
        }

        /**
         * 将获得的数据进行加载
         * @param integer
         */
        @Override
        protected void onPostExecute(Integer integer) {
            progressBar.setVisibility(View.INVISIBLE);
            refreshUI();
        }


    }


}