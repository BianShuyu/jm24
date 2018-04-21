package cn.edu.nuaa.jc.jcmicroschool;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;

import cn.edu.nuaa.jc.jcmicroschool.Adapters.SchoolBusAdapter;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.SchoolBusDataUtil;

public class SchoolBusActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener{

    private DatePickerDialog datePickerDialog;
    private TextView btSchoolBusDateSet;
    private RecyclerView rvSchoolBus;
    private ProgressBar progressBar;
    private ImageView btBack;
    private Toolbar titleBar;

    private SchoolBusAdapter schoolBusAdapter;
    private SchoolBusDataUtil schoolBusDataUtil;
    private int year;
    private int month;
    private int day;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_bus);

        //返回键
        btBack=(ImageView)findViewById(R.id.back_school_bus);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //菜单初始化
        titleBar =(Toolbar)findViewById(R.id.toolbar_school_bus);
        titleBar.inflateMenu(R.menu.menu_school_bus);
        titleBar.setOnMenuItemClickListener(this);


        //RecyclerView初始化
        rvSchoolBus=(RecyclerView)findViewById(R.id.recyclerview_school_bus);
        rvSchoolBus.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvSchoolBus.setLayoutManager(layoutManager);
        schoolBusAdapter=new SchoolBusAdapter();
        rvSchoolBus.setAdapter(schoolBusAdapter);

        btSchoolBusDateSet=(TextView) findViewById(R.id.button_school_bus_date_setup);
        btSchoolBusDateSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        //日期调整
        final Calendar calendar=Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH)+1;
        day=calendar.get(Calendar.DAY_OF_MONTH);
        btSchoolBusDateSet.setText(year+"年 "+month+"月 "+day+"日 "+calcDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
        datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                year=i;
                month=i1+1;
                day=i2;
                calendar.set(year,month-1,day);
                btSchoolBusDateSet.setText(year+"年 "+month+"月 "+day+"日 "+calcDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
                Log.i("date",""+year+month+day+"");
                refresh();
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        //todo 使用datePickerDialog.getDatePicker().setMaxDate();设置时间戳

        progressBar=(ProgressBar)findViewById(R.id.progressbar_school_bus);

        refresh();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_school_bus_refresh:
                refresh();
                break;
            case R.id.action_school_bus_change_to_today:
                Calendar calendar=Calendar.getInstance();
                year=calendar.get(Calendar.YEAR);
                month=calendar.get(Calendar.MONTH)+1;
                day=calendar.get(Calendar.DAY_OF_MONTH);
                btSchoolBusDateSet.setText(year+"年 "+month+"月 "+day+"日 "+calcDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
                refresh();
                break;
        }
        return true;
    }

    private void refresh(){
        new SchoolBusAsync().execute();
    }

    public class SchoolBusAsync extends AsyncTask<Void,Void,Integer>{


        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            schoolBusDataUtil=new SchoolBusDataUtil();

            return schoolBusDataUtil.achieveFromInternet(SchoolBusActivity.this);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            schoolBusAdapter.setDataList(schoolBusDataUtil.getSchoolBusDataList());
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 处理从calendar获取1-7的星期几的数据为中文
     * @param dayOfWeek
     * @return
     */
    private String calcDayOfWeek(int dayOfWeek){
        switch (dayOfWeek){
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }
}
