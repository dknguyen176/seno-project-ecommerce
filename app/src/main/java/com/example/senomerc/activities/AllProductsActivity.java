package com.example.senomerc.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.senomerc.R;
import com.example.senomerc.adapters.ProductsAdapter;
import com.example.senomerc.model.ProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllProductsActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView result, titleView;

    int count;


    RecyclerView productRecyclerView;
    ProductsAdapter productsAdapter;
    List<ProductsModel> productsList;

    FirebaseFirestore db;

    String category;
    String title;
    String[] tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        tags = intent.getStringExtra("tags").split(",");
        category = intent.getStringExtra("category");

        createToolbar();

        createProductView();

        createTitle();
    }

    private void createTitle() {
        titleView = findViewById(R.id.title);
        titleView.setText(title);
    }

    private void createProductView() {
        db = FirebaseFirestore.getInstance();

        productRecyclerView = findViewById(R.id.product_rec);
        productRecyclerView.setLayoutManager(new GridLayoutManager(AllProductsActivity.this,2));
        productsList = new ArrayList<>();
        productsAdapter = new ProductsAdapter(AllProductsActivity.this,productsList, R.layout.product_large);
        productRecyclerView.setAdapter(productsAdapter);

        Query query = db.collection("Product");

        if (category != null) query = query.whereEqualTo("type", category);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    count = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        ProductsModel productsModel = document.toObject(ProductsModel.class);
                        for (String tag : tags){
                            if (productsModel.getTags().contains(tag)){
                                productsList.add(productsModel);
                                productsAdapter.notifyDataSetChanged();
                                ++count;
                                break;
                            }
                        }
                    }
                    createResults();
                } else {
                    Toast.makeText(AllProductsActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createToolbar() {
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createResults() {
        result = findViewById(R.id.result_count);

        String text;
        if (count < 100)
            text = String.format("Found %d Results", count);
        else
            text = "Found 99+ Results";

        result.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_cart) {
            startActivity(new Intent(this, CartActivity.class));
        }

        if (id == R.id.map) {
            startActivity(new Intent(this, MapsActivity.class));
        }

        return true;
    }
}