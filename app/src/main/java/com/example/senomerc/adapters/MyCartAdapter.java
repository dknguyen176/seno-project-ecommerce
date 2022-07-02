package com.example.senomerc.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.senomerc.R;
import com.example.senomerc.activities.DetailedActivity;
import com.example.senomerc.model.MyCartModel;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter < MyCartAdapter.ViewHolder >  {

    private Context context;
    private List<MyCartModel> list;

    int totalAmount;

    private FirebaseFirestore firestore;

    public MyCartAdapter(Context context, List<MyCartModel> list) {
        this.context = context;
        this.list = list;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set view's content
        int totalPrice = list.get(position).getTotalPrice();
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.img);
        holder.name.setText(list.get(position).getName());
        holder.price = list.get(position).getPrice();
        holder.total.setText(String.format("%d.%03dđ", totalPrice / 1000, totalPrice % 1000));
        holder.quantity.setText(String.valueOf(list.get(position).getQuantity()));

        // Total amount pass to Cart activity
        totalAmount = totalAmount + totalPrice;
        Intent intent = new Intent("MyTotalAmount");
        intent.putExtra("totalAmount", totalAmount);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        // Set on click listener
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(holder.quantity.getText().toString());
                if (count < 99) {
                    count = count + 1;
                    int totalPrice = holder.price * count;
                    holder.quantity.setText(String.format("%d", count));
                    holder.total.setText(String.format("%d.%03dđ", totalPrice / 1000, totalPrice % 1000));

                    totalAmount = totalAmount + holder.price;
                    Intent intent = new Intent("MyTotalAmount");
                    intent.putExtra("totalAmount", totalAmount);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    firestore.collection("AddToCart")
                            .document(list.get(position).getDocumentId())
                            .update("quantity", count);
                    firestore.collection("AddToCart")
                            .document(list.get(position).getDocumentId())
                            .update("totalPrice", totalPrice);
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
                    holder.total.setText(String.format("%d.%03dđ", totalPrice / 1000, totalPrice % 1000));

                    totalAmount = totalAmount - holder.price;
                    Intent intent = new Intent("MyTotalAmount");
                    intent.putExtra("totalAmount", totalAmount);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    firestore.collection("AddToCart")
                            .document(list.get(position).getDocumentId())
                            .update("quantity", count);
                    firestore.collection("AddToCart")
                            .document(list.get(position).getDocumentId())
                            .update("totalPrice", totalPrice);

                }
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalAmount = 0;

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                firestore.collection("AddToCart")
                            .document(list.get(position).getDocumentId())
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        list.remove(holder.getAdapterPosition());
                                        notifyDataSetChanged();
                                    }
                                    else {

                                    }
                                }
                            });
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
