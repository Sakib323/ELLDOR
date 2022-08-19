package com.itsolution.demo_recycler_view_with_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Location extends AppCompatActivity implements OnUserEarnedRewardListener {
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE=100;
    CardView location;
    TextView tv_loc;
    private RewardedInterstitialAd rewardedInterstitialAd;
    private String TAG = "--->Admob";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(Location.this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadAd();
            }
        });

        Dialog dialog1=new Dialog(Location.this);
        dialog1.setContentView(R.layout.location_dialog);
        dialog1.getWindow().setBackgroundDrawableResource(R.drawable.bg_with_radius);
        dialog1.setCancelable(true);
        dialog1.show();

        SharedPreferences togetlocation = getSharedPreferences("location", MODE_PRIVATE);
        String city_address = togetlocation.getString("city",null);

        tv_loc=findViewById(R.id.show_location);
        tv_loc.setText(city_address);

        location=findViewById(R.id.update_location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_location();

                if (rewardedInterstitialAd!=null) {

                    rewardedInterstitialAd.show(Location.this,/*
    OnUserEarnedRewardListener */ Location.this);
                }else{
                    Toast.makeText(Location.this,"Add not loaded",Toast.LENGTH_SHORT).show();
                    Log.e(TAG,"Ad not loaded");
                }
            }
        });


    }





    private void loadAd() {

        RewardedInterstitialAd.load(Location.this, "ca-app-pub-3940256099942544/5354046379",
                new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {



                        if (rewardedInterstitialAd!=null) {

                            rewardedInterstitialAd.show(Location.this,/*
    OnUserEarnedRewardListener */ Location.this);
                        }else{
                            Toast.makeText(Location.this,"Add not loaded",Toast.LENGTH_SHORT).show();
                            Log.e(TAG,"Ad not loaded");
                        }

                        rewardedInterstitialAd = ad;
                        Log.e(TAG, "onAdLoaded");

                        rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                Log.i(TAG, "onAdFailedToShowFullScreenContent");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                Log.i(TAG, "onAdShowedFullScreenContent");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.i(TAG, "onAdDismissedFullScreenContent");
                                loadAd();
                            }
                        });


                    }
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.e(TAG, "onAdFailedToLoad");
                    }
                });

    }



    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        Toast.makeText(this, "You got reward", Toast.LENGTH_SHORT).show();
    }





    public void get_location(){
        if(ContextCompat.checkSelfPermission(Location.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location location) {
                    if(location!=null){
                        Geocoder geocoder=new Geocoder(Location.this, Locale.getDefault());
                        List<Address> addresses= null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            String city=addresses.get(0).getLocality();

                            SharedPreferences game = getSharedPreferences("location", MODE_PRIVATE);
                            SharedPreferences.Editor editor = game.edit();
                            editor.putString("city",city);
                            editor.apply();


                            Dialog dialog1=new Dialog(Location.this);
                            dialog1.setContentView(R.layout.after_getting_location);
                            dialog1.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
                            dialog1.setCancelable(false);
                            dialog1.show();
                            TextView location_for_text=dialog1.findViewById(R.id.text);
                            if(city!=null){
                                location_for_text.setText("Your location is updated. Your location is now "+city);
                            }else{
                                location_for_text.setText("Can't update your location. May be you haven't turned on your location.Please turn on then try");
                            }
                            ExtendedFloatingActionButton dash=dialog1.findViewById(R.id.goto_dashboard);
                            dash.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            });


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }else{
            ask_permission();
        }
    }
    private void ask_permission(){
        ActivityCompat.requestPermissions(Location.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                get_location();
            }else{
                Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show();
            }

        }
    }
}