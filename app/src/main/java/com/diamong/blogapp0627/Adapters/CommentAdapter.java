package com.diamong.blogapp0627.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.diamong.blogapp0627.Models.Comment;
import com.diamong.blogapp0627.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;


    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment,viewGroup,false);

        return new CommentViewHolder(row);

    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {

        Glide.with(mContext).load(mData.get(i).getUimg()).into(commentViewHolder.imgUser);
        commentViewHolder.tvName.setText(mData.get(i).getUname());
        commentViewHolder.tvContent.setText(mData.get(i).getContent());
        commentViewHolder.tvDate.setText(timestampToString((long)mData.get(i).getTimestamep()));


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder{

        ImageView imgUser;
        TextView tvName,tvContent,tvDate;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUser=itemView.findViewById(R.id.comment_user_img);
            tvName=itemView.findViewById(R.id.comment_username);
            tvContent=itemView.findViewById(R.id.comment_content);
            tvDate=itemView.findViewById(R.id.comment_date);
        }
    }

    private String timestampToString(long time){

        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm",calendar).toString();

        return date;



    }


}
