package com.theindiecorp.vlearn.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.adapters.CommentsAdapter;
import com.theindiecorp.vlearn.adapters.ForumPostAdapter;
import com.theindiecorp.vlearn.adapters.TopicsListAdapter;
import com.theindiecorp.vlearn.data.Comment;
import com.theindiecorp.vlearn.data.Course;
import com.theindiecorp.vlearn.data.ForumPost;
import com.theindiecorp.vlearn.data.Topic;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class SubscribedCourseActivity extends AppCompatActivity implements TopicsListAdapter.RvListener, ForumPostAdapter.CommentListener {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String courseId;
    private BottomSheetBehavior bottomSheetBehavior, commentSheetBehaviour;
    private LinearLayout bottomSheet, commentBottomSheet;

    int PLACE_PICKER_REQUEST = 12;
    private static final int PICK_IMAGE = 100;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private ImageView postImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_course);

        final TextView categoryTv = findViewById(R.id.category_tv);
        final TextView courseNameTv = findViewById(R.id.course_name_tv);
        final TextView descriptionTv = findViewById(R.id.course_description_tv);
        final TextView keyPointsTv = findViewById(R.id.key_points_tv);
        final TextView progressTv = findViewById(R.id.enrollment_number_tv);
        final FloatingActionButton floatingActionButton = findViewById(R.id.add_forum_post_fab);

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

        databaseReference.child("studyingCourses").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(courseId)
                .child("progress").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    progressTv.setText(dataSnapshot.getValue(String.class));
                else
                    progressTv.setVisibility(View.GONE);
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
                    Collections.reverse(posts);
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
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                showIntro.setTextColor(getResources().getColor(R.color.colorPrimary));
                showTopics.setTextColor(getResources().getColor(android.R.color.black));
                showDiscussion.setTextColor(getResources().getColor(android.R.color.black));
                introLayout.setVisibility(View.VISIBLE);
                topicsLayout.setVisibility(View.GONE);
                discussionLayout.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.GONE);
            }
        });

        showTopics.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                showIntro.setTextColor(getResources().getColor(android.R.color.black));
                showTopics.setTextColor(getResources().getColor(R.color.colorPrimary));
                showDiscussion.setTextColor(getResources().getColor(android.R.color.black));
                introLayout.setVisibility(View.GONE);
                topicsLayout.setVisibility(View.VISIBLE);
                discussionLayout.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.GONE);
            }
        });

        showDiscussion.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                showIntro.setTextColor(getResources().getColor(android.R.color.black));
                showTopics.setTextColor(getResources().getColor(android.R.color.black));
                showDiscussion.setTextColor(getResources().getColor(R.color.colorPrimary));
                introLayout.setVisibility(View.GONE);
                topicsLayout.setVisibility(View.GONE);
                discussionLayout.setVisibility(View.VISIBLE);
                floatingActionButton.setVisibility(View.VISIBLE);
            }
        });

        //Add Forum Post Bottom Sheet
        final CardView cardView = findViewById(R.id.subscribed_course_card_view);
        bottomSheet = findViewById(R.id.add_post_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        final EditText contentEt = bottomSheet.findViewById(R.id.new_forum_post_content_et);
        Button uploadBtn = bottomSheet.findViewById(R.id.new_post_upload_image_btn);
        Button shareBtn = bottomSheet.findViewById(R.id.new_post_share_btn);
        postImg = bottomSheet.findViewById(R.id.new_post_main_image);

        ImageButton closeSheetBtn = bottomSheet.findViewById(R.id.back_btn);
        closeSheetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addForumPost(contentEt.getText().toString());
            }
        });
    }

    private void addForumPost(String toString) {
        if(toString.isEmpty()){
            Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout), "Enter some content", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        ForumPost post = new ForumPost();
        post.setAuthorId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        post.setContent(toString);
        post.setPostId(databaseReference.push().getKey());
        post.setImgUrl(uploadImage(post.getPostId()));

        databaseReference.child("forumPosts").child(courseId).child(post.getPostId()).setValue(post);
        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout), "Posted!", Snackbar.LENGTH_LONG);
        snackbar.show();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                postImg.setImageURI(data.getData());
            }
        }
    }

    public String uploadImage(String eventId) {

        if (postImg.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) postImg.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();

            String path = "events/" + eventId + "/images/image.jpeg";
            StorageReference storageReference = storage.getReference(path);

            UploadTask uploadTask = storageReference.putBytes(bitmapdata);

            return path;
        }

        return "";
    }

    @Override
    public void openTopic(final int position, final String url) {
        databaseReference.child("studyingCourses").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(courseId)
                .child("progress").setValue(position + 1 + " completed").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(SubscribedCourseActivity.this, TopicViewActivity.class);
                    intent.putExtra("courseId", courseId);
                    intent.putExtra("position", position);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void viewComments(final String postId) {
        commentBottomSheet = findViewById(R.id.comments_bottom_sheet);
        commentSheetBehaviour = BottomSheetBehavior.from(commentBottomSheet);

        commentSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);

        CircularImageView profileImg = commentBottomSheet.findViewById(R.id.comment_sheet_profile_pic_iv);
        final EditText commentEt = commentBottomSheet.findViewById(R.id.comment_sheet_comment_et);
        ImageButton sendBtn = commentBottomSheet.findViewById(R.id.comment_sheet_send_btn);
        ImageButton backBtn = commentBottomSheet.findViewById(R.id.comment_sheet_back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(commentEt.getText().toString()))
                    return;

                Date date = new Date();

                Comment comment = new Comment(
                        databaseReference.push().getKey(),
                        commentEt.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        date.getTime());

                databaseReference.child("forumPosts").child(courseId).child(postId).child("comments").child(comment.getCommentId())
                        .setValue(comment);
                commentEt.setText("");
            }
        });

        RecyclerView recyclerView = findViewById(R.id.comment_sheet_scroll_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final CommentsAdapter adapter = new CommentsAdapter(this, new ArrayList<Comment>());
        recyclerView.setAdapter(adapter);

        databaseReference.child("forumPosts").child(courseId).child(postId).child("comments").addValueEventListener(
                new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<Comment> comments = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Comment comment = snapshot.getValue(Comment.class);
                        comments.add(comment);
                    }
                    Collections.reverse(comments);
                    adapter.setComments(comments);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}