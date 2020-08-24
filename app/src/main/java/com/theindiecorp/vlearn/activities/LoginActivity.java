package com.theindiecorp.vlearn.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.theindiecorp.vlearn.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEt, passwordEt;
    private Button loginBtn, registerBtn, forgetPasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final FirebaseAuth auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        emailEt = findViewById(R.id.email);
        passwordEt = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_login_btn);
        registerBtn = findViewById(R.id.login_sign_up_btn);
        forgetPasswordBtn = findViewById(R.id.reset_password_reset_btn);
        final CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinator_layout);

        forgetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(emailEt.getText().toString())){
                    emailEt.setError("Please enter your email id");
                    return;
                }

                if(passwordEt.getText().toString().length() < 6){
                    passwordEt.setError("Password must contain more than 6 characters");
                    return;
                }

                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Oops! Please try again", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                        else{
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            }
        });
    }
}