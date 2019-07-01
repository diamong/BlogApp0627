package com.diamong.blogapp0627.Activites;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.diamong.blogapp0627.Adapters.CommentAdapter;
import com.diamong.blogapp0627.Models.Comment;
import com.diamong.blogapp0627.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {
    ImageView imgPost,imgUserPost,imgCurrentUser;
    TextView txtPostDescription, txtPostDateName, txtPostTitle;
    EditText editTextComment;
    Button btnAddComment;
    String postKey;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase firebaseDatabase;

    RecyclerView rvComment;
    CommentAdapter commentAdapter;
    List<Comment> commentList;

    static String COMMENT_KEY ="Comment";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        firebaseDatabase =FirebaseDatabase.getInstance();

        //ini view;

        rvComment=findViewById(R.id.rv_comment);

        imgPost = findViewById(R.id.post_detail_img);
        imgUserPost=findViewById(R.id.post_detail_user_img);
        imgCurrentUser=findViewById(R.id.post_detail_currentuser_img);

        txtPostDescription=findViewById(R.id.post_detail_description);
        txtPostDateName=findViewById(R.id.post_detail_data_name);
        txtPostTitle=findViewById(R.id.post_detail_title);

        editTextComment=findViewById(R.id.post_detail_comment_txt);

        btnAddComment =findViewById(R.id.post_detail_add_comment_btn);

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnAddComment.setVisibility(View.INVISIBLE);

                DatabaseReference commentReference  = firebaseDatabase.getReference(COMMENT_KEY).child(postKey).push();
                String comment_content = editTextComment.getText().toString();
                String uid = mUser.getUid();
                String name = mUser.getDisplayName();
                String uimg = mUser.getPhotoUrl().toString();

                Comment comment = new Comment(comment_content,uid,uimg,name);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        showMessage("comment added");
                        editTextComment.setText("");
                        btnAddComment.setVisibility(View.VISIBLE);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("fail to add comment : " + e.getMessage());
                    }
                });


            }
        });


        String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(imgPost);

        String userPostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userPostImage).into(imgUserPost);

        String postTitle=getIntent().getExtras().getString("title");
        txtPostTitle.setText(postTitle);

        String data=getIntent().getExtras().getString("postDate");


        String description = getIntent().getExtras().getString("description");
        txtPostDescription.setText(description);

        //String  currentUserImg= mAuth.getCurrentUser().getPhotoUrl().toString();
        Glide.with(this).load(mUser.getPhotoUrl()).into(imgCurrentUser);

        postKey=getIntent().getExtras().getString("postKey");
        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        txtPostDateName.setText(date);

        iniRvCmment();

    }

    private void iniRvCmment() {

        rvComment.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(postKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                commentList = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    Comment comment =snap.getValue(Comment.class);
                    commentList.add(comment);

                }

                commentAdapter = new CommentAdapter(getApplicationContext(),commentList);
                rvComment.setAdapter(commentAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String timestampToString(long time){

        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy",calendar).toString();

        return date;



    }


}
