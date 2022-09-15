package com.itsolution.demo_recycler_view_with_firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.androidbrowserhelper.playbilling.provider.PaymentActivity;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class payment_activity extends AppCompatActivity {
    Card card;
    Charge charge;
    Dialog dialog;
    Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        PaystackSdk.initialize(getApplicationContext());


        Button pay=findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay();
            }
        });


    }


    public void pay() {
        int expiryMonth = 0;
        int expiryYear = 0;
        TextInputEditText etCardNumber = findViewById(R.id.card_number);
        TextInputEditText etExpiryMonth = findViewById(R.id.month);
        TextInputEditText etExpiryYear = findViewById(R.id.year);
        TextInputEditText etCVC = findViewById(R.id.cvc);

        String cardNumber = etCardNumber.getText().toString();
        if(!etExpiryMonth.getText().toString().isEmpty()) {
            expiryMonth = Integer.parseInt(etExpiryMonth.getText().toString());
        }
        if(!etExpiryMonth.getText().toString().isEmpty()) {
            expiryYear = Integer.parseInt(etExpiryYear.getText().toString());
        }
        String cvv = etCVC.getText().toString();

        Card card = new Card(cardNumber, expiryMonth, expiryYear, cvv);
        if (card.isValid()) {
            charge = new Charge();
            charge.setCard(card);

            dialog = new ProgressDialog(payment_activity.this);
            dialog.setTitle("Performing transaction... please wait");
            dialog.show();

            charge.setAmount(0);
            charge.setEmail("sakibahmed2018go@gmail.com");
            charge.setReference("ChargedFromAndroid_" + Calendar.getInstance().getTimeInMillis());
            try {
                charge.putCustomField("Charged From", "Android SDK");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            chargeCard();
        }
        else {
            Toast.makeText(payment_activity.this, "Invalid card details", Toast.LENGTH_LONG).show();
        }
    }

    private void dismissDialog() {
        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void chargeCard() {
        transaction = null;
        PaystackSdk.chargeCard(payment_activity.this, charge, new Paystack.TransactionCallback() {
            // This is called only after transaction is successful
            @Override
            public void onSuccess(Transaction transaction) {
                dismissDialog();

                payment_activity.this.transaction = transaction;
                Toast.makeText(payment_activity.this, transaction.getReference(), Toast.LENGTH_LONG).show();
                new verifyOnServer().execute(transaction.getReference());
            }

            // This is called only before requesting OTP
            // Save reference so you may send to server if
            // error occurs with OTP
            // No need to dismiss dialog
            @Override
            public void beforeValidate(Transaction transaction) {
                payment_activity.this.transaction = transaction;
                Toast.makeText(payment_activity.this, transaction.getReference(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                // If an access code has expired, simply ask your server for a new one
                // and restart the charge instead of displaying error
                payment_activity.this.transaction = transaction;
                if (error instanceof ExpiredAccessCodeException) {
                    payment_activity.this.chargeCard();
                    return;
                }

                dismissDialog();

                if (transaction.getReference() != null) {
                    Toast.makeText(payment_activity.this, transaction.getReference() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    new verifyOnServer().execute(transaction.getReference());
                } else {
                    Toast.makeText(payment_activity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class verifyOnServer extends AsyncTask<String, Void, String> {
        private String reference;
        private String error;

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Toast.makeText(payment_activity.this, String.format("Gateway response: %s", result), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(payment_activity.this, String.format("There was a problem verifying %s on the backend: %s ", this.reference, error), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        }

        @Override
        protected String doInBackground(String... reference) {
            try {
                this.reference = reference[0];
                String json = String.format("{\"reference\":\"%s\"}", this.reference);
                String url1 = "https://www.serverdomain.com/app/verify.php?details=" + json;
                URL url = new URL(url1);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine;
                inputLine = in.readLine();
                in.close();
                return inputLine;
            }catch (Exception e) {
                error = e.getClass().getSimpleName() + ": " + e.getMessage();
            }
            return null;
        }
    }


}