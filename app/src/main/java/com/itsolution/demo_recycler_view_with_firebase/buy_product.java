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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class buy_product extends AppCompatActivity {

    String shop_name,price,product_name,total_price;
    TextInputEditText address,phone_number,item_nmbr;
    String add,phn,item;
    CardView btn;
    String email_address,phn_number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_product);





        Intent intent1=getIntent();

        shop_name=intent1.getStringExtra("shopname");

        DatabaseReference send_email_to_user=FirebaseDatabase.getInstance().getReference().child("User").child(shop_name).child("business_email");


        DatabaseReference send_msg_to_user=FirebaseDatabase.getInstance().getReference().child("User").child(shop_name).child("business_phone");
        send_email_to_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email_address=snapshot.getValue(String.class);

                if (email_address==null){
                    email_address="";
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send_msg_to_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                phn_number=snapshot.getValue(String.class);
                Toast.makeText(buy_product.this, "phn is "+phn_number, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        if (ContextCompat.checkSelfPermission(buy_product.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(buy_product.this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent=getIntent();
        price=intent.getStringExtra("itemprice");

        product_name=intent.getStringExtra("productname");
        address=findViewById(R.id.address);
        phone_number=findViewById(R.id.phone);
        item_nmbr=findViewById(R.id.nmbr_item);
        btn=findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                add=address.getText().toString();
                phn=phone_number.getText().toString();
                item=item_nmbr.getText().toString();

                if(add != null || phn != null|| item!=null ){


                    int total=Integer.valueOf(price)*Integer.valueOf(item);
                    total_price=String.valueOf(total);



                    JavaMailAPI javaMailAPI = new JavaMailAPI(buy_product.this, email_address, "You got a new order", "Hello! Thanks for listing your item in ell-dor app.You product named "+product_name+" just received an order which is listed on shop named:- "+shop_name+".The customer is looking for "+item+" pieces of this item.Charge "+total_price+" ghc from user with shipping fee.Please ship the item in this address:- "+add+"for further query contact the customer in this number:- "+phn+"\n \n Order information:-\n Product name:- "+product_name+" \n Number of pieces:- "+item+"\n Shipping address:- "+add+" \n Charge+Shipping cost:- "+price+" ");

                    javaMailAPI.execute();

                    send_sms(phn_number);


                    Dialog dialog_=new Dialog(buy_product.this);
                    dialog_.setContentView(R.layout.buying_confirm);
                    dialog_.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
                    dialog_.setCancelable(false);
                    dialog_.show();



                    TextView txt=dialog_.findViewById(R.id.text);
                    txt.setText("You have made a request to buy "+product_name+" from "+shop_name+".Please keep ready the amount which is "+total_price+" gh₵ with shipping cost.If any further query needed then the shop owner will contact you in this number "+phn+" Your address is "+add+" ---Thank you ");
                    CardView btn=dialog_.findViewById(R.id.yes);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent =new Intent(buy_product.this,RetriveDataInRecyclerView.class);
                            startActivity(intent);
                        }
                    });



                    DatabaseReference setOrder=FirebaseDatabase.getInstance().getReference().child("User").child(shop_name).child("shop").child("order");
                    HashMap <String,String> hashMap=new HashMap<>();
                    hashMap.put("firstName",add);
                    hashMap.put("lastName",phn);
                    hashMap.put("total price",total_price);
                    hashMap.put("number of product",item);
                    hashMap.put("product name",product_name);
                    setOrder.push().setValue(hashMap);

                }else{
                    Toast.makeText(buy_product.this, "Empty field!", Toast.LENGTH_SHORT).show();
                }

            }
        });





    }



    private void send_sms(String shop_nmbr) {


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, 1);

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        } else {

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(shop_nmbr, null, "You Got a new order.\nOrder details:-\nProduct name:- "+product_name+"\nNumber of pieces:- "+item+"\nShipping address:- "+add+"\nCharge+Shipping cost:- "+price+"      ", null, null);
            } catch (Exception e) {

            }
        }
    }

}