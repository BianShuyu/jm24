package cn.edu.nuaa.jc.jcmicroschool;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.edu.nuaa.jc.jcmicroschool.DataUtils.LocalDataUtil;
import cn.edu.nuaa.jc.jcmicroschool.Fragments.HomeFragment;
import cn.edu.nuaa.jc.jcmicroschool.Fragments.PersonalCenterFragment;
import cn.edu.nuaa.jc.jcmicroschool.Fragments.ToolboxFragment;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener{
    private static final String TAG = "MainActivity";

    private ToolboxFragment toolboxFragment;
    private HomeFragment homeFragment;
    private PersonalCenterFragment personalCenterFragment;
    private ImageView btToolbox;
    private ImageView btHome;
    private ImageView btPersonalCenter;
    private TextView tvNickname;
    private Toolbar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvNickname=(TextView)findViewById(R.id.textView_nickname);

        titleBar=(Toolbar)findViewById(R.id.toolbar_main);
        titleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LocalDataUtil.read(MainActivity.this,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.LOGIN_STATUS).equals("no")){
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                }
            }
        });

        btToolbox=(ImageView) findViewById(R.id.button_toolbox);
        btToolbox.setOnClickListener(this);

        btHome=(ImageView)findViewById(R.id.button_home);
        btHome.setOnClickListener(this);

        btPersonalCenter=(ImageView)findViewById(R.id.button_personal_center);
        btPersonalCenter.setOnClickListener(this);

        homeFragment=new HomeFragment();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container,homeFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(LocalDataUtil.read(MainActivity.this,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.LOGIN_STATUS).equals("no")){
            tvNickname.setText("未登录");
        }else {
            tvNickname.setText(LocalDataUtil.read(MainActivity.this,LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.NICKNAME));
        }
        Log.i(TAG,"Resume!");
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        hideAllFragments(fragmentTransaction);
        resetButtonColor();
        switch (view.getId()){
            case R.id.button_toolbox:
                ((ImageView)view).setImageDrawable(getResources().getDrawable(R.drawable.toolbox_on));
                if(toolboxFragment==null){
                    toolboxFragment=new ToolboxFragment();
                    fragmentTransaction.add(R.id.fragment_container,toolboxFragment);
                }else{
                    fragmentTransaction.show(toolboxFragment);
                }
                break;

            case R.id.button_home:
                ((ImageView)view).setImageDrawable(getResources().getDrawable(R.drawable.home_on));
                if(homeFragment==null){
                    homeFragment=new HomeFragment();
                    fragmentTransaction.add(R.id.fragment_container,homeFragment);
                }else {
                    fragmentTransaction.show(homeFragment);
                }
                break;

            case R.id.button_personal_center:
                ((ImageView)view).setImageDrawable(getResources().getDrawable(R.drawable.settings_on));
                if(personalCenterFragment==null){
                    personalCenterFragment=new PersonalCenterFragment();
                    fragmentTransaction.add(R.id.fragment_container,personalCenterFragment);
                }else {
                    fragmentTransaction.show(personalCenterFragment);
                }
                break;
        }
        fragmentTransaction.commit();//不能忘记！！！

    }

    private void hideAllFragments(FragmentTransaction fragmentTransaction){
        if(toolboxFragment!=null) fragmentTransaction.hide(toolboxFragment);
        if(homeFragment!=null) fragmentTransaction.hide(homeFragment);
        if(personalCenterFragment!=null)fragmentTransaction.hide(personalCenterFragment);
    }

    private void resetButtonColor(){
        btPersonalCenter.setImageDrawable(getResources().getDrawable(R.drawable.settings_off));
        btHome.setImageDrawable(getResources().getDrawable(R.drawable.home_off));
        btToolbox.setImageDrawable(getResources().getDrawable(R.drawable.toolbox_off));
    }
}
