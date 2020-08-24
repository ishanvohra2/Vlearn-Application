package com.theindiecorp.vlearn.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.adapters.SearchResourceAdapter;
import com.theindiecorp.vlearn.data.Course;

import java.util.ArrayList;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private SearchResourceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SearchView searchView = findViewById(R.id.search_view);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));

        adapter = new SearchResourceAdapter(this, new ArrayList<Course>());
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Query query1 = FirebaseDatabase.getInstance().getReference("courses");
                query1.orderByChild("nameOfCourse").startAt(query).endAt(query + "\uf8ff")
                        .addValueEventListener(valueEventListener);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query query1 = FirebaseDatabase.getInstance().getReference("courses");
                query1.orderByChild("nameOfCourse").startAt(newText).endAt(newText + "\uf8ff")
                        .addValueEventListener(valueEventListener);

                return false;
            }
        });
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                ArrayList<Course> courses = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Course course = snapshot.getValue(Course.class);
                    courses.add(course);
                }
                adapter.setCourses(courses);
                adapter.notifyDataSetChanged();
            }
            else{
                Log.d("Search Activity", "onDataChange: Not Found Anything");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}