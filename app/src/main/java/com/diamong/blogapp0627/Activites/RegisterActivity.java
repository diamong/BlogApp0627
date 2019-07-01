package com.diamong.blogapp0627.Activites;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.diamong.blogapp0627.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    private static final int REQUESTCODE = 1;
    private int PReqcode = 1;

    private EditText userName, userEmail, userPassword, userPassword2;
    private ProgressBar loadingProgress;
    private Button regBtn;

    private FirebaseAuth mAuth;

    ImageView ImgUserPhoto;
    Uri pickedImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.profile_name);
        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        userPassword2 = findViewById(R.id.profile_password2);
        loadingProgress = findViewById(R.id.login_progressbar);
        loadingProgress.setVisibility(View.INVISIBLE);

        mAuth=FirebaseAuth.getInstance();

        regBtn = findViewById(R.id.loginBtn);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                regBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                final String name = userName.getText().toString();
                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();
                String password2 = userPassword2.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty() || !password.equals(password2)) {
                    showMessage("Please Verify all fields");

                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                } else{
                    createAccount(name,email,password);
                }



            }
        });

        ImgUserPhoto = findViewById(R.id.profile_user_photo);
        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestPermission();
                } else {
                    openGallery();
                }


            }
        });
    }

    private void createAccount(final String name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            showMessage("Account created");

                            updateUserInfo(name,pickedImgUri,mAuth.getCurrentUser());

                        }else {
                            showMessage("Account creation failed  "+task.getException().getMessage());
                            regBtn.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);
                        }

                    }
                });



    }

    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

        StorageReference mStorage= FirebaseStorage.getInstance().getReference().child("users_photo");
        final StorageReference imageFilePath=mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            showMessage("Register Complete");
                                            updateUi();
                                        }



                                    }
                                });


                    }
                });



            }
        });


    }

    private void updateUi() {
        Intent homeInent = new Intent(getApplicationContext(),HomeNaviMenu.class);
        startActivity(homeInent);
        finish();
    }

    private void showMessage(String message) {

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();


    }

    private void openGallery() {
        //Todo: open gallery intent and wail for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);


    }

    private void checkAndRequestPermission() {

        if (ContextCompat.checkSelfPermission
                (RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "Please accept for required permission", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions
                        (RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PReqcode);
            }
        } else {
            openGallery();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {

            //the user successfully picked an image
            //we need to save its refrence to Uri variable

            pickedImgUri = data.getData();
            ImgUserPhoto.setImageURI(pickedImgUri);


        }

    }
}
