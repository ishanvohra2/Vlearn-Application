package com.theindiecorp.vlearn.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.data.User;

public class ProfileViewActivity extends AppCompatActivity {

    private TextView displayNameTv, bioTv, sexTv, emailTv;
    private Button editProfileBtn, logoutBtn;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        displayNameTv = findViewById(R.id.profile_view_display_name_tv);
        bioTv = findViewById(R.id.profile_view_bio_tv);
        sexTv = findViewById(R.id.profile_view_sex_tv);
        emailTv = findViewById(R.id.profile_view_email_tv);
        editProfileBtn = findViewById(R.id.edit_profile_btn);
        logoutBtn = findViewById(R.id.logout_btn);

        final String userId = auth.getCurrentUser().getUid();

        databaseReference.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getSex() != null)
                    sexTv.setText(user.getSex().toString());
                else
                    sexTv.setVisibility(View.GONE);

                if (user.getBio() != null)
                    bioTv.setText(user.getBio());
                else
                    bioTv.setVisibility(View.GONE);

                displayNameTv.setText(user.getDisplayName());
                emailTv.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(ProfileViewActivity.this, LoginActivity.class));
                finish();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileViewActivity.this, EditProfileActivity.class));
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}