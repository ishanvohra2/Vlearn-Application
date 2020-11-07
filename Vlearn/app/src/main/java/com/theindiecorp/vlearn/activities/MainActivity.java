package com.theindiecorp.vlearn.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.adapters.ContinueCourseAdapter;
import com.theindiecorp.vlearn.adapters.LatestCourseAdapter;
import com.theindiecorp.vlearn.data.Course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN ACTIVITY";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseAuth auth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.log_out_item:
                        auth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        break;

                    case R.id.profile_item:
                        startActivity(new Intent(MainActivity.this, ProfileViewActivity.class));
                        break;
                }
                return false;
            }
        });

        RecyclerView latestCourseList = findViewById(R.id.latest_course_rv);
        latestCourseList.setLayoutManager(new LinearLayoutManager(this));
        final LatestCourseAdapter latestCourseAdapter = new LatestCourseAdapter(this, new ArrayList<Course>());
        latestCourseList.setAdapter(latestCourseAdapter);

        databaseReference.child("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<Course> courses = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Course course = snapshot.getValue(Course.class);
                        courses.add(course);
                    }
                    Collections.reverse(courses);
                    latestCourseAdapter.setCourses(courses);
                    latestCourseAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final LinearLayout continueLayout = findViewById(R.id.continue_layout);

        RecyclerView continueRecycler = findViewById(R.id.continue_recycler);
        continueRecycler.setLayoutManager(new LinearLayoutManager(this));
        final ContinueCourseAdapter continueCourseAdapter = new ContinueCourseAdapter(this, new ArrayList<Course>());
        continueRecycler.setAdapter(continueCourseAdapter);

        databaseReference.child("studyingCourses").child(auth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<Course> courses = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Course course = snapshot.getValue(Course.class);
                        courses.add(course);
                    }
                    Collections.reverse(courses);
                    continueCourseAdapter.setCourses(courses);
                    continueCourseAdapter.notifyDataSetChanged();
                }
                else{
                    continueLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SearchView searchResource = findViewById(R.id.search_resource);
        searchResource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

    }
}