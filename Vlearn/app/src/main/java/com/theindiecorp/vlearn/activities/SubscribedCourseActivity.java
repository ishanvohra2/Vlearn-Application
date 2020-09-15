package com.theindiecorp.vlearn.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.adapters.ForumPostAdapter;
import com.theindiecorp.vlearn.adapters.TopicsListAdapter;
import com.theindiecorp.vlearn.data.Course;
import com.theindiecorp.vlearn.data.ForumPost;
import com.theindiecorp.vlearn.data.Topic;

import java.util.ArrayList;

public class SubscribedCourseActivity extends AppCompatActivity implements TopicsListAdapter.RvListener, ForumPostAdapter.CommentListener {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String courseId;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_course);

        final TextView categoryTv = findViewById(R.id.category_tv);
        final TextView courseNameTv = findViewById(R.id.course_name_tv);
        final TextView descriptionTv = findViewById(R.id.course_description_tv);
        final TextView keyPointsTv = findViewById(R.id.key_points_tv);

        courseId = getIntent().getStringExtra("courseId");

        RecyclerView topicsRecyclerView = findViewById(R.id.topics_recycler);
        topicsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final TopicsListAdapter topicsListAdapter = new TopicsListAdapter(this, new ArrayList<Topic>(), this);
        topicsRecyclerView.setAdapter(topicsListAdapter);

        RecyclerView forumPostRecycler = findViewById(R.id.discussion_recycler);
        forumPostRecycler.setLayoutManager(new LinearLayoutManager(this));
        final ForumPostAdapter forumPostAdapter = new ForumPostAdapter(this, new ArrayList<ForumPost>(), this);
        forumPostRecycler.setAdapter(forumPostAdapter);

        ImageButton backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Loading course topics
        databaseReference.child("courseTopics").child(courseId).addListenerForSingleValueEvent(new ValueEventListener() {
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

        //Load information about the course
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

        //Load forum posts
        databaseReference.child("forumPosts").child(courseId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ForumPost> posts = new ArrayList<>();
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        ForumPost p = snapshot.getValue(ForumPost.class);
                        p.setPostId(snapshot.getKey());
                        posts.add(p);
                    }
                    forumPostAdapter.setPosts(posts);
                    forumPostAdapter.notifyDataSetChanged();
                }
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

        //Add Forum Post Bottom Sheet

        bottomSheet = findViewById(R.id.add_post_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        FloatingActionButton floatingActionButton = findViewById(R.id.add_forum_post_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        EditText contentEt = findViewById(R.id.new_forum_post_content_et);
        Button uploadBtn = findViewById(R.id.new_post_upload_image_btn);
        Button shareBtn = findViewById(R.id.new_post_share_btn);
    }

    @Override
    public void openTopic(int position, String url) {
        Intent intent = new Intent(this, TopicViewActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("position", position);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    @Override
    public void viewComments(String postId) {

    }
}