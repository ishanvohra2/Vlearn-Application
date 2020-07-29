package com.theindiecorp.vlearn.activities;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.theindiecorp.vlearn.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class discussionforumactivity extends AppCompatActivity{

    private EditText inputTopic;
    private EditText inputDescrp;
    private ProgressDialog pDialog;
    private String name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.discussion);

        inputTopic = findViewById( R.id.topic);
        inputDescrp = findViewById(R.id.description);
        Button mSubmitButton = findViewById(R.id.submit_post);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        // SqLite database handler

        Object db = new Object();
        HashMap<String, String> user =   db.getUserDetails();
        name = user.get("name");

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = inputTopic.getText().toString().trim();
                String description = inputDescrp.getText().toString().trim();

                if (!topic.isEmpty() && !description.isEmpty()) {
                    submitPost(topic , description , name);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter the details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();
    }

    private void submitPost(final String topic , final String description , final String name){

        pDialog.setMessage("Submitting ...");
        showDialog();

          {

             public void (String  String response = "" )
              {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {


                        Toast.makeText(getApplicationContext(), "Submitted successfully!", Toast.LENGTH_LONG).show();

                        // LAUNCH MAIN ACTIVITY
                        Intent intent = new Intent(discussionforumactivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        {

             {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("topic", topic);
                params.put("description", description);
                params.put("name", name);

             }

        }

        // Adding request to request queue

    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
