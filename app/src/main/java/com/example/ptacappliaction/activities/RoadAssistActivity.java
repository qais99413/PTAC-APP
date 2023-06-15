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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ptacappliaction.R;
import com.example.ptacappliaction.models.RoadAssistModel;
import com.example.ptacappliaction.models.SchMileageModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoadAssistActivity extends BaseActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    DatabaseReference databaseReference;
    TextView tvUser, tvPhone, tvLocation;
    ImageView imgLocation;
    EditText edtKM, edtNextService;
    Button btnSchedule;
    double latitude, longitude;
    boolean locFlag = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_assist);

        String userId = MainActivity.userId;
        databaseReference = FirebaseDatabase.getInstance().getReference("RoadAssistance").child(userId);

        tvUser = findViewById(R.id.tvUser);
        tvPhone = findViewById(R.id.tvPhone);
        tvLocation = findViewById(R.id.tvDate);
        edtKM = findViewById(R.id.edtKM);
        edtNextService = findViewById(R.id.edtNextService);
        imgLocation = findViewById(R.id.imgLocation);
        btnSchedule = findViewById(R.id.btnSchedule);

        if(MainActivity.key.equals("google")){
            tvUser.setText("User: "+MainActivity.userName);
            tvPhone.setText("Email: "+MainActivity.email);
        }else if(MainActivity.key.equals("custom")){
            tvUser.setText("User: "+MainActivity.userName);
            tvPhone.setText("Phone: "+MainActivity.phone);
        }

        imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String carModel = edtKM.getText().toString().trim();
                String damageType = edtNextService.getText().toString().trim();

                if(!locFlag){
                    Toast.makeText(getApplicationContext(), "location is need!!, Please select your current location", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(carModel.isEmpty()){
                    edtKM.setError("Car model is Required!");
                    edtKM.requestFocus();
                    return;
                }
                if(damageType.isEmpty()){
                    edtNextService.setError("Please write damage of your car!");
                    edtNextService.requestFocus();
                    return;
                }
                String id = databaseReference.push().getKey();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if(MainActivity.key.equals("google")){
                    RoadAssistModel model = new RoadAssistModel(id,userId,MainActivity.userName,MainActivity.email,carModel,damageType,latitude,longitude);
                    databaseReference.child(id).setValue(model);
                }else if(MainActivity.key.equals("custom")){
                    RoadAssistModel model = new RoadAssistModel(id,userId,MainActivity.userName,MainActivity.phone,carModel,damageType,latitude,longitude);
                    databaseReference.child(id).setValue(model);
                }

                Toast.makeText(getApplicationContext(), "Your Road assist report submitted successfully", Toast.LENGTH_SHORT).show();

                edtKM.setText("");
                edtKM.requestFocus();
                edtNextService.setText("");
                tvLocation.setText("Select your current location");
                locFlag = false;
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
    private void getCurrentLocation() {
        showProgressDialog("Getting current location..");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                locFlag = true;

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    List<Address> listAddresses = geocoder.getFromLocation(latitude,longitude, 1);

                    if (listAddresses != null && listAddresses.size() > 0) {
                        String address = "";

                        if (listAddresses.get(0).getAddressLine(0) != null) {
                            address += listAddresses.get(0).getAddressLine(0);
                            tvLocation.setText("Location: "+address);
                        }else {
                            if (listAddresses.get(0).getThoroughfare() != null) {
                                address += listAddresses.get(0).getThoroughfare() + ", ";
                            }

                            if (listAddresses.get(0).getLocality() != null) {
                                address += listAddresses.get(0).getLocality() + ", ";
                            }

                            if (listAddresses.get(0).getAdminArea() != null) {
                                address += listAddresses.get(0).getAdminArea()+", ";
                            }

                            if (listAddresses.get(0).getCountryName() != null) {
                                address += listAddresses.get(0).getCountryName();
                            }
                            tvLocation.setText("Location: "+address);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) { }
            @Override
            public void onProviderEnabled(String s) { }
            @Override
            public void onProviderDisabled(String s) { }
        };
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5000, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5000, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5000, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5000, locationListener);
            }
        }
    }
}