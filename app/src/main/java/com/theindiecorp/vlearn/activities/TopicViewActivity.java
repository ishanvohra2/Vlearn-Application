package com.theindiecorp.vlearn.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.data.Topic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TopicViewActivity extends AppCompatActivity {

    private int courseIndex;
    private String courseId, videoURL;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_view);

        courseIndex = getIntent().getIntExtra("position", 0);
        courseId = getIntent().getStringExtra("courseId");
        videoURL = getIntent().getStringExtra("url");

        final TextView courseNameTv = findViewById(R.id.course_name_tv);
        final TextView topicTv = findViewById(R.id.topic_title_tv);
        final TextView descriptionTv = findViewById(R.id.description_tv);
        ImageButton backBtn = findViewById(R.id.back_btn);

        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = getYouTubeId(videoURL);
                if(!videoId.equals("error")){
                    youTubePlayer.loadVideo(videoId, 0f);
                    youTubePlayer.play();
                }
            }
        });

        databaseReference.child("courseTopics").child(courseId).child(courseIndex + "")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Topic topic = dataSnapshot.getValue(Topic.class);
                descriptionTv.setText(topic.getDescription());
                topicTv.setText(topic.getTitle());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("courses").child(courseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                courseNameTv.setText(dataSnapshot.child("nameOfCourse").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private String getYouTubeId (String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if(matcher.find()){
            return matcher.group();
        } else {
            return "error";
        }
    }
}