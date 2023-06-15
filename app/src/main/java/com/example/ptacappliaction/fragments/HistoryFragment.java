package com.example.ptacappliaction.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ptacappliaction.R;
import com.example.ptacappliaction.activities.MaintenanceAppointmentActivity;
import com.example.ptacappliaction.activities.RoadAssistActivity;
import com.example.ptacappliaction.activities.ViewMaintenanceHistoryActivity;
import com.example.ptacappliaction.activities.ViewOilChangeHistoryActivity;
import com.example.ptacappliaction.activities.ViewOilServicesActivity;
import com.example.ptacappliaction.activities.ViewRoadAssistHistoryActivity;
import com.example.ptacappliaction.activities.ViewSchMileageHistoryActivity;

public class HistoryFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    View view;
    Context context;
    Button btnOilChange, btnRoadAssist, btnMaintenance, btnSchMileage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);
        context = container.getContext();

        btnOilChange = view.findViewById(R.id.btnOilChange);
        btnRoadAssist = view.findViewById(R.id.btnRoadAssist);
        btnMaintenance = view.findViewById(R.id.btnMaintenance);
        btnSchMileage = view.findViewById(R.id.btnSchMileage);

        btnOilChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ViewOilChangeHistoryActivity.class));
            }
        });
        btnRoadAssist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ViewRoadAssistHistoryActivity.class));
            }
        });
        btnMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ViewMaintenanceHistoryActivity.class));
            }
        });
        btnSchMileage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ViewSchMileageHistoryActivity.class));
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
