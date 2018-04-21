package cn.edu.nuaa.jc.jcmicroschool.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import cn.edu.nuaa.jc.jcmicroschool.AboutActivity;
import cn.edu.nuaa.jc.jcmicroschool.BindActivity;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.LocalDataUtil;
import cn.edu.nuaa.jc.jcmicroschool.LoginActivity;
import cn.edu.nuaa.jc.jcmicroschool.MainActivity;
import cn.edu.nuaa.jc.jcmicroschool.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalCenterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalCenterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private LinearLayout btAbout;
    private LinearLayout btSignOut;
    private TextView tvSignOut;
    private LinearLayout btBind;
    private Switch swtNotification;
    private TextView tvUsername;
    private TextView tvPermission;

    public PersonalCenterFragment() {
        // Required empty public constructor
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btAbout=(LinearLayout)getActivity().findViewById(R.id.button_about);
        btAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AboutActivity.class));
            }
        });

        btSignOut=getActivity().findViewById(R.id.button_sign_out);
        btSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDataUtil.write(getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.USERNAME,"");
                LocalDataUtil.write(getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.PASSWORD,"");
                LocalDataUtil.write(getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.TEMP_PASSWORD,"");
                LocalDataUtil.write(getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.LOGIN_STATUS,"no");
                startActivity(new Intent(getActivity(),LoginActivity.class));
            }
        });

        tvSignOut=getActivity().findViewById(R.id.textView_sign_out);

        btBind=getActivity().findViewById(R.id.button_bind);
        btBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BindActivity.class));
            }
        });

        swtNotification=getActivity().findViewById(R.id.switch_notification);
        swtNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = isChecked?"yes":"no";
                LocalDataUtil.write(getActivity(),LocalDataUtil.DATABASE_USERDATA,"notification",value);
            }
        });

        tvUsername=getActivity().findViewById(R.id.textView_center_username);
        tvPermission=getActivity().findViewById(R.id.textView_center_permission);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(LocalDataUtil.read(getActivity(),LocalDataUtil.DATABASE_USERDATA,"notification").equals("no")){
            swtNotification.setChecked(false);
        } else {
            swtNotification.setChecked(true);
        }

        if(LocalDataUtil.read(getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.LOGIN_STATUS).equals("no")){
            tvUsername.setText("未登录");
            tvPermission.setText("");
            tvSignOut.setText("登录");
        }else {
            tvUsername.setText("用户名："+LocalDataUtil.read(getActivity(),LocalDataUtil.DATABASE_USERDATA, LocalDataUtil.USERNAME));
            tvPermission.setText("权限："+LocalDataUtil.read(getActivity(),LocalDataUtil.DATABASE_USERDATA, LocalDataUtil.PERMISSION));
            tvSignOut.setText("退出账号");
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonalCenterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalCenterFragment newInstance(String param1, String param2) {
        PersonalCenterFragment fragment = new PersonalCenterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_center, container, false);
    }

}
