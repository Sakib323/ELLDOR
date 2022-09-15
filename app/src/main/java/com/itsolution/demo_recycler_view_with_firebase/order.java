package com.itsolution.demo_recycler_view_with_firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class order extends AppCompatActivity implements RecyclerViewInterface{

    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    RecyclerView recyclerView;
    order_adapter studentAdapter;
    String city_address;
    List<new_model> studentMdlList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);



        Dialog dialog_=new Dialog(order.this);
        dialog_.setContentView(R.layout.dialog_for_item_and_everything);
        dialog_.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
        dialog_.setCancelable(false);
        dialog_.show();

        TextView txt=dialog_.findViewById(R.id.text);
        txt.setText("Showing the customer request whose want to buy your product");
        CardView btn=dialog_.findViewById(R.id.yes);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_.dismiss();
            }
        });


        SharedPreferences sharedPreferences=getSharedPreferences("business_info",MODE_PRIVATE);
        String shop_name=sharedPreferences.getString("name","");
        if(shop_name==null || shop_name==""){

        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();

        DatabaseReference getorder = FirebaseDatabase.getInstance().getReference().child("User").child(shop_name).child("shop").child("order");


        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        recyclerView = findViewById(R.id.recyclerview_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        studentMdlList = new ArrayList<new_model>();
        studentAdapter = new order_adapter(order.this, studentMdlList, this);



        recyclerView.setAdapter(studentAdapter);
        getorder.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                new_model studentModel = snapshot.getValue(new_model.class);
                studentMdlList.add(studentModel);
                studentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void OnclickItem(int position) {
        String  data =studentMdlList.get(position).getFirstName();

        DatabaseReference get_order = FirebaseDatabase.getInstance().getReference().child("User").child(data).child("shop").child("order");


    }
}