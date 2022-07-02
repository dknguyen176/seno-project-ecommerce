package com.example.senomerc.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.senomerc.R;
import com.example.senomerc.activities.DetailedActivity;
import com.example.senomerc.helper.Currency;
import com.example.senomerc.model.ProductsModel;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter < ProductsAdapter.ViewHolder >  {

    private Context context;
    private List<ProductsModel> list;
    private String specialAttr;
    private int layoutId;

    public ProductsAdapter(Context context, List<ProductsModel> list, String specialAttr, int layoutId) {
        this.context = context;
        this.list = list;
        this.specialAttr = specialAttr;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(list.get(position).getImg_url()).into(holder.img);
        holder.name.setText(list.get(position).getName());
        holder.price.setText(Currency.toVND(list.get(position).getPrice()));
        //holder.specAttr.setText(specialAttr);

        ProductsModel newProductsModel = list.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("product", newProductsModel);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView name, price, specAttr;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.new_img);
            name = itemView.findViewById(R.id.new_product_name);
            price = itemView.findViewById(R.id.new_price);
            // specAttr = itemView.findViewById(R.id.specialAttribute);
        }
    }
}
