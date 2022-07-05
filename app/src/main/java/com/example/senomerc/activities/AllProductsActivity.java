package com.example.senomerc.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AllProductsActivity extends AppCompatActivity {
    public static boolean active = false;

    Toolbar toolbar;
    TextView result, titleView;

    int count;

    RecyclerView productRecyclerView;
    public static ProductsAdapter productsAdapter;
    public static List<ProductsModel> productsList;
    public static HashMap<String, Integer> productsListPosition;

    FirebaseFirestore db;
    FirebaseAuth auth;

    TextView cartCount;

    String category;
    String title;
    String[] tags;

    HashMap < String, Object > favMap;
    boolean fav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);
        active = true;

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        String sus = intent.getStringExtra("tags");
        if (sus != null) tags = sus.split(","); else tags = null;
        category = intent.getStringExtra("category");
        fav = title.compareToIgnoreCase("Favorite Products") == 0;

        favMap = new HashMap<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        db.collection("Favorites").document(auth.getCurrentUser().getUid()).collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String docID = document.get("id", String.class);
                                favMap.put(docID, null);
                            }
                            createProductView();
                        }
                    }
                });

        createToolbar();

        createTitle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartCount();
    }

    private void createTitle() {
        titleView = findViewById(R.id.title);
        titleView.setText(title);
    }

    private void createProductView() {
        productRecyclerView = findViewById(R.id.product_rec);
        productRecyclerView.setLayoutManager(new GridLayoutManager(AllProductsActivity.this,2));
        productsList = new ArrayList<>();
        productsAdapter = new ProductsAdapter(AllProductsActivity.this,productsList, R.layout.product_large);
        productRecyclerView.setAdapter(productsAdapter);
        productsListPosition = new HashMap<>();

        Query query = db.collection("Product");

        if (category != null) query = query.whereEqualTo("type", category);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    count = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String docId = document.getId();
                        ProductsModel productsModel = document.toObject(ProductsModel.class);
                        productsModel.setId(docId);
                        productsModel.setFavorite(favMap.containsKey(docId));

                        if (fav){
                            if (productsModel.isFavorite()) {
                                productsList.add(productsModel);
                                productsListPosition.put(docId, count);
                                // productsAdapter.notifyDataSetChanged();
                                ++count;
                            }
                        }
                        else {
                            for (String tag : tags) {
                                if (productsModel.getTags().contains(tag)) {
                                    productsList.add(productsModel);
                                    productsListPosition.put(docId, count);
                                    // productsAdapter.notifyDataSetChanged();
                                    ++count;
                                    break;
                                }
                            }
                        }
                    }
                    productsAdapter.notifyDataSetChanged();
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
        MenuItem item = menu.findItem(R.id.cart);
        MenuItemCompat.setActionView(item, R.layout.cart);
        ConstraintLayout cart = (ConstraintLayout) MenuItemCompat.getActionView(item);

        cartCount = (TextView) cart.findViewById(R.id.item_count);
        cartCount.setText("0");
        loadCartCount();

        cart.findViewById(R.id.btn_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllProductsActivity.this, CartActivity.class));
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void loadCartCount() {
        db.collection("AddToCart").document(auth.getCurrentUser().getUid()).collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = task.getResult().size();
                            cartCount.setText(String.valueOf(count));
                        } else {

                        }
                    }
                });
    }
}