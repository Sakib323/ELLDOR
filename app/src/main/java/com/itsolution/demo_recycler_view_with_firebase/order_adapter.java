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

public class order_adapter extends RecyclerView.Adapter<order_adapter.ViewHolder> {

    Context context;
    List<new_model> studentModelList;

    private final RecyclerViewInterface recyclerViewInterface;

    public order_adapter(Context context, List<new_model> studentModelList,RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.studentModelList = studentModelList;
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @NonNull
    @Override
    public order_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.design_order_recycler_view,parent,false);
        return new order_adapter.ViewHolder(v,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull order_adapter.ViewHolder holder, int position) {

        new_model studentModel=studentModelList.get(position);
        holder.tvFirst.setText("Customer address: "+studentModel.getFirstName());
        holder.tvLast.setText("Customer contact number: "+studentModel.getLastName());

    }

    @Override
    public int getItemCount() {
        return studentModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFirst,tvLast;
        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            tvFirst=itemView.findViewById(R.id.tvfirstname_recyclerView_id);
            tvLast=itemView.findViewById(R.id.tvlastname_recyclerView_id);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(order_adapter.this.recyclerViewInterface !=null){
                        int pos=getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            order_adapter.this.recyclerViewInterface.OnclickItem(pos);
                        }
                    }
                }
            });
        }
    }


}
