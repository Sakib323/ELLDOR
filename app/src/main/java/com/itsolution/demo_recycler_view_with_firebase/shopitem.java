package com.itsolution.demo_recycler_view_with_firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class shopitem extends AppCompatActivity implements RecyclerViewInterface {
    DatabaseReference getitem;
    String shop_name;


    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    RecyclerView recyclerView;
    new_adapter studentAdapter;
    String city_address;
    List<StudentModel> studentMdlList;


    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopitem);
        Intent intent=getIntent();

        txt=findViewById(R.id.txt);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();

        shop_name=intent.getStringExtra("shopname");
        getitem= FirebaseDatabase.getInstance().getReference().child("User").child(shop_name).child("shop").child("shop_item").child("item");

        getitem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    txt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Dialog dialog=new Dialog(shopitem.this);
        dialog.setContentView(R.layout.dialog_for_item_and_everything);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bgfordialog2);
        dialog.show();
        dialog.setCancelable(false);
        TextView txt=dialog.findViewById(R.id.text);
        txt.setText("Showing the product from "+shop_name+".You can buy any item by clicking on it.");
        CardView btn=dialog.findViewById(R.id.yes);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        mDatabase=FirebaseDatabase.getInstance();


        mStorage= FirebaseStorage.getInstance();
        recyclerView=findViewById(R.id.recyclerview_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        studentMdlList=new ArrayList<StudentModel>();
        studentAdapter= new new_adapter(shopitem.this,studentMdlList,this);


        recyclerView.setAdapter(studentAdapter);
        getitem.addChildEventListener(new ChildEventListener() {
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


    @Override
    public void OnclickItem(int position) {

        String  price =studentMdlList.get(position).getLastName();
        String product_name=studentMdlList.get(position).getFirstName();
        Intent intent=new Intent(shopitem.this,buy_product.class);
        intent.putExtra("productname",product_name);
        intent.putExtra("itemprice",price);
        intent.putExtra("shopname",shop_name);
        startActivity(intent);

    }
}