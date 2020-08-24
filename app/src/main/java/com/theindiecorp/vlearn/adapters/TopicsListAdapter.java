package com.theindiecorp.vlearn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.data.Topic;

import java.util.ArrayList;

public class TopicsListAdapter extends RecyclerView.Adapter<TopicsListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Topic> dataSet;
    private RvListener rvListener;

    public interface RvListener{
        public void openTopic(int position, String url);
    }

    public void setTopics(ArrayList<Topic> dataSet){
        this.dataSet = dataSet;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView topicTv;
        private ImageView progressBar;

        public MyViewHolder(View itemView){
            super(itemView);
            topicTv = itemView.findViewById(R.id.topic_tv);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }

    public TopicsListAdapter(Context context, ArrayList<Topic> dataSet){
        this.context = context;
        this.dataSet = dataSet;
    }

    public TopicsListAdapter(Context context, ArrayList<Topic> dataSet, RvListener rvListener){
        this.context = context;
        this.dataSet = dataSet;
        this.rvListener = rvListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topics_list_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Topic topic = dataSet.get(position);
        holder.topicTv.setText(topic.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvListener.openTopic(position, topic.getUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
