package com.theindiecorp.vlearn.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.adapters.TopicsListAdapter;
import com.theindiecorp.vlearn.data.Course;
import com.theindiecorp.vlearn.data.Topic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class ResourceViewActivity extends AppCompatActivity implements TopicsListAdapter.RvListener {

    FirebaseFirestore database = FirebaseFirestore.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_view);

        final LinearLayout topicsLayout = findViewById(R.id.topics_layout);
        final TextView categoryTv = findViewById(R.id.category_tv);
        final TextView courseNameTv = findViewById(R.id.course_name_tv);
        final TextView enrollmentTv = findViewById(R.id.enrollment_number_tv);
        final TextView descriptionTv = findViewById(R.id.course_description_tv);
        final TextView keyPointsTv = findViewById(R.id.key_points_tv);
        final Button getCourseBtn = findViewById(R.id.get_course_btn);
        final Button viewOwnedBtn = findViewById(R.id.view_library_btn);
        final ImageView imageView = findViewById(R.id.course_image);

        final String courseId = getIntent().getStringExtra("courseId");
        final String resourceType = getIntent().getStringExtra("resourceType");

        RecyclerView topicsRecyclerView = findViewById(R.id.recycler_view);
        topicsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final TopicsListAdapter topicsListAdapter = new TopicsListAdapter(this, new ArrayList<Topic>(), this);
        topicsRecyclerView.setAdapter(topicsListAdapter);

        ImageButton backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final TextView readMoreTv = findViewById(R.id.read_more_tv);
        readMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topicsLayout.setVisibility(View.VISIBLE);
                readMoreTv.setVisibility(View.GONE);
            }
        });

        assert courseId != null;
        databaseReference.child("courseTopics").child(courseId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Topic> topics = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    topics.add(snapshot.getValue(Topic.class));
                }
                topicsListAdapter.setTopics(topics);
                topicsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("courses").child(courseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Course course = dataSnapshot.getValue(Course.class);
                categoryTv.setText(course.getCategory());
                courseNameTv.setText(course.getNameOfCourse());
                enrollmentTv.setText(course.getEnrollmentNo() + " students");
                descriptionTv.setText(course.getCourseDescription());
                keyPointsTv.setText(course.getKeyPoints());

                if(course.getImgPath() != null){
                    if(!course.getImgPath().isEmpty()){
                        Glide.with(ResourceViewActivity.this)
                                .load(course.getImgPath())
                                .into(imageView);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("studyingCourses").child(userId).child(courseId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    getCourseBtn.setVisibility(View.GONE);
                    viewOwnedBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscribeCourse(courseId);
            }
        });

        viewOwnedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResourceViewActivity.this, SubscribedCourseActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });
    }

    private void subscribeCourse(final String courseId){
        databaseReference.child("courses").child(courseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Course course = dataSnapshot.getValue(Course.class);
                databaseReference.child("studyingCourses").child(userId).child(courseId).setValue(course);
                databaseReference.child("courses").child(courseId).child("enrollmentNo").setValue(course.getEnrollmentNo() + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void openTopic(int position, String url) {

    }
}