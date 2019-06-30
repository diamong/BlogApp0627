package com.diamong.blogapp0627.Activites;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.diamong.blogapp0627.Models.Post;
import com.diamong.blogapp0627.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import Fragment.HomeFragment;
import Fragment.ProfileFragment;
import Fragment.SettingsFragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeNaviMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUESTCODE = 2;
    private static final int PReqcode = 2;


    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Dialog popAddPost;
    ImageView popupPostImage, popupAddBtn;
    TextView popupTitle, popupDescription;
    ProgressBar popupProgress;
    CircleImageView popupUserPhoto;
    private Uri pickedImgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_navi_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        iniPopup();
        setupPopupImageClick();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                popAddPost.show();
                Glide.with(getApplicationContext()).load(currentUser.getPhotoUrl()).into(popupUserPhoto);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        updateNaviHeader();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
    }

    private void setupPopupImageClick() {

        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermission();
            }
        });

    }

    private void checkAndRequestPermission() {

        if (ContextCompat.checkSelfPermission
                (HomeNaviMenu.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (HomeNaviMenu.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "Please accept for required permission", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions
                        (HomeNaviMenu.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PReqcode);
            }
        } else {
            openGallery();
        }


    }

    private void openGallery() {
        //Todo: open gallery intent and wail for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {

            //the user successfully picked an image
            //we need to save its refrence to Uri variable

            pickedImgUri = data.getData();
            popupPostImage.setImageURI(pickedImgUri);


        }

    }

    private void iniPopup() {

        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        popupUserPhoto = popAddPost.findViewById(R.id.popup_user_photo);
        popupPostImage = popAddPost.findViewById(R.id.popup_img);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        popupProgress = popAddPost.findViewById(R.id.popup_progressBar);

        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Glide.with(getApplicationContext()).load(currentUser.getPhotoUrl()).into(popupUserPhoto);


                popupProgress.setVisibility(View.VISIBLE);
                popupAddBtn.setVisibility(View.INVISIBLE);

                if (!popupTitle.getText().toString().isEmpty()
                        && !popupDescription.getText().toString().isEmpty()
                        && pickedImgUri != null) {

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());

                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String imageDownloadLink = uri.toString();

                                    Post post = new Post(
                                            popupTitle.getText().toString()
                                            , popupDescription.getText().toString()
                                            , imageDownloadLink
                                            , currentUser.getUid(), currentUser.getPhotoUrl().toString());

                                    addPost(post);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    showMessage(e.getMessage());
                                    popupProgress.setVisibility(View.INVISIBLE);
                                    popupAddBtn.setVisibility(View.VISIBLE);


                                }
                            });
                        }
                    });


                } else {
                    showMessage("Please verify all input fields and choose Post image");
                    popupProgress.setVisibility(View.INVISIBLE);
                    popupAddBtn.setVisibility(View.VISIBLE);
                }


            }
        });


    }

    private void addPost(Post post) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("BlogPosts").push();

        String key = myRef.getKey();
        post.setPostKey(key);

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post Added successfully");
                popupProgress.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        });



    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_navi_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();

        } else if (id == R.id.nav_settings) {
            getSupportActionBar().setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();

        } else if (id == R.id.nav_signout) {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNaviHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.navi_username);
        TextView navUserMail = headerView.findViewById(R.id.navi_user_email);
        ImageView navUserPhoto = headerView.findViewById(R.id.navi_user_photo);

        navUserMail.setText(currentUser.getEmail());
        navUserName.setText(currentUser.getDisplayName());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhoto);

    }
}
