package cn.edu.nuaa.jc.jcmicroschool;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nuaa.jc.jcmicroschool.DataUtils.LocalDataUtil;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.NetUtil;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.SignDataUtil;

public class SignActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView btBack;
    private EditText etSign;
    private Button btConfirm;
    private ProgressBar progressBar;
    private LinearLayout ll;
    private GridLayout gridLayout;
    private Toast toast;
    private List<String> selectedClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        btBack=findViewById(R.id.back_sign);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        etSign=findViewById(R.id.editText_sign);
        btConfirm=findViewById(R.id.button_sign);
        btConfirm.setOnClickListener(this);
        progressBar=findViewById(R.id.progressBar_sign);
        ll=findViewById(R.id.linearLayout_sign);
        gridLayout=findViewById(R.id.gridLayout_sign_class);
        toast=Toast.makeText(this,"",Toast.LENGTH_SHORT);

        new GetClassesAsync().execute();
        selectedClasses=new ArrayList<>();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_sign:
                new SignAsync().execute();
                break;
            case R.id.textView_class:
                boolean e=true;
                for(int i=0;i<selectedClasses.size();i++){
                    String s=selectedClasses.get(i);
                    if (s.equals(v.getTag())){
                        v.setBackground(getDrawable(R.drawable.frame_grey));
                        e=false;
                        selectedClasses.remove(i);
                        break;
                    }
                }

                if(e){
                    v.setBackground(getDrawable(R.drawable.frame_orange));
                    selectedClasses.add((String) v.getTag());
                }
        }
    }

    public class GetClassesAsync extends AsyncTask<Void,Void,Integer> {

        public List<String> classes=new ArrayList<>();

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
            SignDataUtil sdu=new SignDataUtil(SignActivity.this,SignDataUtil.DB_SIGN, null,0);
            int t=sdu.achieveFromInternet(SignActivity.this,SignDataUtil.FUNCTION_可签到班级, null,null);
            classes=sdu.getClassesData();
            return t;
        }

        @Override
        protected void onPostExecute(Integer result) {

            LayoutInflater inflater=LayoutInflater.from(SignActivity.this);
            for(int i=0;i<classes.size();i++){
                View v=inflater.inflate(R.layout.item_sign_classes,gridLayout,false);

                GridLayout.Spec rowSpec = GridLayout.spec(i/3);
                GridLayout.Spec columnSpec = GridLayout.spec(i%3);
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
                layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
                gridLayout.addView(v,layoutParams);
                TextView textView=v.findViewById(R.id.textView_class);
                textView.setText(classes.get(i));
                textView.setTag(classes.get(i));
                textView.setOnClickListener(SignActivity.this);
            }

            progressBar.setVisibility(View.INVISIBLE);

        }
    }

    public class SignAsync extends AsyncTask<Void,Void,Integer> {

        public List<String> classes=new ArrayList<>();
        private String password=null;
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            btConfirm.setText("正在发送签到请求...");
            btConfirm.setEnabled(false);
            password=etSign.getText().toString();
        }

        /**
         *
         * @param param
         * @return 获取数据并返回错误代码，如果没错返回0
         */
        @Override
        protected Integer doInBackground(Void... param) {
            SignDataUtil sdu=new SignDataUtil(SignActivity.this,SignDataUtil.DB_SIGN, null,0);
            return sdu.achieveFromInternet(SignActivity.this,SignDataUtil.FUNCTION_班级签到,password,selectedClasses);
        }

        @Override
        protected void onPostExecute(Integer result) {
            btConfirm.setText("开始签到");
            btConfirm.setEnabled(true);
            if(result== NetUtil.CODE_OK){
                if(toast!=null) toast.cancel();
                toast= Toast.makeText(SignActivity.this,"签到请求发送成功！",Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
            progressBar.setVisibility(View.INVISIBLE);

        }
    }
}
