package com.itsolution.demo_recycler_view_with_firebase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;

import co.paystack.android.model.Card;

public class choice_plan extends AppCompatActivity {

    CardView usd_5,usd_10,usd_15,usd_25,usd_50,usd_120,usd_250,usd_400,usd_850,usd_1500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_plan);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();



        usd_5=findViewById(R.id.usd_5);
        usd_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amount = (int) (5*9.94);
                makePayment(amount);
            }
        });



    }


    private void makePayment(int usd) {
        new RavePayManager(this)
                .setAmount(usd)
                .setEmail("test@gmail.com")
                .setCountry("GH")
                .setCurrency("GHS")
                .setfName("Licio")
                .setlName("Lentimo")
                .setNarration("Purchase Goods")
                .setPublicKey("FLWPUBK_TEST-4299efd774e4e6ed73b1f6bf64bae4c3-X")
                .setEncryptionKey("FLWSECK_TEST9b53d509a457")
                .setTxRef(System.currentTimeMillis() + "Ref")
                .acceptAccountPayments(true)
                .acceptCardPayments(true)
                .acceptMpesaPayments(true)
                .onStagingEnv(true)
                .shouldDisplayFee(true)
                .showStagingLabel(true)
                .initialize();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {



                Toast.makeText(this, "SUCCESS " + message, Toast.LENGTH_LONG).show();
            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_LONG).show();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_LONG).show();
            }
        }
    }



}