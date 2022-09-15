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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.net.URI;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    TextInputLayout edtFirst,edtLast;
    CardView btninsert;
    ProgressBar progressDialog;
    Boolean reg,shop_or_item;
    String name,email,phone,node;
    public String city_address;
    private static final int Gallery_Code=1;
    Uri image_uri=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();


        SharedPreferences togetlocation = getSharedPreferences("location", MODE_PRIVATE);
        city_address = togetlocation.getString("city",null);
        if(city_address==null){
            city_address="unknown";
        }


        SharedPreferences getRegState=getSharedPreferences("register_done",MODE_PRIVATE);
        reg=getRegState.getBoolean("state",false);
        shop_or_item=getRegState.getBoolean("shop_setup",false);



        btninsert=findViewById(R.id.btn);
        mDatabase=FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference().child(city_address);
        mStorage=FirebaseStorage.getInstance();
        progressDialog=new ProgressBar(this);

        SharedPreferences user_data=getSharedPreferences("business_info",MODE_PRIVATE);
        name=user_data.getString("name","can't get name");
        email=user_data.getString("email","can't get email");
        phone=user_data.getString("phoneNo","cant get phone number");


        edtFirst=findViewById(R.id.text1);

        edtLast=findViewById(R.id.text2);

        if (shop_or_item==false){
            edtFirst.setHint("Type your product name");
            edtLast.setHint("Product price with shipping cost in gh₵");
        }







        btninsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,Gallery_Code);
            }
        });
    }

    public void upload_to_server(){

        String fn=edtFirst.getEditText().getText().toString().trim();
        String ln=edtLast.getEditText().getText().toString().trim();

        if(fn.isEmpty() && ln.isEmpty() && image_uri==null){
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Empty field");
            progressDialog.show();
        }else {



            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference filepath = mStorage.getReference().child("imagePost").child(image_uri.getLastPathSegment());
            filepath.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {


                            DatabaseReference root = null;

                            if(shop_or_item==true){
                                root=FirebaseDatabase.getInstance().getReference().child("User").child(name).child("shop").child(name);
                                SharedPreferences sharedPreferences=getSharedPreferences("register_done",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putBoolean("shop_setup",false);
                                editor.apply();

                                String t = task.getResult().toString();
                                DatabaseReference newPost = mRef.child(name+"_"+phone+"_"+fn);
                                newPost.child("firstName").setValue(name);
                                newPost.child("lastName").setValue(ln);
                                newPost.child("image").setValue(task.getResult().toString());


                            }
                            if(shop_or_item==false){
                                  root=FirebaseDatabase.getInstance().getReference().child("User").child(name).child("shop").child("shop_item").child("item").child(name+"_"+phone+"_"+fn);
                            }
                            HashMap<String,String>UserMap=new HashMap<>();
                            UserMap.put("firstName",fn);
                            UserMap.put("lastName",ln);
                            UserMap.put("image",task.getResult().toString());
                            root.setValue(UserMap);


                            progressDialog.dismiss();


                            Intent intent = new Intent(MainActivity.this, RetriveDataInRecyclerView.class);
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
        if(requestCode==Gallery_Code && resultCode==RESULT_OK){
            image_uri=data.getData();
            upload_to_server();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}