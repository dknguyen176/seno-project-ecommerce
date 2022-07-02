package com.example.senomerc.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.senomerc.R;
import com.example.senomerc.adapters.ProductsAdapter;
import com.example.senomerc.model.ProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllProductsActivity extends AppCompatActivity {

    RecyclerView productRecyclerView;
    ProductsAdapter productsAdapter;
    List<ProductsModel> productsList;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String db_url = intent.getStringExtra("db_url");
        String specAttr = intent.getStringExtra("specAttr");

        productRecyclerView = findViewById(R.id.product_rec);
        productRecyclerView.setLayoutManager(new GridLayoutManager(AllProductsActivity.this,2));
        productsList = new ArrayList<>();
        productsAdapter = new ProductsAdapter(AllProductsActivity.this,productsList, specAttr);
        productRecyclerView.setAdapter(productsAdapter);

        db.collection(db_url)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                ProductsModel productsModel = document.toObject(ProductsModel.class);
                                productsList.add(productsModel);
                                productsAdapter.notifyDataSetChanged();

                            }
                        } else {
                            Toast.makeText(AllProductsActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}