package com.itsolution.demo_recycler_view_with_firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RetriveDataInRecyclerView extends AppCompatActivity implements RecyclerViewInterface {
    FirebaseDatabase mDatabase;
    DatabaseReference mRef,gettoshop;
    FirebaseStorage mStorage;
    RecyclerView recyclerView;
    StudentAdapter studentAdapter;
    int current_date;
    String city_address;
    LinearLayout upt_loc,google_map,user;
    ExtendedFloatingActionButton host_new_business;
    TextView tv_loc,register_state,user_number;
    Boolean register=false;
    public int active_user_number;
    CountDownTimer count;
    private RewardedAd mRewardedAd;
    List<StudentModel> studentMdlList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrive_data_in_recycler_view);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadRewardedAd();
            }
        });


        Date date=new Date();
        current_date=date.getDate();

        Dialog dialog_=new Dialog(RetriveDataInRecyclerView.this);
        dialog_.setContentView(R.layout.loc_upd);
        dialog_.getWindow().setBackgroundDrawableResource(R.drawable.bg_with_radius);
        dialog_.setCancelable(false);
        dialog_.show();
        CardView yes_btn_=dialog_.findViewById(R.id.yes);
        CardView no_btn_=dialog_.findViewById(R.id.no);
        yes_btn_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showrewardedad();
                Intent intent =new Intent(RetriveDataInRecyclerView.this,Location.class);
                startActivity(intent);
            }
        });
        no_btn_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_.dismiss();
            }
        });




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
                    showrewardedad();
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



        user=findViewById(R.id.profile);
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


        google_map=findViewById(R.id.google_map);
        google_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showrewardedad();
                Intent intent = new Intent(RetriveDataInRecyclerView.this,google_map.class);
                startActivity(intent);
            }
        });
        upt_loc=findViewById(R.id.location);
        upt_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showrewardedad();
                Intent intent = new Intent(RetriveDataInRecyclerView.this,Location.class);
                startActivity(intent);
            }
        });
        host_new_business=findViewById(R.id.add_business);
        host_new_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showrewardedad();

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
        studentAdapter=new StudentAdapter(RetriveDataInRecyclerView.this,studentMdlList,this);


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


    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.

                        mRewardedAd = null;

                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;





                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.


                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.


                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.



                                mRewardedAd = null;
                                loadRewardedAd();
                            }
                        });
                    }
                });
    }

    private void showrewardedad(){

        if (mRewardedAd != null) {
            mRewardedAd.show(this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.

                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();





                }
            });
        }
        else {


        }

    }




    public void show_advertise(){

        //advertising here
        DatabaseReference advertise_info=FirebaseDatabase.getInstance().getReference().child("advertise").child(city_address);
        advertise_info.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String expire=snapshot.child("expire_date").getValue(String.class);
                    if(Integer.valueOf(expire)!=current_date){
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


                        CardView item=dialog1.findViewById(R.id.visit_shop);
                        item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String shop_name=snapshot.child("shop name").getValue(String.class);
                                Intent intent=new Intent(RetriveDataInRecyclerView.this,shopitem.class);
                                intent.putExtra("shopname",shop_name);
                                startActivity(intent);
                            }
                        });

                        CardView close=dialog1.findViewById(R.id.close);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog1.dismiss();
                            }
                        });

                    }
                    else{

                        DatabaseReference advertise_info=FirebaseDatabase.getInstance().getReference().child("advertise").child(city_address);
                        advertise_info.removeValue();

                    }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
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

    @Override
    public void OnclickItem(int position) {
        showrewardedad();
        String  data =studentMdlList.get(position).getFirstName();
        Intent intent=new Intent(RetriveDataInRecyclerView.this,shopitem.class);
        intent.putExtra("shopname",data);
        startActivity(intent);
    }
}