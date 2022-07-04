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
import com.example.senomerc.activities.AllProductsActivity;
import com.example.senomerc.activities.DetailedActivity;
import com.example.senomerc.model.CategoryModel;
import com.example.senomerc.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter < CategoryAdapter.ViewHolder > {

    private Context context;
    private List<CategoryModel> list;
    private int layoutId;

    public CategoryAdapter(Context context, List<CategoryModel> list, int layoutId) {
        this.context = context;
        this.list = list;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId,parent,false)) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.catImg);
        String name = list.get(position).getName();
        holder.catName.setText(name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AllProductsActivity.class);
                intent.putExtra("title", name);
                intent.putExtra("tags", "");
                if (name.compareToIgnoreCase("all") != 0) intent.putExtra("category", name);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView catImg;
        TextView catName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catImg = itemView.findViewById(R.id.cat_img);
            catName = itemView.findViewById(R.id.cat_name);

        }
    }


}
