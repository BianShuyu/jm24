package cn.edu.nuaa.jc.jcmicroschool;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import cn.edu.nuaa.jc.jcmicroschool.DataUtils.LocalDataUtil;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.LoginUtil;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.NetUtil;

public class WelcomeActivity extends AppCompatActivity {

    private Handler handler;
    private final int GO_AHEAD=0;
    private final int CONNECTION_FAILED=1;
    private final int OK=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case GO_AHEAD:
                        startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                        startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                        finish();
                        break;
                    case CONNECTION_FAILED:
                        Toast.makeText(WelcomeActivity.this,"网络连接失败，请检查网络！",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                        finish();
                        break;
                    default:
                        startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                        finish();
                }
            }
        };

        new SignInAsync().execute();

    }

    public class SignInAsync extends AsyncTask<Void,Void,Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            LocalDataUtil.init(WelcomeActivity.this);
            int code= LoginUtil.login(WelcomeActivity.this);
            if(code==LoginUtil.OK ){
                return OK;
            }else if(code==LoginUtil.CONNECTION_ERROR || !NetUtil.isNetworkConnected(WelcomeActivity.this)){
                return CONNECTION_FAILED;
            }else {
                return GO_AHEAD;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result==OK){
                handler.sendEmptyMessageDelayed(OK,500);
            }else if(result==CONNECTION_FAILED){
                handler.sendEmptyMessageDelayed(CONNECTION_FAILED,500);
            }else {
                handler.sendEmptyMessageDelayed(GO_AHEAD,500);
            }
        }
    }
}
