package com.example.ptacappliaction.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ptacappliaction.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity {

    EditText LogEmail, LogPassword;
    Button loginButton;
    TextView signupRedirectText;
    ImageView imgGoogle;
    TextView forgotPassword;
    FirebaseAuth auth;
    FirebaseUser mUser;
    ProgressDialog progressDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private GoogleSignInClient mgoogleSignInClient;
    public static final int REQUEST_CODE = 1001;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestProfile()
                        .build();
        mgoogleSignInClient = GoogleSignIn.getClient(this ,googleSignInOptions);

        LogEmail = findViewById(R.id.loginEmail);
        LogPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotpassword);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        imgGoogle = findViewById(R.id.imgGoogle);
        auth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        mUser = auth.getCurrentUser();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerforLogin_720();
            }
        });
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        imgGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SignInIntent = mgoogleSignInClient.getSignInIntent();
                startActivityForResult(SignInIntent , REQUEST_CODE);
            }
        });
// codes for reset password
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.reset_pass, null);
                EditText emailBox = dialogView.findViewById(R.id.email);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btnreset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();
                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                            emailBox.setError("Please Enter registered email account!");

                            return;
                        }
                        auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "reset password link was sent to entered email account, Check your email", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Unable to send process failed, Email that you entered not valid in our DB, try to enter correct email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
                dialogView.findViewById(R.id.btncancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });


    }

    private void PerforLogin_720() {
        String email = LogEmail.getText().toString();
        String password = LogPassword.getText().toString();

        if (!email.matches(emailPattern)) {
            LogEmail.setError("Enter correct email with @gmail.com");

        }else if (password.isEmpty() || password.length() < 6) {
            LogPassword.setError("Enter proper password");
        } else {
            progressDialog.setMessage("Wait while Login...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,"Enter correct email or password"+task.getException(),Toast.LENGTH_LONG).show();

                    }
                }
            });
        }

    }

    private void sendUserToNextActivity() {
        Toast.makeText(LoginActivity.this,"Login successful",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("key","custom");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
    //This method checks user login status..
    @Override
    protected void onStart() {
        super.onStart();

        if(isOnline()){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("key","custom");
            startActivity(intent);
            finish();
        }else {
            // Check if user is signed in with Google Sign-In
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account != null) {
                // User is signed in

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("key","google");
                startActivity(intent);
                finish();
                // TODO: Update UI with signed-in account information.
            } else {
                // User is not signed in
                // TODO: Update UI to reflect this.
            }

        }
    }

    boolean isOnline(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            showProgressDialog("Signing In..");
            Task<GoogleSignInAccount> accounttask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSigin(accounttask);
        }
    }

    private void handleGoogleSigin(Task<GoogleSignInAccount> accounttask) {
        try {
            GoogleSignInAccount account = accounttask.getResult(ApiException.class);
            updateUi(account);
            hideProgressDialog();
        } catch (ApiException e) {
            e.printStackTrace();
            hideProgressDialog();

            Toast.makeText(getApplicationContext() , GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()) + " " , Toast.LENGTH_LONG).show();
            Log.d("error" , GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
            Log.d("error" , ""+e.getStatusCode());
        }
    }
    private void updateUi(GoogleSignInAccount account) {
        if(account !=null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("key","google");
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "Error while sign in", Toast.LENGTH_SHORT).show();
        }
    }

}
