package com.example.senomerc.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.senomerc.R;
import com.example.senomerc.activities.DetailedActivity;
import com.example.senomerc.model.MyCartModel;

import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter < MyCartAdapter.ViewHolder >  {

    private Context context;
    private List<MyCartModel> list;

    public MyCartAdapter(Context context, List<MyCartModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int totalPrice = list.get(position).getTotalPrice();
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.img);
        holder.name.setText(list.get(position).getName());
        holder.price = list.get(position).getPrice();
        holder.total.setText(String.format("%d.%dđ", totalPrice / 1000, totalPrice % 1000));
        holder.quantity.setText(String.valueOf(list.get(position).getQuantity()));

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(holder.quantity.getText().toString());
                if (count < 99) {
                    count = count + 1;
                    int totalPrice = holder.price * count;
                    holder.quantity.setText(String.format("%d", count));
                    holder.total.setText(String.format("%d.%dđ", totalPrice / 1000, totalPrice % 1000));
                }
            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(holder.quantity.getText().toString());
                if (count > 1) {
                    count = count - 1;
                    int totalPrice = holder.price * count;
                    holder.quantity.setText(String.format("%d", count));
                    holder.total.setText(String.format("%d.%dđ", totalPrice / 1000, totalPrice % 1000));
                }
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView name, total, quantity;
        ImageButton minus, plus, cancel;

        int price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.cart_img);
            name = itemView.findViewById(R.id.cart_name);
            total = itemView.findViewById(R.id.cart_price);
            quantity = itemView.findViewById(R.id.cart_quantity);
            minus = itemView.findViewById(R.id.cart_remove);
            plus = itemView.findViewById(R.id.cart_add);
            cancel = itemView.findViewById(R.id.cart_cancel);

        }
    }
}
