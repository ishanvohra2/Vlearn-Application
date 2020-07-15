package com.theindiecorp.vlearn.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.adapters.TopicsListAdapter;
import com.theindiecorp.vlearn.data.Course;
import com.theindiecorp.vlearn.data.Topic;

import java.util.ArrayList;

public class SubscribedCourseActivity extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_course);

        final TextView categoryTv = findViewById(R.id.category_tv);
        final TextView courseNameTv = findViewById(R.id.course_name_tv);
        final TextView descriptionTv = findViewById(R.id.course_description_tv);
        final TextView keyPointsTv = findViewById(R.id.key_points_tv);

        final String courseId = getIntent().getStringExtra("courseId");

        RecyclerView topicsRecyclerView = findViewById(R.id.topics_recycler);
        topicsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final TopicsListAdapter topicsListAdapter = new TopicsListAdapter(this, new ArrayList<Topic>());
        topicsRecyclerView.setAdapter(topicsListAdapter);

        ImageButton backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
                descriptionTv.setText(course.getCourseDescription());
                keyPointsTv.setText(course.getKeyPoints());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final TextView showIntro = findViewById(R.id.show_intro_tv);
        final TextView showTopics = findViewById(R.id.show_topics_tv);
        final TextView showDiscussion = findViewById(R.id.show_discussion_tv);
        final LinearLayout introLayout = findViewById(R.id.intro_layout);
        final LinearLayout discussionLayout = findViewById(R.id.discussion_layout);
        final LinearLayout topicsLayout = findViewById(R.id.topics_layout);

        showIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIntro.setTextColor(getResources().getColor(R.color.colorPrimary));
                showTopics.setTextColor(getResources().getColor(android.R.color.black));
                showDiscussion.setTextColor(getResources().getColor(android.R.color.black));
                introLayout.setVisibility(View.VISIBLE);
                topicsLayout.setVisibility(View.GONE);
                discussionLayout.setVisibility(View.GONE);
            }
        });

        showTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIntro.setTextColor(getResources().getColor(android.R.color.black));
                showTopics.setTextColor(getResources().getColor(R.color.colorPrimary));
                showDiscussion.setTextColor(getResources().getColor(android.R.color.black));
                introLayout.setVisibility(View.GONE);
                topicsLayout.setVisibility(View.VISIBLE);
                discussionLayout.setVisibility(View.GONE);
            }
        });

        showDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIntro.setTextColor(getResources().getColor(android.R.color.black));
                showTopics.setTextColor(getResources().getColor(android.R.color.black));
                showDiscussion.setTextColor(getResources().getColor(R.color.colorPrimary));
                introLayout.setVisibility(View.GONE);
                topicsLayout.setVisibility(View.GONE);
                discussionLayout.setVisibility(View.VISIBLE);
            }
        });

    }
}