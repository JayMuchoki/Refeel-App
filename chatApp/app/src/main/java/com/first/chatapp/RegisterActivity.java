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

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout inputemail,inputpassword,inputconfirmpassword;
    Button btnRegister;
    TextView alreadyhaveanaccount;
    FirebaseAuth mAuth;
    ProgressDialog mLoadingBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inputemail=findViewById(R.id.inputemail);
        inputpassword=findViewById(R.id.inputpassword);
        inputconfirmpassword=findViewById(R.id.inputconfirmpassword);
        btnRegister=findViewById(R.id.btnLogin);
        alreadyhaveanaccount=findViewById(R.id.createnewaccount);
        mAuth= FirebaseAuth.getInstance();
        mLoadingBar= new ProgressDialog(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AtemptRegistration();
            }
        });
        alreadyhaveanaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void AtemptRegistration() {
        String email=inputemail.getEditText().getText().toString();
        String password=inputpassword.getEditText().getText().toString();
        String confirmPassword=inputconfirmpassword.getEditText().getText().toString();
        if (email.isEmpty() || !email.contains("@gmail.com")){
            showError(inputemail,"email is not valid");
        }else if (password.isEmpty() || password.length()<5){
            showError(inputpassword,"Password must be Greater than 5 Letters");
        }else if (!confirmPassword.equals(password)){
            showError(inputconfirmpassword,"password did not match");
        }
        else
        {
            mLoadingBar.setTitle("Registration");
            mLoadingBar.setMessage("Please wait ,while Checking your Credentials");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        mLoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registration Is Successful", Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(RegisterActivity.this,setupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else {
                        mLoadingBar.dismiss();

                        Toast.makeText(RegisterActivity.this, "Registration Is Failed", Toast.LENGTH_SHORT).show();

                    }
                }
            });



        }

    }

    private void showError(TextInputLayout field, String  text){
        field.setError(text);
        field.requestFocus();
    }
}