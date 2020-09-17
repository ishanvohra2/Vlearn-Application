package com.theindiecorp.vlearn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theindiecorp.vlearn.R;
import com.theindiecorp.vlearn.data.Comment;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

    private ArrayList<Comment> comments;
    private Context context;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public void setComments(ArrayList<Comment> comments){
        this.comments = comments;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTv, commentTv;
        private RelativeTimeTextView timeTv;
        private CircularImageView profilePic;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.comment_item_name_tv);
            commentTv = itemView.findViewById(R.id.comment_item_content_tv);
            timeTv = itemView.findViewById(R.id.comment_item_time_tv);
            profilePic = itemView.findViewById(R.id.comment_sheet_profile_pic_iv);
        }
    }

    public CommentsAdapter(Context context, ArrayList<Comment> comments){
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Comment comment = comments.get(position);

        holder.commentTv.setText(comment.getMessage());
        holder.timeTv.setReferenceTime(comment.getDate());

        databaseReference.child("users").child(comment.getUserId()).child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.nameTv.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

}
