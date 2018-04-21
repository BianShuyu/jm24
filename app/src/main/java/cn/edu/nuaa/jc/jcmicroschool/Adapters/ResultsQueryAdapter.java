package cn.edu.nuaa.jc.jcmicroschool.Adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.edu.nuaa.jc.jcmicroschool.DataUtils.ResultsQueryDataUtil;
import cn.edu.nuaa.jc.jcmicroschool.R;

/**
 * Created by bsy on 2017/9/16.
 */

public class ResultsQueryAdapter extends RecyclerView.Adapter<ResultsQueryAdapter.ResultsQueryViewHolder> {

    private List<ResultsQueryDataUtil.ResultsQueryData> dataList;

    public ResultsQueryAdapter() {

    }


    @Override
    public ResultsQueryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.item_results_query,parent,false);
        return new ResultsQueryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultsQueryViewHolder holder, int position) {
        final ResultsQueryDataUtil.ResultsQueryData data =dataList.get(position);
        holder.tvTitle.setText(data.name课程名);
        holder.tvScore.setText(data.gpa绩点);
        holder.tvDetailed.setText("学期："+data.term学期 +'\n'
                +"考试日期："+data.date考试日期 +'\n'
                + "成绩："+data.score成绩 +'\n'
                +"学分："+data.credit学分 +'\n');
        // TODO: 2017/9/16 待补充完整
        final int mPosition=position;
        final ResultsQueryViewHolder passByHolder=holder;
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!passByHolder.detailed) {//未展开时
                    passByHolder.tvDetailed.setText("学期："+data.term学期 +'\n'
                            +"考试日期：" + data.date考试日期 + '\n'
                            + "成绩：" + data.score成绩 + '\n'
                            + "学分：" + data.credit学分 + '\n'
                            + "考试性质：" + data.properties考试性质 + '\n'
                            + "课程编号：" + data.courseId课程编号 + '\n'
                            + "课程类别："+data.courseType课程类别 +'\n'
                    );
                    passByHolder.ivArray.setImageResource(R.drawable.array_up);
                    passByHolder.detailed=true;
                }else {//已展开时
                    passByHolder.tvDetailed.setText("学期："+data.term学期 +'\n'
                            +"考试日期："+data.date考试日期 +'\n'
                            + "成绩："+data.score成绩 +'\n'
                            +"学分："+data.credit学分 +'\n');
                    passByHolder.ivArray.setImageResource(R.drawable.array_down);
                    passByHolder.detailed=false;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(dataList==null){
            return 0;
        }else {
            return dataList.size();
        }
    }

    public void setDataList(List<ResultsQueryDataUtil.ResultsQueryData> dataList){
        this.dataList=dataList;
        notifyDataSetChanged();

    }

    /**
     * 暂存所有需要修改的view对象
     */
    public class ResultsQueryViewHolder extends RecyclerView.ViewHolder{

        public final TextView tvDetailed;
        public final TextView tvScore;
        public final TextView tvTitle;
        public final ImageView ivArray;
        public final ConstraintLayout constraintLayout;
        Boolean detailed;

        /**
         *
         * @param itemView 原始参数，传入布局
         * //@param listener 新增参数，实现并传入具体的点击事件
         */
        public ResultsQueryViewHolder(View itemView) {
            super(itemView);
            tvDetailed=(TextView)itemView.findViewById(R.id.textView_results_query_detailed);
            tvScore=(TextView)itemView.findViewById(R.id.textView_results_query_score);
            tvTitle=(TextView)itemView.findViewById(R.id.textView_results_query_title);
            ivArray=(ImageView)itemView.findViewById(R.id.imageView_results_query_array);
            constraintLayout=(ConstraintLayout)itemView.findViewById(R.id.constraintLayout_item_results);
            detailed=false;
        }

    }
}
