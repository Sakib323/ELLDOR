package com.itsolution.demo_recycler_view_with_firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
    Context context;
    List<StudentModel> studentModelList;

    private final RecyclerViewInterface recyclerViewInterface;

    public StudentAdapter(Context context, List<StudentModel> studentModelList,RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.studentModelList = studentModelList;
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.design_row_for_recycler_view,parent,false);
        return new ViewHolder(v,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StudentModel studentModel=studentModelList.get(position);
        holder.tvFirst.setText("Shop name: "+studentModel.getFirstName());
        holder.tvLast.setText("Shop contact number: "+studentModel.getLastName());
        String imageUri=null;
        imageUri=studentModel.getImage();
        Picasso.get().load(imageUri).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return studentModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvFirst,tvLast;
        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image_recyclerView_id);
            tvFirst=itemView.findViewById(R.id.tvfirstname_recyclerView_id);
            tvLast=itemView.findViewById(R.id.tvlastname_recyclerView_id);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(StudentAdapter.this.recyclerViewInterface !=null){
                        int pos=getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            StudentAdapter.this.recyclerViewInterface.OnclickItem(pos);
                        }
                    }
                }
            });
        }
    }
}
