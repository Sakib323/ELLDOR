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
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RetriveDataInRecyclerView extends AppCompatActivity {
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    RecyclerView recyclerView;
    StudentAdapter studentAdapter;
    String city_address;
    ImageButton host_new_business,upt_loc,google_map,user;
    TextView tv_loc,register_state,user_number;
    Boolean register=false;
    public int active_user_number;
    CountDownTimer count;

    List<StudentModel> studentMdlList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrive_data_in_recycler_view);




        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();




        register_state=findViewById(R.id.registered_state);
        SharedPreferences togetlocation = getSharedPreferences("location", MODE_PRIVATE);
        city_address = togetlocation.getString("city",null);
        if(city_address==null){
            city_address="unknown";
        }
        if(city_address==null || city_address=="unknown"){

            Dialog dialog1=new Dialog(RetriveDataInRecyclerView.this);
            dialog1.setContentView(R.layout.add_location);
            dialog1.getWindow().setBackgroundDrawableResource(R.drawable.bg_with_radius);
            dialog1.setCancelable(false);
            dialog1.show();
            CardView yes_btn=dialog1.findViewById(R.id.yes);
            CardView no_btn=dialog1.findViewById(R.id.no);
            yes_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(RetriveDataInRecyclerView.this,Location.class);
                    startActivity(intent);
                }
            });
            no_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog1.dismiss();
                }
            });
        }

        SharedPreferences getRegState=getSharedPreferences("register_done",MODE_PRIVATE);
        register=getRegState.getBoolean("state",false);
        if (register==true){
            register_state.setText("Yes");
        }


        mDatabase=FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference().child(city_address);
        mStorage=FirebaseStorage.getInstance();
        recyclerView=findViewById(R.id.recyclerview_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        count=new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long l) {
                active();
            }

            @Override
            public void onFinish() {
                DatabaseReference active_user=FirebaseDatabase.getInstance().getReference().child("active_user").child("number");
                int new_user=active_user_number+1;
                active_user.setValue(new_user);
                user_number=findViewById(R.id.active_user);
                user_number.setText(String.valueOf(active_user_number));
            }
        }.start();



        tv_loc=findViewById(R.id.show_location);
        tv_loc.setText(city_address);



        user=findViewById(R.id.forward_arrow);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(register==true){
                    Intent intent = new Intent(RetriveDataInRecyclerView.this,seller_dashboard.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(RetriveDataInRecyclerView.this,Register_business.class);
                    startActivity(intent);
                }

            }
        });


        google_map=findViewById(R.id.back_arrow);
        google_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RetriveDataInRecyclerView.this,google_map.class);
                startActivity(intent);
            }
        });
        upt_loc=findViewById(R.id.refresh);
        upt_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RetriveDataInRecyclerView.this,Location.class);
                startActivity(intent);
            }
        });
        host_new_business=findViewById(R.id.stop);
        host_new_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(register==true){
                    Intent intent = new Intent(RetriveDataInRecyclerView.this,MainActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(RetriveDataInRecyclerView.this,Register_business.class);
                    startActivity(intent);
                }
            }
        });

        studentMdlList=new ArrayList<StudentModel>();
        studentAdapter=new StudentAdapter(RetriveDataInRecyclerView.this,studentMdlList);


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

    public void show_advertise(){

        //advertising here
        DatabaseReference advertise_info=FirebaseDatabase.getInstance().getReference().child("advertise").child(city_address);
        advertise_info.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){


                    Dialog dialog1=new Dialog(RetriveDataInRecyclerView.this);
                    dialog1.setContentView(R.layout.advertise_from_database);
                    dialog1.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
                    dialog1.setCancelable(false);
                    dialog1.show();


                    TextView name=dialog1.findViewById(R.id.shop_name);
                    name.setText(snapshot.child("shop name").getValue(String.class));
                    TextView phone=dialog1.findViewById(R.id.shop_phone);
                    phone.setText(snapshot.child("shop phone").getValue(String.class));
                    TextView address=dialog1.findViewById(R.id.address);
                    address.setText(snapshot.child("address").getValue(String.class));
                    TextView description=dialog1.findViewById(R.id.description);
                    description.setText(snapshot.child("description").getValue(String.class));
                    ImageView shop_img=dialog1.findViewById(R.id.shop_image);
                    String img_url=snapshot.child("image").getValue(String.class);
                    Picasso.get().load(img_url).into(shop_img);

                    CardView close=dialog1.findViewById(R.id.close);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog1.dismiss();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    public void active(){

        DatabaseReference active_user=FirebaseDatabase.getInstance().getReference().child("active_user").child("number");
        active_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    long data=snapshot.getValue(long.class);
                    active_user_number=(int) data;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        show_advertise();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseReference active_user=FirebaseDatabase.getInstance().getReference().child("active_user").child("number");

        if(active_user_number<=0){
            active_user.setValue(0);
        }else{
            active_user.setValue(active_user_number-1);
        }


    }
}