package com.example.ptacappliaction.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ptacappliaction.R;
import com.example.ptacappliaction.activities.BaseActivity;
import com.example.ptacappliaction.activities.MainActivity;
import com.example.ptacappliaction.models.FeedbackModel;
import com.example.ptacappliaction.models.RoadAssistModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;

public class FeedbackActivity extends BaseActivity {
    DatabaseReference databaseReference;
    TextView tvUser, tvPhone;
    EditText edtFeedback;
    Button btnSchedule;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        String userId = MainActivity.userId;
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback").child(userId);

        tvUser = findViewById(R.id.tvUser);
        tvPhone = findViewById(R.id.tvPhone);
        edtFeedback = findViewById(R.id.edtFeedback);
        btnSchedule = findViewById(R.id.btnSchedule);

        if(MainActivity.key.equals("google")){
            tvUser.setText("User: "+ MainActivity.userName);
            tvPhone.setText("Email: "+MainActivity.email);
        }else if(MainActivity.key.equals("custom")){
            tvUser.setText("Email: "+ MainActivity.email);
            tvPhone.setText("Phone: "+MainActivity.phone);
        }

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = edtFeedback.getText().toString().trim();

                if(feedback.isEmpty()){
                    edtFeedback.setError("Write your feedback about our services!");
                    edtFeedback.requestFocus();
                    return;
                }
                String id = databaseReference.push().getKey();

                if(MainActivity.key.equals("google")){
                    FeedbackModel model = new FeedbackModel(id,MainActivity.userName,MainActivity.email,MainActivity.userName,feedback);
                    databaseReference.child(id).setValue(model);
                }else if(MainActivity.key.equals("custom")){
                    FeedbackModel model = new FeedbackModel(id,MainActivity.userName,MainActivity.email,MainActivity.phone,feedback);
                    databaseReference.child(id).setValue(model);
                }
                Toast.makeText(getApplicationContext(), "Your feedback Submitted successfully, thanks", Toast.LENGTH_SHORT).show();

                edtFeedback.setText("");
                edtFeedback.requestFocus();
            }
        });

    }
}