package com.theindiecorp.vlearn.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.activities.ResourceViewActivity;
import com.theindiecorp.vlearn.data.Course;

import java.util.ArrayList;

public class SearchResourceAdapter extends RecyclerView.Adapter<SearchResourceAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Course> dataSet;

    public void setCourses(ArrayList<Course> dataSet){
        this.dataSet = dataSet;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView courseNameTv, publisherNameTv;
        private ImageView courseImage;

        public MyViewHolder(View v){
            super(v);
            courseNameTv = v.findViewById(R.id.course_name_tv);
            publisherNameTv = v.findViewById(R.id.progress_tv);
            courseImage = v.findViewById(R.id.course_image);
        }
    }

    public SearchResourceAdapter(Context context, ArrayList<Course> dataSet){
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_menu_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Course course = dataSet.get(position);

        holder.courseNameTv.setText(course.getNameOfCourse());

        FirebaseDatabase.getInstance().getReference("users").child(course.getAuthorId())
                .child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.publisherNameTv.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ResourceViewActivity.class).
                        putExtra("courseId", course.getCourseId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
