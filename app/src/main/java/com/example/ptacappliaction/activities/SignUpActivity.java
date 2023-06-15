package com.example.ptacappliaction.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ptacappliaction.R;
import com.example.ptacappliaction.models.UserModelClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText signupUsername, signupEmail,signupPhone, signupPassword,signupConfirmPasswod;
    TextView loginRedirectText;
    Button signupBtn;
    DatabaseReference databaseReference;
    String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        signupEmail = findViewById(R.id.email);
        signupUsername = findViewById(R.id.username);
        signupPhone = findViewById(R.id.phone);
        signupPassword = findViewById(R.id.password);
        signupConfirmPasswod = findViewById(R.id.confirmpassword);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupBtn = findViewById(R.id.singupButton);

        progressDialog=new ProgressDialog(this);

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                performSingUp();
            }
        });
    }

    //method
    private void performSingUp() {
        String email = signupEmail.getText().toString().trim();
        String username = signupUsername.getText().toString().trim();
        String phone = signupPhone.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();
        String conformPassword = signupConfirmPasswod.getText().toString().trim();

        if (!email.matches(emailPattern)) {
            signupEmail.setError("Please enter correct email account with @gmail.com.");
        }else if(password.isEmpty()||password.length() < 6 ) {
            signupPassword.setError("Enter proper password");
        }else if(!password.equals(conformPassword)) {
            signupConfirmPasswod.setError("password not match both filed");
        }else if(phone.isEmpty()||username.isEmpty()){
            signupPhone.setError("Please write your phone number");
            signupUsername.setError("Please write username");
        }else if(phone.length()>8){
            signupPhone.setError("Phone number must be 8 digits numbers...!");

        }

        else {
            progressDialog.setMessage("Wait while Registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        saveData(username,phone,email,password);
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this,"Enter correct email and password"+task.getException(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void saveData(String userName, String phone, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    String userId = firebaseUser.getUid();

                    UserModelClass model = new UserModelClass(userId,userName,phone,email,password);
                    databaseReference.child(userId).setValue(model);

                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this,"Registration successful",Toast.LENGTH_LONG).show();

                    sendUserToNextActivity();

                }
            }
        });
    }
    private void sendUserToNextActivity() {
        Intent intent=new Intent(SignUpActivity.this, MainActivity.class);
        intent.putExtra("key","custom");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}