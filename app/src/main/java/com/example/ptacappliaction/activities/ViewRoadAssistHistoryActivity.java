package com.example.ptacappliaction.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ptacappliaction.R;
import com.example.ptacappliaction.models.RoadAssistModel;
import com.example.ptacappliaction.models.SchMileageModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViewRoadAssistHistoryActivity extends BaseActivity {

    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    TextView textView;
    List<RoadAssistModel> list;
    public static RoadAssistModel model;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_assist_history);

        showProgressDialog("Loading data..");
        String userId = MainActivity.userId;
        databaseReference = FirebaseDatabase.getInstance().getReference("RoadAssistance").child(userId);
        list = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); ;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        textView = findViewById(R.id.textView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                textView.setText("");
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    RoadAssistModel model = snapshot1.getValue(RoadAssistModel.class);
                    list.add(model);
                }
                if(list.size()>0){
                    EventsListAdapter adapter = new EventsListAdapter(ViewRoadAssistHistoryActivity.this,list);
                    recyclerView.setAdapter(adapter);
                }else {
                    textView.setText("No data in History!");
                }
                hideProgressDialog();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressDialog();
            }});
    }
    public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<RoadAssistModel> muploadList;

        public EventsListAdapter(Context context , List<RoadAssistModel> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.services_list_layout, parent , false);
            return (new ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            final RoadAssistModel model = muploadList.get(position);
            holder.tvName.setText("Username: "+model.getUserName());
            holder.tvPrice.setText("Phone: "+model.getPhone());
            holder.tvDuration.setText("Car Model & Year: "+model.getCarModel());
            holder.tvTime.setText("Damage Type: "+model.getDamageType());

            getLocationName(model.getLatitude(),model.getLongitude(),holder.tvWarranty);

            holder.btnAdd.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                    builder.setTitle("Confirmation?");
                    builder.setMessage("Do you want to delete this road assist request?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child(model.getId()).removeValue();
                            muploadList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(mcontext, "this road assist request Deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }

        private void getLocationName(double latitude, double longitude, TextView tvWarranty) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude,longitude, 1);

                if (listAddresses != null && listAddresses.size() > 0) {
                    String address = "";

                    if (listAddresses.get(0).getAddressLine(0) != null) {
                        address += listAddresses.get(0).getAddressLine(0);
                        tvWarranty.setText("Location: "+address);
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
                        tvWarranty.setText("Location: "+address);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public int getItemCount() {
            return muploadList.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder{
            public TextView tvName;
            public TextView tvPrice;
            public TextView tvDuration;
            public TextView tvTime;
            public TextView tvWarranty;
            public RelativeLayout btnAdd;
            public RelativeLayout btnCancel;

            public ImageViewHolder(View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tvName);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvDuration = itemView.findViewById(R.id.tvDuration);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvWarranty = itemView.findViewById(R.id.tvWarranty);
                btnAdd = itemView.findViewById(R.id.btnAdd);
                btnCancel = itemView.findViewById(R.id.btnCancel);
            }
        }
    }
}