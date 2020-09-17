package com.theindiecorp.vlearn.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.data.ForumPost;

import java.util.ArrayList;

public class ForumPostAdapter extends RecyclerView.Adapter<ForumPostAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ForumPost> dataSet;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private CommentListener commentListener;

    public interface CommentListener{
        public void viewComments(String postId);
    }

    public void setPosts(ArrayList<ForumPost> dataSet){
        this.dataSet = dataSet;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView contentTv, authorNameTv;
        private CircularImageView profilePicIv;
        private ImageView postImg;
        private ImageView likeBtn, commentBtn;

        public MyViewHolder(View itemView){
            super(itemView);
            contentTv = itemView.findViewById(R.id.forum_post_item_content_tv);
            authorNameTv = itemView.findViewById(R.id.forum_post_item_publisher_name_tv);
            postImg = itemView.findViewById(R.id.forum_post_item_post_image_iv);
            likeBtn = itemView.findViewById(R.id.forum_post_item_like_btn);
            commentBtn = itemView.findViewById(R.id.forum_post_item_comment_btn);
            profilePicIv = itemView.findViewById(R.id.forum_post_item_profile_pic_iv);
        }
    }

    public ForumPostAdapter(Context context, ArrayList<ForumPost> dataSet, CommentListener commentListener){
        this.commentListener = commentListener;
        this.context = context;
        this.dataSet = dataSet;
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
        final ForumPost post = dataSet.get(position);
        final boolean[] isLiked = {false};

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

        if(post.getImgUrl() != null){
            if(!post.getImgUrl().isEmpty()){
                StorageReference imageReference = storage.getReference().child(post.getImgUrl());
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context.getApplicationContext())
                                .load(uri)
                                .into(holder.postImg);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("Course Image", exception.getMessage());
                    }
                });
            }
        }

        databaseReference.child("likes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(post.getPostId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            isLiked[0] = true;
                            holder.likeBtn.setImageDrawable(context.getDrawable(R.drawable.liked));
                        }
                        else {
                            isLiked[0] = false;
                            holder.likeBtn.setImageDrawable(context.getDrawable(R.drawable.notliked));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLiked[0]){
                    databaseReference.child("likes").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(post.getPostId()).setValue(true);
                }
                else
                    databaseReference.child("likes").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(post.getPostId()).removeValue();
            }
        });

        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentListener.viewComments(post.getPostId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
