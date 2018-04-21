package cn.edu.nuaa.jc.jcmicroschool;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ResultsQuerySummaryActivity extends AppCompatActivity {


    private ImageView btBack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_query_summary);

        progressBar=(ProgressBar)findViewById(R.id.progressbar_results_query_summary);

        btBack=(ImageView)findViewById(R.id.back_results_query_summary);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }



    public class ResultsQueryAsync extends AsyncTask<Void,Void,Integer> {

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
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {

            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
