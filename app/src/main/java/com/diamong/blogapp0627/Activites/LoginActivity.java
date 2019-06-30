package com.diamong.blogapp0627.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.diamong.blogapp0627.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    private EditText userEmail, userPassword;
    private Button btnLogin;
    private ProgressBar loginProgress;
    private ImageView loginPhoto;

    private FirebaseAuth mAuth;

    private Intent HomeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.loginMail);
        userPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.loginBtn);
        loginProgress = findViewById(R.id.loginProgressBar);
        loginPhoto = findViewById(R.id.loginUserPhoto);
        loginProgress.setVisibility(View.INVISIBLE);

        HomeActivity = new Intent(this, com.diamong.blogapp0627.Activites.HomeNaviMenu.class);

        mAuth = FirebaseAuth.getInstance();

        loginPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginProgress.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                final String mail = userEmail.getText().toString();
                final String password = userPassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty()) {
                    showMesage("Pleas Verify All Fields");
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                } else {
                    signIn(mail, password);
                }


            }
        });

    }

    private void signIn(String mail, String password) {

        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    loginProgress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    showMesage("Login Successfully");
                    updateUi();


                } else {

                    showMesage(task.getException().getMessage());

                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);


                }

            }
        });


    }

    private void updateUi() {

        startActivity(HomeActivity);
        finish();


    }

    private void showMesage(String message) {

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            updateUi();
        }
    }
}
