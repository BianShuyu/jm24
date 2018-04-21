package cn.edu.nuaa.jc.jcmicroschool;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import cn.edu.nuaa.jc.jcmicroschool.Adapters.ResultsQueryAdapter;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.LocalDataUtil;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.ResultsQueryDataUtil;


public class ResultsQueryActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener{

    //组件定义
    private RecyclerView rvResultsQuery;
    private ProgressBar progressBar;
    private Toolbar titlebar;
    private ImageView btBack;
    private TextView tvGPA;
    //数据定义
    private ResultsQueryAdapter resultsQueryAdapter;
    private ResultsQueryDataUtil resultsQueryDataUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_query);

        rvResultsQuery=findViewById(R.id.recyclerview_results_query);
        rvResultsQuery.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        rvResultsQuery.setLayoutManager(layoutManager);
        resultsQueryAdapter=new ResultsQueryAdapter();
        rvResultsQuery.setAdapter(resultsQueryAdapter);

        progressBar=findViewById(R.id.progressbar_results_query);
        btBack=findViewById(R.id.back_results_query);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        titlebar=findViewById(R.id.toolbar_results_query);
        titlebar.inflateMenu(R.menu.menu_results_query);
        titlebar.setOnMenuItemClickListener(this);

        tvGPA=findViewById(R.id.textView_results_query_GPA);

        refresh(ResultsQueryDataUtil.ORDER_DATE);
    }



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_results_query_refresh:
                refresh(ResultsQueryDataUtil.ORDER_DATE);
                break;
            case R.id.action_results_query_sort_default:
                refresh(ResultsQueryDataUtil.ORDER_DATE);
                break;
            case R.id.action_results_query_sort_name:
                refresh(ResultsQueryDataUtil.ORDER_NAME);
                break;
            case R.id.action_results_query_sort_gpa:
                refresh(ResultsQueryDataUtil.ORDER_GPA);
                break;
            case R.id.action_results_query_summary:
                startActivity(new Intent(ResultsQueryActivity.this,ResultsQuerySummaryActivity.class));
                break;
        }
        return true;
    }

    private void refresh(int order) {
        ResultsQueryAsync async=new ResultsQueryAsync();
        async.order=order;
        async.execute();
    }

    public class ResultsQueryAsync extends AsyncTask<Void,Void,Integer>{

        public int order;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         *
         * @param param
         * @return 获取数据并返回错误代码，如果没错返回0
         */
        @Override
        protected Integer doInBackground(Void... param) {
            resultsQueryDataUtil=new ResultsQueryDataUtil(ResultsQueryActivity.this,ResultsQueryDataUtil.DB_RESULTS_QUERY, null,0);

            return resultsQueryDataUtil.achieveFromInternet(ResultsQueryActivity.this);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            resultsQueryAdapter.setDataList(resultsQueryDataUtil.getResultsQueryDataList(order));
            progressBar.setVisibility(View.INVISIBLE);
            double GPA= Double.parseDouble(LocalDataUtil.read(ResultsQueryActivity.this,LocalDataUtil.DATABASE_RESULTS_SUMMARY, LocalDataUtil.GPA));
            if(GPA>=5.0){
                tvGPA.setText("GPA："+GPA+"\n你已经超神了！你在金城学院前无古人，后无来者！！");
            }
            else if(GPA>=4.0){
                tvGPA.setText("GPA："+GPA+" 非常优秀！");
            }else if(GPA>=3.5){
                tvGPA.setText("GPA："+GPA+" 继续努力！");
            }else if(GPA>=2.0){
                tvGPA.setText("GPA："+GPA);
            }else if(GPA>=0.1){
                tvGPA.setText("GPA："+GPA+" 加把劲！");
            }else {
                tvGPA.setText("GPA：无");
            }
        }
    }
}

