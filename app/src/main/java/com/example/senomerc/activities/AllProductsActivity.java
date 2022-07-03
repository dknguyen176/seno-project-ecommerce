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
import java.util.List;

public class AllProductsActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView result, title;
    int count;
    String category, specAttr;

    RecyclerView productRecyclerView;
    ProductsAdapter productsAdapter;
    List<ProductsModel> productsList;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        createToolbar();

        createProductView();

        createTitle();
    }

    private void createTitle() {
        title = findViewById(R.id.title);
        if (category != null && !category.isEmpty())
            title.setText(category);
        else if (specAttr != null && !specAttr.isEmpty())
            title.setText(specAttr);
        else
            title.setText("All");
    }

    private void createProductView() {
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String db_url = intent.getStringExtra("db_url");
        String specAttr = intent.getStringExtra("specAttr");
        String category = intent.getStringExtra("category");
        String order_by = intent.getStringExtra("order_by");
        int limit = intent.getIntExtra("limit", 0);

        this.category = category;
        this.specAttr = specAttr;

        productRecyclerView = findViewById(R.id.product_rec);
        productRecyclerView.setLayoutManager(new GridLayoutManager(AllProductsActivity.this,2));
        productsList = new ArrayList<>();
        productsAdapter = new ProductsAdapter(AllProductsActivity.this,productsList, specAttr, R.layout.product_large);
        productRecyclerView.setAdapter(productsAdapter);

        Query query = db.collection(db_url);

        if (category != null) query = query.whereEqualTo("type", category);
        if (order_by != null) query = query.orderBy(order_by);
        if (limit > 0) query = query.limit(limit);
        else if (limit < 0) query = query.limitToLast(-limit);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    count = task.getResult().size();
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        ProductsModel productsModel = document.toObject(ProductsModel.class);
                        productsList.add(productsModel);
                        productsAdapter.notifyDataSetChanged();

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