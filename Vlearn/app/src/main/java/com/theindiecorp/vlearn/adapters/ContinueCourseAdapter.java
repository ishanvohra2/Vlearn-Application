package com.theindiecorp.vlearn.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.activities.ResourceViewActivity;
import com.theindiecorp.vlearn.activities.SubscribedCourseActivity;
import com.theindiecorp.vlearn.data.Course;

import java.util.ArrayList;

public class ContinueCourseAdapter extends RecyclerView.Adapter<ContinueCourseAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Course> dataset;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public void setCourses(ArrayList<Course> dataset){
        this.dataset = dataset;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView courseImage;
        private TextView courseNameTv, progressTv;

        public MyViewHolder(View itemView){
            super(itemView);
            courseImage = itemView.findViewById(R.id.course_image);
            courseNameTv = itemView.findViewById(R.id.course_name_tv);
            progressTv = itemView.findViewById(R.id.progress_tv);
        }
    }

    public ContinueCourseAdapter(Context context, ArrayList<Course> dataset){
        this.context = context;
        this.dataset = dataset;
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
        final Course course = dataset.get(position);

        holder.courseNameTv.setText(course.getNameOfCourse());

        if(course.getImgPath() != null) {
            if(!course.getImgPath().isEmpty()){
                Glide.with(context.getApplicationContext())
                        .load(course.getImgPath())
                        .into(holder.courseImage);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, SubscribedCourseActivity.class).
                        putExtra("courseId", course.getCourseId()));
            }
        });

        databaseReference.child("studyingCourses").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(course.getCourseId())
                .child("progress").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                            holder.progressTv.setText(dataSnapshot.getValue(String.class));
                        else
                            holder.progressTv.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
