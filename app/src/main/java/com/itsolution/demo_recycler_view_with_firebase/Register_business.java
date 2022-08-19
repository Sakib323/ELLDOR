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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Register_business extends AppCompatActivity {

    TextInputLayout  regUsername, regEmail, regPhoneno, regPassword;
    Button regToLoginBtn;
    CardView regBtn;
    FirebaseDatabase rootNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_business);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();


        regUsername = findViewById(R.id.username);
        regEmail = findViewById(R.id.reg_email);
        regPhoneno = findViewById(R.id.reg_phoneNo);
        regPassword = findViewById(R.id.reg_password);
        regBtn = findViewById(R.id.registerUser);
        regToLoginBtn = findViewById(R.id.reg_login_btn);


        final ProgressBar simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);


        regToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Register_business.this,login_business.class);
                startActivity(intent);
            }
        });


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {






                String val2 = regUsername.getEditText().getText().toString();

                if (val2.isEmpty()) {
                    regUsername.setError("field cannot be empty");
                    regUsername.setEnabled(true);
                }
                else {
                    regUsername.setError(null);
                    regUsername.setEnabled(false);
                }

                String val3 = regEmail.getEditText().getText().toString();
                if (val3.isEmpty()) {
                    regEmail.setError("field cannot be empty");
                    regEmail.setEnabled(true);
                }
                else {
                    regEmail.setError(null);
                    regEmail.setEnabled(false);
                }
                String val4 = regPhoneno.getEditText().getText().toString();

                if (val4.isEmpty()) {
                    regPhoneno.setError("field cannot be empty");
                    regPhoneno.setEnabled(true);
                }
                else {
                    regPhoneno.setError(null);
                    regPhoneno.setEnabled(false);
                }

                String val5 = regPassword.getEditText().getText().toString();
                if (val5.isEmpty()) {
                    regPassword.setError("field cannot be empty");
                    regPassword.setEnabled(true);
                }
                else {
                    regPassword.setError(null);
                    regPassword.setEnabled(false);

                }
                if ( val2.isEmpty() || val3.isEmpty() || val4.isEmpty() || val5.isEmpty() )

                {
                    Toast.makeText(Register_business.this, "field cannot be empty", Toast.LENGTH_SHORT).show();

                }else {

                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("User");
                    Query checkUser=reference.orderByChild("business_name").equalTo(val2);
                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                regUsername.setError("This name already exist");
                                regUsername.setErrorEnabled(true);
                                regUsername.getEditText().setText("");


                            } else {


                                SharedPreferences game = getSharedPreferences("register_done", MODE_PRIVATE);
                                SharedPreferences.Editor editor = game.edit();
                                editor.putBoolean("state",true);
                                editor.apply();


                                DatabaseReference reference;
                                simpleProgressBar.setVisibility(View.VISIBLE);
                                rootNode = FirebaseDatabase.getInstance();
                                reference = rootNode.getReference("User");
                                String username = regUsername.getEditText().getText().toString();
                                String email = regEmail.getEditText().getText().toString();
                                String phoneNo = regPhoneno.getEditText().getText().toString();
                                String password = regPassword.getEditText().getText().toString();

                                SharedPreferences sharedPref = getSharedPreferences("business_info", MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = sharedPref.edit();
                                editor1.putString("Business_User",username);
                                editor1.putString("BusinessEmail",email);
                                editor1.putString("BusinessPhone",phoneNo);
                                editor1.apply();



                                HashMap<String,String> UserMap=new HashMap<>();

                                UserMap.put("Business_User",username);
                                UserMap.put("business_email",email);
                                UserMap.put("business_phone",phoneNo);
                                UserMap.put("password",password);
                                reference.child(username).setValue(UserMap);

                                Intent intent=new Intent(Register_business.this,login_business.class);
                                startActivity(intent);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}