package com.example.ptacappliaction.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.ptacappliaction.R;
import com.example.ptacappliaction.activities.LoginActivity;
import com.example.ptacappliaction.activities.MainActivity;
import com.example.ptacappliaction.activities.SignUpActivity;
import com.example.ptacappliaction.models.UserModelClass;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    View view;
    Context context;
    EditText signupUsername, signupEmail,signupPhone, signupPassword;
    Button btnUpdate, btnPassword;
    ImageView imgEdit;
    TextView tvAccount;
    DatabaseReference databaseReference;
    int count = 0;
    String userId;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = container.getContext();

        tvAccount = view.findViewById(R.id.tvAccount);
        imgEdit = view.findViewById(R.id.imgEdit);
        signupEmail = view.findViewById(R.id.email);
        signupUsername = view.findViewById(R.id.username);
        signupPhone = view.findViewById(R.id.phone);
        signupPassword = view.findViewById(R.id.password);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnPassword = view.findViewById(R.id.btnPassword);

        if(MainActivity.key.equals("google")){
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
            if (account != null) {
                // User is signed in
                String userName = account.getDisplayName();
                String email = account.getEmail();

                signupUsername.setText(userName);
                signupEmail.setText(email);

                signupUsername.setEnabled(false);
                signupEmail.setEnabled(false);


                tvAccount.setVisibility(View.VISIBLE);
                imgEdit.setVisibility(View.GONE);
                signupPhone.setVisibility(View.GONE);
                signupPassword.setVisibility(View.GONE);
                btnUpdate.setVisibility(View.GONE);
                btnPassword.setVisibility(View.GONE);
            }
        }else if(MainActivity.key.equals("custom")){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            mAuth = FirebaseAuth.getInstance();

            signupPhone.setText(MainActivity.phone);
            signupUsername.setText(MainActivity.userName);
            signupEmail.setText(MainActivity.email);
            signupPassword.setText(MainActivity.password);

            signupPhone.setEnabled(false);
            signupUsername.setEnabled(false);
            signupEmail.setEnabled(false);
            signupPassword.setEnabled(false);
        }

        //Edit sign clicks code
        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count%2 != 0) {
                    signupUsername.setEnabled(true);
                    signupUsername.requestFocus();
                    signupPhone.setEnabled(true);
                }else {
                    signupUsername.setEnabled(false);
                    signupPhone.setEnabled(false);
                }
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = signupUsername.getText().toString().trim();
                String phone = signupPhone.getText().toString().trim();

                if(username.isEmpty()){
                    signupUsername.setError("Please write new username!");
                    signupUsername.requestFocus();
                    return;
                }
                if(phone.isEmpty()){
                    signupPhone.setError("Please write new phone number!");
                    signupPhone.requestFocus();
                    return;
                }
                databaseReference.child("userName").setValue(username);
                databaseReference.child("phone").setValue(phone);

                signupUsername.setEnabled(false);
                signupPhone.setEnabled(false);

                Toast.makeText(context, "Your account info updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.sendPasswordResetEmail(MainActivity.email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Reset password email link is sent to "+MainActivity.email, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Unable to send, failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return view;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Loading Data...");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
