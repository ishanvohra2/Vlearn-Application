package com.theindiecorp.vlearn.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.data.User;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEt, emailEt, usernameEt, passwordEt;
    private Button signUpBtn, logInBtn, forgetPassBtn;
    private CircularImageView profilePic;
    public static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final FirebaseAuth auth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        nameEt = findViewById(R.id.signup_display_name);
        passwordEt = findViewById(R.id.login_password);
        emailEt = findViewById(R.id.reset_email);
        usernameEt = findViewById(R.id.username_et);
        signUpBtn = findViewById(R.id.signup_register_btn);
        forgetPassBtn = findViewById(R.id.reset_password_reset_btn);
        logInBtn = findViewById(R.id.signup_log_in_btn);
        profilePic = findViewById(R.id.sign_up_profile_photo);
        final CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinator_layout);

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

        forgetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, ResetPasswordActivity.class));
                finish();
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(nameEt.getText().toString())){
                    nameEt.setError("Please enter your name");
                    return;
                }

                if(TextUtils.isEmpty(emailEt.getText().toString())){
                    emailEt.setError("Please enter your email");
                    return;
                }

                if(TextUtils.isEmpty(usernameEt.getText().toString())){
                    usernameEt.setError("Please enter your username");
                    return;
                }

                if(passwordEt.getText().toString().length() < 6){
                    passwordEt.setError("Password must contain more than 6 characters");
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                final String date = day + "/" + month + "/" + year;

                String password = passwordEt.getText().toString();
                final String email = emailEt.getText().toString();

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Oops! Please try again", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                        else{
                            String path = updateProfilePick(auth.getCurrentUser().getUid(), profilePic);
                            UserProfileChangeRequest request;
                            if (path != null) {
                                request = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nameEt.getText().toString())
                                        .setPhotoUri(Uri.parse(path)).build();
                            } else {
                                request = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nameEt.getText().toString()).build();
                            }
                            auth.getCurrentUser().updateProfile(request);
                            String id = auth.getCurrentUser().getUid();
                            User user = new User();
                            user.setDisplayName(nameEt.getText().toString());
                            user.setJoinedOn(date);
                            user.setSex(User.Sex.MALE);
                            user.setEmail(email);
                            user.setUsername(usernameEt.getText().toString());
                            user.setPrivateProfile("Public");
                            user.setPoints(100);
                            writeNewUser(user, id);

                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                });
            }
        });
    }

    private void writeNewUser(User user, String id) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(id).setValue(user);
        mDatabase.child("pointsHistory").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("regPoints").setValue(100);
    }

    public static String updateProfilePick(String userId, ImageView imageView) {
        if (imageView.getDrawable() != null) {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();

            String path = "users/" + userId + "/images/profile_pic/profile_pic.jpeg";
            StorageReference storageReference = storage.getReference(path);

            storageReference.putBytes(bitmapdata);
            return path;
        }
        return null;
    }

    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            profilePic.setImageURI(data.getData());
        }
    }
}