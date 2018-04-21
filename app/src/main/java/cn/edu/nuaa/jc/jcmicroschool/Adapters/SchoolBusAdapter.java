package cn.edu.nuaa.jc.jcmicroschool.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.List;

import cn.edu.nuaa.jc.jcmicroschool.DataUtils.SchoolBusDataUtil;
import cn.edu.nuaa.jc.jcmicroschool.R;

/**
 * Created by bsy on 2017/9/24.
 */

public class SchoolBusAdapter extends RecyclerView.Adapter<SchoolBusAdapter.SchoolBusViewHolder> {

    private List<SchoolBusDataUtil.SchoolBusData> dataList;

    private Context context;

    public void setDataList(List<SchoolBusDataUtil.SchoolBusData> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(dataList!=null){
            return dataList.size();
        }
        return 0;
    }

    @Override
    public SchoolBusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.item_school_bus,parent,false);
        return new SchoolBusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SchoolBusViewHolder holder, int position) {
        SchoolBusDataUtil.SchoolBusData data=dataList.get(position);
        holder.tvBusRoute.setText(data.getRoute());
        Log.i("data",data.getRoute());
        int i=0;
        for (i=0;i<data.getDaytime().size();i++){
            String time=data.getDaytime().get(i);
            TextView textView=new TextView(context);
            textView.setText(time);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);//sp为单位，24sp


            GridLayout.Spec rowSpec = GridLayout.spec(i/4);
            GridLayout.Spec columnSpec = GridLayout.spec(i%4,1f);
            GridLayout.LayoutParams layoutParams=new GridLayout.LayoutParams(rowSpec,columnSpec);
            layoutParams.height=GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.width=0;

            holder.glBusTimetable.addView(textView,layoutParams);
            Log.i("data",time);
        }

        if(i<4){
            for (;i<4;i++){
                TextView textView=new TextView(context);
                textView.setText("");
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);//sp为单位，24sp

                GridLayout.Spec rowSpec = GridLayout.spec(i/4);
                GridLayout.Spec columnSpec = GridLayout.spec(i%4,1f);
                GridLayout.LayoutParams layoutParams=new GridLayout.LayoutParams(rowSpec,columnSpec);
                layoutParams.height=GridLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.width=0;

                holder.glBusTimetable.addView(textView,layoutParams);
            }
        }

    }

    public class SchoolBusViewHolder extends RecyclerView.ViewHolder{

        public TextView tvBusRoute;
        public GridLayout glBusTimetable;

        public SchoolBusViewHolder(View itemView) {
            super(itemView);
            tvBusRoute=(TextView) itemView.findViewById(R.id.textView_bus_route);
            glBusTimetable=(GridLayout)itemView.findViewById(R.id.gridlayout_bus_timetable);
        }
    }

}
