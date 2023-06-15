package com.example.ptacappliaction.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ptacappliaction.R;
import com.example.ptacappliaction.activities.MainActivity;
import com.example.ptacappliaction.models.SchMileageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleMileageFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    DatabaseReference databaseReference;
    View view;
    Context context;
    TextView tvDate;
    Spinner spnOilType;
    ImageView imgDate;
    EditText edtKM, edtNextService;
    Button btnSchedule;
    String chooseDate="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sch_mileage, container, false);
        context = container.getContext();

        String userId = MainActivity.userId;
        databaseReference = FirebaseDatabase.getInstance().getReference("ScheduleMileage").child(userId);

        tvDate = view.findViewById(R.id.tvDate);
        imgDate = view.findViewById(R.id.imgDate);
        edtKM = view.findViewById(R.id.edtKM);
        edtNextService = view.findViewById(R.id.edtNextService);
        spnOilType = view.findViewById(R.id.spnOilType);
        btnSchedule = view.findViewById(R.id.btnSchedule);

        List<String> list = new ArrayList<>();
        list.add("Select oil type?");
        list.add("5000km");
        list.add("7000km");
        list.add("10000km");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String >(context,android.R.layout.simple_list_item_1,list);
        spnOilType.setAdapter(adapter);

        imgDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });
        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String km = edtKM.getText().toString().trim();
                String nextService = edtNextService.getText().toString().trim();
                String oilType = spnOilType.getSelectedItem().toString();

                if(chooseDate.isEmpty()){
                    Toast.makeText(context, "Please select date!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(km.isEmpty()){
                    edtKM.setError("Required!");
                    edtKM.requestFocus();
                    return;
                }
                if(nextService.isEmpty()){
                    edtNextService.setError("Required!");
                    edtNextService.requestFocus();
                    return;
                }
                if(oilType.equals("Select oil type?")){
                    Toast.makeText(context, "Please select oil type!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String id = databaseReference.push().getKey();
                SchMileageModel model = new SchMileageModel(id,chooseDate,km,nextService,oilType);
                databaseReference.child(id).setValue(model);
                Toast.makeText(context, "Mileage scheduled successfully", Toast.LENGTH_SHORT).show();
                edtKM.setText("");
                edtKM.requestFocus();
                edtNextService.setText("");
                tvDate.setText("Select Date ---->");
                spnOilType.setSelection(0);
                chooseDate="";
            }
        });

        return view;
    }

    private void selectDate() {
        try {

            final Calendar calendar = Calendar.getInstance();
            final int day = calendar.get(Calendar.DAY_OF_MONTH);
            final int month = calendar.get(Calendar.MONTH);
            final int year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
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
