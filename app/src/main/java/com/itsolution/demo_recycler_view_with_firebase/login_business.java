package com.itsolution.demo_recycler_view_with_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class login_business extends AppCompatActivity {

    CardView login_btn;
    Button callSignUp ;
    TextInputLayout username1,password2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_business);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();




        callSignUp=findViewById(R.id.sign_up);
        username1=findViewById(R.id.username);
        password2=findViewById(R.id.password);

        final ProgressBar simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);

        login_btn=findViewById(R.id.login);


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                simpleProgressBar.setVisibility(View.VISIBLE);
                String userEnteredUsername=username1.getEditText().getText().toString().trim();
                String userEnteredUserPassword=password2.getEditText().getText().toString().trim();

                DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("User");
                Query checkUser=reference.orderByChild("Business_User").equalTo(userEnteredUsername);
                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            username1.setError(null);
                            username1.setErrorEnabled(false);

                            String passwordFromDB=snapshot.child(userEnteredUsername).child("password").getValue(String.class);
                            if (passwordFromDB.equals(userEnteredUserPassword)){

                                username1.setError(null);
                                username1.setErrorEnabled(false);

                                String nameFromDB=snapshot.child(userEnteredUsername).child("Business_User").getValue(String.class);
                                String phoneNoFromDB=snapshot.child(userEnteredUsername).child("business_phone").getValue(String.class);
                                String emailFromDB=snapshot.child(userEnteredUsername).child("business_email").getValue(String.class);

                                SharedPreferences sharedPref = getSharedPreferences("business_info", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("name", nameFromDB);
                                editor.putString("email", emailFromDB);
                                editor.putString("phoneNo", phoneNoFromDB);
                                editor.apply();


                                Intent intent=new Intent(getApplicationContext(),seller_dashboard.class);
                                startActivity(intent);

                                SharedPreferences game = getSharedPreferences("register_done", MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = game.edit();
                                editor1.putBoolean("preview",true);
                                editor1.putBoolean("state",true);
                                editor1.apply();


                            }
                            else {
                                password2.setError("Wrong Password");
                                password2.requestFocus();
                                simpleProgressBar.setVisibility(View.INVISIBLE);

                                SharedPreferences game = getSharedPreferences("register_done", MODE_PRIVATE);
                                SharedPreferences.Editor editor = game.edit();
                                editor.putBoolean("state",false);
                                editor.apply();
                            }

                        }

                        else {

                            username1.setError("No Such User");
                            username1.requestFocus();
                            simpleProgressBar.setVisibility(View.INVISIBLE);

                            SharedPreferences game = getSharedPreferences("register_done", MODE_PRIVATE);
                            SharedPreferences.Editor editor = game.edit();
                            editor.putBoolean("state",false);
                            editor.apply();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(login_business.this,Register_business.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}