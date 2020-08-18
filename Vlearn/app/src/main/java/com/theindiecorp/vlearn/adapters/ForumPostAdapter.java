package com.theindiecorp.vlearn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.data.ForumPost;

import java.util.ArrayList;

public class ForumPostAdapter extends RecyclerView.Adapter<ForumPostAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ForumPost> dataSet;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public void setPosts(ArrayList<ForumPost> dataSet){
        this.dataSet = dataSet;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView contentTv, authorNameTv;
        private CircularImageView profilePicTv;
        private ImageView postImg;
        private ImageView likeBtn, commentBtn;

        public MyViewHolder(View itemView){
            super(itemView);
            contentTv = itemView.findViewById(R.id.forum_post_item_content_tv);
            authorNameTv = itemView.findViewById(R.id.forum_post_item_publisher_name_tv);
            //Define all items here
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_post_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        ForumPost post = dataSet.get(position);

        holder.contentTv.setText(post.getContent());

        databaseReference.child("users").child(post.getAuthorId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.authorNameTv.setText(dataSnapshot.child("displayName").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
