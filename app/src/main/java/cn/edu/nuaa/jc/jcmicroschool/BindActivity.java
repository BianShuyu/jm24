package cn.edu.nuaa.jc.jcmicroschool;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.edu.nuaa.jc.jcmicroschool.DataUtils.BindUtil;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.NetUtil;

public class BindActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView btBack;
    private EditText etUsername;
    private EditText etPassword;
    private Button btSave;
    private Toast toast;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);

        btBack=(ImageView)findViewById(R.id.back_bind);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        etUsername=(EditText)findViewById(R.id.editText_bind_username);
        etUsername.requestFocus();
        etPassword=(EditText)findViewById(R.id.editText_bind_password);
        btSave=(Button)findViewById(R.id.button_bind_save);
        btSave.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_bind_save:
                tryToSignIn();
                break;
        }
    }

    /**
     * 用于验证用户名密码是否输入
     * */
    private boolean validate(){
        username=etUsername.getText().toString();
        password=etPassword.getText().toString();

        if(username==null || username.equals("")){
            toast.cancel();
            toast=Toast.makeText(this,"请输入教务网账号！",Toast.LENGTH_SHORT);
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
            new SignAsync().execute();
        }
    }


    public class SignAsync extends AsyncTask<Void,Void,Integer> {
        @Override
        protected void onPreExecute() {
            btSave.setText("正在同步，请稍候...");
            btSave.setEnabled(false);
        }

        /**
         * 执行登录过程，返回错误代码，如果为OK则登录成功
         * 使用用户输入进行登录
         * @param params
         * @return
         */
        @Override
        protected Integer doInBackground(Void... params) {
            return BindUtil.bind(BindActivity.this,username,password);
        }

        @Override
        protected void onPostExecute(Integer result) {
            btSave.setText("开始同步");
            btSave.setEnabled(true);
            if(result== NetUtil.CODE_OK){

                if(toast!=null) toast.cancel();
                toast=Toast.makeText(BindActivity.this,"教务网数据同步成功！",Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        }
    }
}
