package com.theindiecorp.vlearn.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.util.HashMap;
import java.util.Map;
import android.app.ActivityOptions;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theindiecorp.vlearn.R;

public class discussionactivity extends AppCompatActivity {


    private ProgressDialog pDialog;
    private String que_id , topic , body , date  ;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fetchanswer);



        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        que_id = bundle.getString("que_id");
        topic = bundle.getString("topic");
        body = bundle.getString("body");
        date = bundle.getString("date");


        FloatingActionButton mSubmitButton = (FloatingActionButton) findViewById(R.id.description);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration (this, LinearLayoutManager.VERTICAL);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        fetchAnswer(que_id );

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(discussionactivity, this, postanswer.class);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext() ).toBundle();

                intent.putExtra("que_id" , que_id);
                intent.putExtra("topic" , topic);
                intent.putExtra("body" , body);
                intent.putExtra("date" , date);
                startActivity(intent, bndlanimation);
                //  overridePendingTransition(0, 0);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.profile_menu_item, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.) {
            Intent intent = new Intent(this, discussionactivity.class);
            intent.putExtra("que_id" , que_id);
            intent.putExtra("topic" , topic);
            intent.putExtra("body" , body);
            intent.putExtra("date" , date);

            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchAnswer (final String que_id ){

        pDialog.setMessage("Loading ...");
        showDialog();


          {
              (); {

                Map<String, String> params = new HashMap<>();
                params.put("que_id", que_id);
          }
        }


    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }


}
