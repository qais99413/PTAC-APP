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
import com.example.ptacappliaction.models.OilServiceBookingModel;
import com.example.ptacappliaction.models.RoadAssistModel;
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

public class ViewOilChangeHistoryActivity extends BaseActivity {

    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    TextView textView;
    List<OilServiceBookingModel> list;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oil_change_history);

        showProgressDialog("Loading data..");
        String userId = MainActivity.userId;
        databaseReference = FirebaseDatabase.getInstance().getReference("UserBookedOilServices").child(userId);
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
                    OilServiceBookingModel model = snapshot1.getValue(OilServiceBookingModel.class);
                    list.add(model);
                }
                if(list.size()>0){
                    EventsListAdapter adapter = new EventsListAdapter(ViewOilChangeHistoryActivity.this,list);
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
        private List<OilServiceBookingModel> muploadList;

        public EventsListAdapter(Context context , List<OilServiceBookingModel> uploadList ) {
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

            final OilServiceBookingModel service = muploadList.get(position);
            holder.tvName.setText(service.getTitle());
            holder.tvPrice.setText("OMR"+service.getPrice());
            holder.tvDuration.setText("-"+service.getDuration());
            holder.tvTime.setText("-"+service.getTimeConsumed());
            holder.tvWarranty.setText("-"+service.getWarranty());
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.tvDate.setText(service.getDate());

            holder.btnAdd.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.VISIBLE);

            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                    builder.setTitle("Confirmation?");
                    builder.setMessage("Do you want to cancel this service?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child(service.getId()).removeValue();
                            muploadList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(mcontext, "this booked has been Deleted", Toast.LENGTH_SHORT).show();
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                    builder.setTitle("Confirmation?");
                    builder.setMessage("Do you want to cancel this service?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child(service.getId()).removeValue();
                            muploadList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(mcontext, "this booked has been Deleted", Toast.LENGTH_SHORT).show();
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
            public TextView tvDate;
            public RelativeLayout btnAdd;
            public RelativeLayout btnCancel;

            public ImageViewHolder(View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tvName);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvDuration = itemView.findViewById(R.id.tvDuration);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvWarranty = itemView.findViewById(R.id.tvWarranty);
                tvDate = itemView.findViewById(R.id.tvDate);
                btnAdd = itemView.findViewById(R.id.btnAdd);
                btnCancel = itemView.findViewById(R.id.btnCancel);
            }
        }
    }
}