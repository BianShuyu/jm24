package cn.edu.nuaa.jc.jcmicroschool;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.edu.nuaa.jc.jcmicroschool.DataUtils.CurriculumScheduleDataUtil;

public class CourseSettingsActivity extends AppCompatActivity implements View.OnClickListener,PopupMenu.OnMenuItemClickListener{

    private ImageView btBack;
    private EditText etName;
    private EditText etTeacher;
    private EditText etLocation;
    private EditText etWeekday;
    private EditText etDaytime;
    private PopupMenu popupMenu;
    private Button btSave;
    private Button btDelete;
    private GridLayout gridLayoutWeek;
    private CurriculumScheduleDataUtil util;
    private CurriculumScheduleDataUtil.CurriculumScheduleData scheduleDataOriginal;
    private CurriculumScheduleDataUtil.CurriculumScheduleData scheduleDataNew;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_settings);

        btBack=(ImageView)findViewById(R.id.back_course_settings);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etName=(EditText)findViewById(R.id.editText_course_name);
        etTeacher=(EditText)findViewById(R.id.editText_course_teacher);
        etLocation=(EditText)findViewById(R.id.editText_course_location);
        etWeekday=(EditText)findViewById(R.id.editText_course_weekday);
        etWeekday.setOnClickListener(this);
        etDaytime=(EditText)findViewById(R.id.editText_course_daytime);
        etDaytime.setOnClickListener(this);
        btSave=(Button)findViewById(R.id.button_course_save);
        btSave.setOnClickListener(this);
        btDelete=(Button)findViewById(R.id.button_course_delete);
        btDelete.setOnClickListener(this);

        Intent intent=getIntent();

        util=new CurriculumScheduleDataUtil(this,CurriculumScheduleDataUtil.DB_CURRICULUM_SCHEDULE, null,0);
        scheduleDataOriginal=util.getEmptyCurriculumScheduleData();
        scheduleDataNew=util.getEmptyCurriculumScheduleData();
        scheduleDataOriginal.name=intent.getStringExtra("name");
        scheduleDataOriginal.teacher=intent.getStringExtra("teacher");
        scheduleDataOriginal.location=intent.getStringExtra("location");
        scheduleDataNew.weekday=scheduleDataOriginal.weekday=intent.getIntExtra("weekday",0);
        scheduleDataNew.daytime=scheduleDataOriginal.daytime=intent.getIntExtra("daytime",0);

        List<CurriculumScheduleDataUtil.CurriculumScheduleData> list=util.getCourseData(scheduleDataOriginal.weekday,scheduleDataOriginal.daytime);
        for(CurriculumScheduleDataUtil.CurriculumScheduleData d:list){
            if(d.name.equals(scheduleDataOriginal.name) && d.teacher.equals(scheduleDataOriginal.teacher) && d.location.equals(scheduleDataOriginal.location)){
                scheduleDataOriginal=d;
                break;
            }
        }

        gridLayoutWeek=(GridLayout)findViewById(R.id.gridLayout_course_week);
        LayoutInflater inflater=LayoutInflater.from(this);
        for(int i=1;i<=30;i++){
            View v=inflater.inflate(R.layout.item_course_week,gridLayoutWeek,false);

            GridLayout.Spec rowSpec = GridLayout.spec((i-1)/5);
            GridLayout.Spec columnSpec = GridLayout.spec((i-1)%5);
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
            gridLayoutWeek.addView(v,layoutParams);
            TextView textView=v.findViewById(R.id.textView_course_week);
            textView.setText("第"+i+"周");
            if(scheduleDataOriginal.week[i]) {
                scheduleDataNew.week[i]=true;
                textView.setBackground(getDrawable(R.drawable.frame_orange));
            }else {
                scheduleDataNew.week[i]=false;
                textView.setBackground(getDrawable(R.drawable.frame_grey));
            }
            textView.setTag(i);
            textView.setOnClickListener(this);
        }

        etName.setText(scheduleDataOriginal.name);
        etTeacher.setText(scheduleDataOriginal.teacher);
        etLocation.setText(scheduleDataOriginal.location);
        etWeekday.setText(calcWeekday(scheduleDataOriginal.weekday));
        etDaytime.setText("第"+scheduleDataOriginal.daytime+"大课");
        if(scheduleDataOriginal.name==null || scheduleDataOriginal.name.equals("")){
            btDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editText_course_weekday:
                popupMenu=new PopupMenu(this,etWeekday);
                popupMenu.getMenuInflater().inflate(R.menu.menu_weekday,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
            case R.id.editText_course_daytime:
                popupMenu=new PopupMenu(this,etDaytime);
                popupMenu.getMenuInflater().inflate(R.menu.menu_daytime,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;

            case R.id.textView_course_week:
                scheduleDataNew.week[(int)v.getTag()]=!scheduleDataNew.week[(int)v.getTag()];
                if(scheduleDataNew.week[(int)v.getTag()]){
                    v.setBackground(getDrawable(R.drawable.frame_orange));
                }else {
                    v.setBackground(getDrawable(R.drawable.frame_grey));
                }
                Log.i("Week",((int)v.getTag())+"");
                break;

            case R.id.button_course_save:
                Boolean weekEmpty=true;
                for(int i=1;i<=30;i++){
                    if(scheduleDataNew.week[i]) {
                        weekEmpty = false;
                        break;
                    }
                }
                if(etName.getText().toString().equals("")){
                    Toast.makeText(this,"课程名不能为空！",Toast.LENGTH_SHORT).show();
                }else if(weekEmpty){
                    Toast.makeText(this,"上课周不能为空！",Toast.LENGTH_SHORT).show();
                }else {
                    save();
                    Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                break;
            case R.id.button_course_delete:
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("确认删除？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete();
                                onBackPressed();
                            }
                        })
                        .setNegativeButton("取消",null);
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_weekday_1:
                etWeekday.setText("星期一");
                scheduleDataNew.weekday=1;
                break;
            case R.id.action_weekday_2:
                etWeekday.setText("星期二");
                scheduleDataNew.weekday=2;
                break;
            case R.id.action_weekday_3:
                etWeekday.setText("星期三");
                scheduleDataNew.weekday=3;
                break;
            case R.id.action_weekday_4:
                etWeekday.setText("星期四");
                scheduleDataNew.weekday=4;
                break;
            case R.id.action_weekday_5:
                etWeekday.setText("星期五");
                scheduleDataNew.weekday=5;
                break;
            case R.id.action_weekday_6:
                etWeekday.setText("星期六");
                scheduleDataNew.weekday=6;
                break;
            case R.id.action_weekday_7:
                etWeekday.setText("星期日");
                scheduleDataNew.weekday=7;
                break;

            case R.id.action_daytime_1:
                etDaytime.setText("第1大课");
                scheduleDataNew.daytime=1;
                break;
            case R.id.action_daytime_2:
                etDaytime.setText("第2大课");
                scheduleDataNew.daytime=2;
                break;
            case R.id.action_daytime_3:
                etDaytime.setText("第3大课");
                scheduleDataNew.daytime=3;
                break;
            case R.id.action_daytime_4:
                etDaytime.setText("第4大课");
                scheduleDataNew.daytime=4;
                break;
            case R.id.action_daytime_5:
                etDaytime.setText("第5大课");
                scheduleDataNew.daytime=5;
                break;
        }
        return true;
    }

    private void save(){
        scheduleDataNew.name=etName.getText().toString();
        scheduleDataNew.teacher=etTeacher.getText().toString();
        scheduleDataNew.location=etLocation.getText().toString();
        //weekday和daytime已经在选下拉列表时设置好

        util.save(scheduleDataOriginal,scheduleDataNew);

    }

    private void delete(){
        util.delete(scheduleDataOriginal);
    }

    private String calcWeekday(int weekday){
        switch (weekday){
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
        }
        return "";
    }

}
