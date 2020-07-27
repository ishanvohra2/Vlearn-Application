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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.activities.ResourceViewActivity;
import com.theindiecorp.vlearn.data.Course;

import java.util.ArrayList;

public class LatestCourseAdapter extends RecyclerView.Adapter<LatestCourseAdapter.MyViewHolder> {

    private Context context;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<Course> dataSet;

    public void setCourses(ArrayList<Course> dataSet){
        this.dataSet = dataSet;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTv, publisherName, publishDate;
        private ImageView courseImg;
        public MyViewHolder(View itemView){
            super(itemView);
            nameTv = itemView.findViewById(R.id.course_name_tv);
            publisherName = itemView.findViewById(R.id.publisher_tv);
            publishDate = itemView.findViewById(R.id.uploaded_at_tv);
            courseImg = itemView.findViewById(R.id.course_image);
        }
    }

    public LatestCourseAdapter(Context context, ArrayList<Course> dataSet){
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.latest_course_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Course course = dataSet.get(position);

        holder.nameTv.setText(course.getNameOfCourse());
        holder.publishDate.setText( "Uploaded at :" + course.getPublishDate());

        if(course.getImgPath() != null) {
            if(!course.getImgPath().isEmpty()){
                StorageReference imageReference = storage.getReference().child(course.getImgPath());
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context.getApplicationContext())
                                .load(uri)
                                .into(holder.courseImg);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("Course Image", exception.getMessage());
                    }
                });
            }
        }

        databaseReference.child("users").child(course.getAuthorId()).child("displayName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    holder.publisherName.setText(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ResourceViewActivity.class)
                        .putExtra("courseId", course.getCourseId())
                        .putExtra("resourceTyoe", course.getType()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
