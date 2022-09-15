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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class seller_dashboard extends AppCompatActivity {
    String name,email,phone,city_address;
    TextView name_lay,email_lay,phone_lay,location_lay;


    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    RecyclerView recyclerView;
    StudentAdapter studentAdapter;
    LinearLayout advertise;
    LinearLayout upt_loc,user;
    TextView tv_loc;
    boolean preview=false;
    Boolean register=false;
    Boolean shop_or_item=false;

    List<StudentModel> studentMdlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);


        SharedPreferences getRegState=getSharedPreferences("register_done",MODE_PRIVATE);
        shop_or_item=getRegState.getBoolean("shop_setup",false);


        upt_loc=findViewById(R.id.location_);
        upt_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(seller_dashboard.this,order.class);
                startActivity(intent);
            }
        });

        user=findViewById(R.id.profile);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(register==true){
                    Intent intent = new Intent(seller_dashboard.this,RetriveDataInRecyclerView.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(seller_dashboard.this,RetriveDataInRecyclerView.class);
                    startActivity(intent);
                }
            }
        });

        ExtendedFloatingActionButton add_business=findViewById(R.id.add);
        if(shop_or_item==false){
            add_business.setText("ADD PRODUCT TO YOUR SHOP");
        }

        add_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent =new Intent(seller_dashboard.this,MainActivity.class);
                    startActivity(intent);

            }
        });

        SharedPreferences preferences=getSharedPreferences("register_done",MODE_PRIVATE);
        preview=preferences.getBoolean("preview",false);

        if(preview==true){
            SharedPreferences preferences1=getSharedPreferences("register_done",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences1.edit();
            editor.putBoolean("preview",false);
            editor.apply();
            Dialog dialog1=new Dialog(seller_dashboard.this);
            dialog1.setContentView(R.layout.reboot_app);
            dialog1.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
            dialog1.setCancelable(false);
            dialog1.show();
            ExtendedFloatingActionButton reboot=dialog1.findViewById(R.id.reboot);
            reboot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishAffinity();
                }
            });
        }



        name_lay=findViewById(R.id.name);
        email_lay=findViewById(R.id.email);
        phone_lay=findViewById(R.id.phone);
        location_lay=findViewById(R.id.location);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();
        advertise=findViewById(R.id.google_map);
        advertise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(seller_dashboard.this,advertise.class);
                startActivity(intent);
            }
        });


        SharedPreferences togetlocation = getSharedPreferences("location", MODE_PRIVATE);
        city_address = togetlocation.getString("city",null);
        if(city_address==null){
            city_address="unknown";
        }


        SharedPreferences user_data=getSharedPreferences("business_info",MODE_PRIVATE);
        name=user_data.getString("name","can't get name");
        email=user_data.getString("email","can't get email");
        phone=user_data.getString("phoneNo","cant get phone number");

        name_lay.setText(name);
        phone_lay.setText(phone);
        email_lay.setText(email);
        location_lay.setText(city_address);


        mDatabase= FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference().child("User").child(name).child("shop");
        mStorage= FirebaseStorage.getInstance();
        recyclerView=findViewById(R.id.recyclerview_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        studentMdlList=new ArrayList<StudentModel>();
        studentAdapter=new StudentAdapter(seller_dashboard.this,studentMdlList,null);


        recyclerView.setAdapter(studentAdapter);
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                StudentModel studentModel=snapshot.getValue(StudentModel.class);
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}