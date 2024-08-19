package com.first.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout inputemail,inputpassword;
    Button btnlogin;
 TextView createnewaccount ,forgotpasword;
    FirebaseAuth mAuth;
    ProgressDialog mLoadingBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputemail=findViewById(R.id.inputemail);
        inputpassword=findViewById(R.id.inputpassword);
        btnlogin=findViewById(R.id.btnLogin);
        forgotpasword=findViewById(R.id.forgotPassword);
        createnewaccount=findViewById(R.id.createnewaccount);
        mAuth= FirebaseAuth.getInstance();
        mLoadingBar= new ProgressDialog(this);



        createnewaccount.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                 startActivity(intent);
             }
         });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AttemptLogin();
            }
        });
        forgotpasword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void AttemptLogin() {
        String email=inputemail.getEditText().getText().toString();
        String password=inputpassword.getEditText().getText().toString();

        if (email.isEmpty() || !email.contains("@gmail.com")){
            showError(inputemail,"email is not valid");
        }else if (password.isEmpty() || password.length()<5){
            showError(inputpassword,"Password must be Greater than 5 Letters");
        }
        else
        {
            mLoadingBar.setTitle("Login");
            mLoadingBar.setMessage("Please wait ,while Checking your Credentials");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        mLoadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, "Registration Is Successful", Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else {
                        mLoadingBar.dismiss();

                        Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                    }
                }
            });



        }

    }

    private void showError(TextInputLayout field, String  text) {
        field.setError(text);
        field.requestFocus();
    }
}