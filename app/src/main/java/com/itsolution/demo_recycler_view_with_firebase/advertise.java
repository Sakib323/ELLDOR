package com.itsolution.demo_recycler_view_with_firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class advertise extends AppCompatActivity {
    TextInputLayout shop_name, shop_phone, description, full_address;
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

    public void upload_to_server(){

        String name=shop_name.getEditText().getText().toString().trim();
        String phone=shop_phone.getEditText().getText().toString().trim();
        String shop_description=description.getEditText().getText().toString().trim();
        String address=full_address.getEditText().getText().toString().trim();

        if(name.isEmpty() && phone.isEmpty()&& shop_description.isEmpty() && address.isEmpty() && image_uri==null){
            ProgressDialog progressDialog = new ProgressDialog(advertise.this);
            progressDialog.setTitle("Empty field");
            progressDialog.show();
        }else {



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



                            DatabaseReference root=FirebaseDatabase.getInstance().getReference().child("advertise").child(city_address);
                            HashMap<String,String> UserMap=new HashMap<>();
                            UserMap.put("shop name",name);
                            UserMap.put("shop phone",phone);
                            UserMap.put("description",shop_description);
                            UserMap.put("address",address);
                            UserMap.put("image",task.getResult().toString());
                            root.setValue(UserMap);


                            progressDialog.dismiss();
                            Intent intent = new Intent(advertise.this, seller_dashboard.class);
                            startActivity(intent);
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