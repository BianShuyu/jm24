package cn.edu.nuaa.jc.jcmicroschool;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import cn.edu.nuaa.jc.jcmicroschool.DataUtils.CurriculumScheduleDataUtil;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.LoginUtil;

public class LoginActivity extends AppCompatActivity {

    private ImageView ivVerification;
    private ProgressBar pbLogin;
    private EditText etUsername;
    private EditText etPassword;
    private Button btSignIn;
    private ImageView btBack;
    private Toast toast;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btBack=(ImageView) findViewById(R.id.back_login);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivVerification=(ImageView)findViewById(R.id.imageView_verification);
        ivVerification.setBackgroundResource(R.mipmap.ic_launcher);

        pbLogin=(ProgressBar)findViewById(R.id.progressBar_login);
        pbLogin.setVisibility(View.INVISIBLE);

        toast=Toast.makeText(this,"",Toast.LENGTH_SHORT);

        etUsername=(EditText) findViewById(R.id.editText_username);
        etPassword=(EditText) findViewById(R.id.editText_password);

        btSignIn=(Button)findViewById(R.id.button_sign_in);
        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryToSignIn();
            }
        });

        etUsername.requestFocus();


    }
    /**
     * 用于验证用户名密码是否输入
     * */
    private boolean validate(){
        username=etUsername.getText().toString();
        password=etPassword.getText().toString();

        if(username==null || username.equals("")){
            toast.cancel();
            toast=Toast.makeText(this,"请输入学号或手机号！",Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }else if(password==null || password.equals("")){
            toast.cancel();
            toast=Toast.makeText(this,"请输入密码！",Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    /**
     * 尝试登录
     */
    private void tryToSignIn(){
        if(validate()){//用户名密码输入了
            new SignInAsync().execute();
        }
    }


    public class SignInAsync extends AsyncTask<Void,Void,Integer> {
        @Override
        protected void onPreExecute() {
            btSignIn.setText("正在登录...");
            btSignIn.setEnabled(false);
        }

        /**
         * 执行登录过程，返回错误代码，如果为OK则登录成功
         * 使用用户输入进行登录
         * @param params
         * @return
         */
        @Override
        protected Integer doInBackground(Void... params) {
            new CurriculumScheduleDataUtil(LoginActivity.this,CurriculumScheduleDataUtil.DB_CURRICULUM_SCHEDULE, null,0)
                    .getCurrentWeek();
            return LoginUtil.login(username,password,LoginActivity.this);
        }

        @Override
        protected void onPostExecute(Integer result) {
            btSignIn.setText("登录！");
            btSignIn.setEnabled(true);
            if(result==LoginUtil.OK){


                toast.cancel();
                toast=Toast.makeText(LoginActivity.this,"登录成功！",Toast.LENGTH_SHORT);
                toast.show();
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            } else if(result==LoginUtil.USERNAME_OR_PASSWORD_ERROR){

                toast.cancel();
                toast=Toast.makeText(LoginActivity.this,"用户名或密码错误！",Toast.LENGTH_SHORT);
                toast.show();
            }else if(result==LoginUtil.CONNECTION_ERROR){

                toast.cancel();
                toast=Toast.makeText(LoginActivity.this,"网络连接失败！",Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}










