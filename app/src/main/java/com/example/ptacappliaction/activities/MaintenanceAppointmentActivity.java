package com.example.ptacappliaction.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ptacappliaction.R;
import com.example.ptacappliaction.activities.BaseActivity;
import com.example.ptacappliaction.activities.MainActivity;
import com.example.ptacappliaction.models.BookMaintenanceModel;
import com.example.ptacappliaction.models.RoadAssistModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MaintenanceAppointmentActivity extends BaseActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    DatabaseReference databaseReference;
    TextView tvUser, tvPhone, tvDate, tvTime;
    ImageView imgDate, imgTime;
    Spinner spnServiceType, spnBranch;
    Button btnSchedule;
    String chooseTime="", chooseDate="";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        String userId = MainActivity.userId;
        databaseReference = FirebaseDatabase.getInstance().getReference("MaintenanceBookings").child(userId);

        tvUser = findViewById(R.id.tvUser);
        tvPhone = findViewById(R.id.tvPhone);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        spnServiceType = findViewById(R.id.spnServiceType);
        spnBranch = findViewById(R.id.spnBranch);
        imgDate = findViewById(R.id.imgDate);
        imgTime = findViewById(R.id.imgTime);
        btnSchedule = findViewById(R.id.btnSchedule);

        if(MainActivity.key.equals("google")){
            tvUser.setText("User: "+MainActivity.userName);
            tvPhone.setText("Email: "+MainActivity.email);
        }else if(MainActivity.key.equals("custom")){
            tvUser.setText("User: "+MainActivity.userName);
            tvPhone.setText("Phone: "+MainActivity.phone);
        }

        List<String> list = new ArrayList<>();
        list.add("Select Service?");
        list.add("AC");
        list.add("Tire Repair");
        list.add("Break Repair");
        list.add("Engine");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String >(this,android.R.layout.simple_list_item_1,list);
        spnServiceType.setAdapter(adapter);

        List<String> list2 = new ArrayList<>();
        list2.add("Select Branch?");
        list2.add("AL KHUWAIR");
        list2.add("AL HAIL NORTH");
        list2.add("GHUBRA BEACH");
        list2.add("NIZWA DARIS");
        list2.add("BARKA TOWN");
        list2.add("RUSTAQ");
        list2.add("MUSANNA");
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String >(this,android.R.layout.simple_list_item_1,list2);
        spnBranch.setAdapter(adapter2);

        imgDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });
        imgTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serviceType = spnServiceType.getSelectedItem().toString();
                String branch = spnBranch.getSelectedItem().toString();

                if(chooseDate.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please select maintenance date you need!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(chooseTime.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please select time you will come!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(serviceType.equals("Select Service?")){
                    Toast.makeText(getApplicationContext(), "Please chose service type!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(branch.equals("Select Branch?")){
                    Toast.makeText(getApplicationContext(), "Please chose Branch of PTAC centers!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String id = databaseReference.push().getKey();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if(MainActivity.key.equals("google")){
                    BookMaintenanceModel model = new BookMaintenanceModel(id,userId,MainActivity.userName,MainActivity.email,chooseDate,chooseTime,
                            serviceType,branch);
                    databaseReference.child(id).setValue(model);
                }else if(MainActivity.key.equals("custom")){
                    BookMaintenanceModel model = new BookMaintenanceModel(id,userId,MainActivity.userName,MainActivity.phone,chooseDate,chooseTime,
                            serviceType,branch);
                    databaseReference.child(id).setValue(model);
                }
                Toast.makeText(getApplicationContext(), "Your Booking Request Sent Successfully", Toast.LENGTH_SHORT).show();

                tvDate.setText("Select Date --->");
                tvTime.setText("Select Time --->");
                spnServiceType.setSelection(0);
                spnBranch.setSelection(0);
                chooseDate="";
                chooseTime="";
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5000, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5000, locationListener);
                }
            }
        }
    }
    private void selectDate() {
        try {

            final Calendar calendar = Calendar.getInstance();
            final int day = calendar.get(Calendar.DAY_OF_MONTH);
            final int month = calendar.get(Calendar.MONTH);
            final int year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                    calendar.set(year, month, day);
                    chooseDate = sdf.format(calendar.getTime());
                    tvDate.setText("Selected Date: "+chooseDate);

                    String cDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                }
            }, year, month, day);
            datePicker.setTitle("Select Date");
            datePicker.show();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void selectTime() {
        try {

            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR);
            int mint = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minutes);

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    chooseTime = sdf.format(calendar.getTime());
                    tvTime.setText("Selected Time: "+chooseTime);

                }
            }, hour, mint, true);
            timePicker.setTitle("Select time");
            timePicker.show();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}