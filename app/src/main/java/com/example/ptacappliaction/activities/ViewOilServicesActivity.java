package com.example.ptacappliaction.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ptacappliaction.R;
import com.example.ptacappliaction.models.OilServiceBookingModel;
import com.example.ptacappliaction.models.OilServicesModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewOilServicesActivity extends BaseActivity {

    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    TextView textView;
    List<OilServicesModel> list;
    public static OilServicesModel model;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_oil_services);

        showProgressDialog("Loading services..");
        databaseReference = FirebaseDatabase.getInstance().getReference("OilServices");
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
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                textView.setText("");
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    OilServicesModel model = snapshot.getValue(OilServicesModel.class);
                    list.add(model);
                }
                if(list.size()>0){
                    EventsListAdapter adapter = new EventsListAdapter(ViewOilServicesActivity.this,list);
                    recyclerView.setAdapter(adapter);
                }else {
                    textView.setText("No Oil Service Added!");
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
        private List<OilServicesModel> muploadList;

        public EventsListAdapter(Context context , List<OilServicesModel> uploadList ) {
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

            final OilServicesModel service = muploadList.get(position);
            holder.tvName.setText(service.getTitle());
            holder.tvPrice.setText("OMR"+service.getPrice());
            holder.tvDuration.setText("-"+service.getDuration());
            holder.tvTime.setText("-"+service.getTimeConsumed());
            holder.tvWarranty.setText("-"+service.getWarranty());

            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userId = MainActivity.userId;
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("UserBookedOilServices").child(userId);

                    String id = dbRef.push().getKey();
                    String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                    OilServiceBookingModel model = new OilServiceBookingModel(id,service.getTitle(),service.getPrice(),service.getDuration(),
                            service.getTimeConsumed(),service.getWarranty(),userId, MainActivity.userName,date);

                    dbRef.child(id).setValue(model);
                    Toast.makeText(mcontext, service.getTitle()+" booked successfully", Toast.LENGTH_SHORT).show();
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
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