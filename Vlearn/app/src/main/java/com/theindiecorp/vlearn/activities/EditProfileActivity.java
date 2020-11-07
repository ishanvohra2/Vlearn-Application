package com.theindiecorp.vlearn.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.data.User;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameEt, bioEt, emailEt;
    private Spinner ageSpinner;
    private Button doneBtn;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final String userId = auth.getCurrentUser().getUid();

        nameEt = findViewById(R.id.display_name_et);
        bioEt = findViewById(R.id.bio_et);
        emailEt = findViewById(R.id.email_et);
        ageSpinner = findViewById(R.id.spinner);
        doneBtn = findViewById(R.id.done_btn);

//        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,);

        databaseReference.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user.getBio() != null)
                    bioEt.setText(user.getBio());
                else
                    bioEt.setVisibility(View.GONE);

                nameEt.setText(user.getDisplayName());
                emailEt.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(nameEt.getText().toString())){
                    nameEt.setError("Please enter your name");
                    return;
                }

                if(TextUtils.isEmpty(bioEt.getText().toString())){
                    bioEt.setError("Please enter your bio");
                    return;
                }

                if(TextUtils.isEmpty(emailEt.getText().toString())){
                    emailEt.setError("Please enter your email");
                    return;
                }

                databaseReference.child("users").child(userId).child("displayName").setValue(nameEt.getText().toString());
                databaseReference.child("users").child(userId).child("bio").setValue(bioEt.getText().toString());
                databaseReference.child("users").child(userId).child("email").setValue(emailEt.getText().toString());

                finish();
            }
        });

    }
}