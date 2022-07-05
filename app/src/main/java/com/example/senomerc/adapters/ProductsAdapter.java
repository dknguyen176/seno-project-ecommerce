package com.example.senomerc.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.senomerc.R;
import com.example.senomerc.activities.DetailedActivity;
import com.example.senomerc.fragments.HomeFragment;
import com.example.senomerc.helper.Currency;
import com.example.senomerc.model.ProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter < ProductsAdapter.ViewHolder >  {

    private Context context;
    private List<ProductsModel> list;
    private int layoutId;

    public ProductsAdapter(Context context, List<ProductsModel> list, int layoutId) {
        this.context = context;
        this.list = list;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ProductsModel productsModel = list.get(position);

        Glide.with(context).load(productsModel.getImg_url()).into(holder.img);
        holder.name.setText(productsModel.getName());
        holder.price.setText(Currency.toVND(productsModel.getPrice()));
        holder.favorite.setImageResource(productsModel.isFavorite()
                ? R.drawable.pink_heart
                : R.drawable.touch);
        holder.favorite.setTag(productsModel.isFavorite());
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean tag = (boolean) holder.favorite.getTag();
                HomeFragment.onClickFavorites(holder.favorite, productsModel);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("product", productsModel);

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
        TextView name, price;
        ImageView favorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.new_img);
            name = itemView.findViewById(R.id.new_product_name);
            price = itemView.findViewById(R.id.new_price);
            favorite = itemView.findViewById(R.id.product_favorite);
        }
    }
}
