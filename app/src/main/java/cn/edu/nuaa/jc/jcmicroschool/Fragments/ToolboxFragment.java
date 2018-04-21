package cn.edu.nuaa.jc.jcmicroschool.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.edu.nuaa.jc.jcmicroschool.CurriculumScheduleActivity;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.LocalDataUtil;
import cn.edu.nuaa.jc.jcmicroschool.DataUtils.SignDataUtil;
import cn.edu.nuaa.jc.jcmicroschool.R;
import cn.edu.nuaa.jc.jcmicroschool.ResultsQueryActivity;
import cn.edu.nuaa.jc.jcmicroschool.SchoolBusActivity;
import cn.edu.nuaa.jc.jcmicroschool.SignActivity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * Use the {@link ToolboxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToolboxFragment extends Fragment implements View.OnClickListener{

    private LinearLayout btResultsQuery;
    private LinearLayout btSchoolBus;
    private LinearLayout btCurriculumSchedule;
    private LinearLayout btJcumbrella;
    private LinearLayout btSign;
    private AlertDialog.Builder builder;
    private TextView btOk;
    private TextView btCancal;
    private EditText etSign;
    private AlertDialog dialog;
    private Toast toast;
    private GetSignInfoAsync signInfoAsync;
    private SignAsync signAsync;
    public ToolboxFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btResultsQuery=getActivity().findViewById(R.id.button_results_query);
        btResultsQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ResultsQueryActivity.class));
            }
        });
        btSchoolBus=getActivity().findViewById(R.id.button_school_bus);
        btSchoolBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SchoolBusActivity.class));
            }
        });
        btCurriculumSchedule =getActivity().findViewById(R.id.button_curriculum_schedule);
        btCurriculumSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CurriculumScheduleActivity.class));
            }
        });
        btJcumbrella=getActivity().findViewById(R.id.button_jcumbrella);
        btJcumbrella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=getActivity().getPackageManager().getLaunchIntentForPackage("cn.edu.nuaa.jc.jcumbrella");
                if(intent!=null){
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(),"尚未安装JCUmbrella！",Toast.LENGTH_SHORT).show();
                }
            }
        });
        toast=Toast.makeText(getActivity(),"",Toast.LENGTH_SHORT);
        btSign=getActivity().findViewById(R.id.button_sign);
        btSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LocalDataUtil.read(ToolboxFragment.this.getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.PERMISSION).equals("学生")) {
                    if(signInfoAsync==null || signInfoAsync.getStatus().equals(AsyncTask.Status.FINISHED)) {
                        signInfoAsync = new GetSignInfoAsync();
                        signInfoAsync.execute();
                    }
                }else if(LocalDataUtil.read(ToolboxFragment.this.getActivity(),LocalDataUtil.DATABASE_USERDATA,LocalDataUtil.PERMISSION).equals("教师")){
                    startActivity(new Intent(ToolboxFragment.this.getActivity(), SignActivity.class));
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_dialog_sign_ok:
                if(signAsync==null || signAsync.getStatus().equals(AsyncTask.Status.FINISHED)) {
                    signAsync = new SignAsync();
                    signAsync.password=etSign.getText().toString();
                    signAsync.execute();
                }
                dialog.hide();
                break;
            case R.id.button_dialog_sign_cancel:
                dialog.hide();
                break;
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ToolboxFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToolboxFragment newInstance(String param1, String param2) {
        ToolboxFragment fragment = new ToolboxFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_toolbox, container, false);
    }

    public class GetSignInfoAsync extends AsyncTask<Void,Void,Integer> {



        /**
         *
         * @param param
         * @return 获取数据并返回错误代码，如果没错返回0
         */
        @Override
        protected Integer doInBackground(Void... param) {
            SignDataUtil signDataUtil=new SignDataUtil(ToolboxFragment.this.getActivity(),SignDataUtil.DB_SIGN, null,0);

            return signDataUtil.achieveFromInternet(ToolboxFragment.this.getActivity(),SignDataUtil.FUNCTION_签到查询,null,null);
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result==SignDataUtil.DO_NOT_NEED_TO_SIGN){
                toast.cancel();
                toast=Toast.makeText(ToolboxFragment.this.getActivity(),"不需要签到！",Toast.LENGTH_SHORT);
                toast.show();
            }else if(result==SignDataUtil.NEED_TO_SIGN){
                builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_sign, null, false);
                etSign = view.findViewById(R.id.editText_dialog_sign);
                btOk = view.findViewById(R.id.button_dialog_sign_ok);
                btCancal = view.findViewById(R.id.button_dialog_sign_cancel);
                btOk.setOnClickListener(ToolboxFragment.this);
                btCancal.setOnClickListener(ToolboxFragment.this);
                builder.setView(view);
                dialog = builder.create();
                dialog.show();
            }
        }
    }
    public class SignAsync extends AsyncTask<Void,Void,Integer> {

        public String password=null;

        /**
         *
         * @param param
         * @return 获取数据并返回结果，结果取值在SignDataUtil中。
         */
        @Override
        protected Integer doInBackground(Void... param) {
            SignDataUtil signDataUtil=new SignDataUtil(ToolboxFragment.this.getActivity(),SignDataUtil.DB_SIGN, null,0);

            return signDataUtil.achieveFromInternet(ToolboxFragment.this.getActivity(),SignDataUtil.FUNCTION_发送口令,password,null);
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result==SignDataUtil.ANSWER_CORRECT){
                toast.cancel();
                toast=Toast.makeText(ToolboxFragment.this.getActivity(),"签到成功！",Toast.LENGTH_SHORT);
                toast.show();
            }else if(result==SignDataUtil.ANSWER_WRONG ){
                toast.cancel();
                toast=Toast.makeText(ToolboxFragment.this.getActivity(),"签到失败！口令不正确！",Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

}
