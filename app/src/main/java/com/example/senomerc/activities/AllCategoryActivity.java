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
import com.example.senomerc.adapters.CategoryAdapter;
import com.example.senomerc.adapters.ProductsAdapter;
import com.example.senomerc.model.CategoryModel;
import com.example.senomerc.model.ProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllCategoryActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView result;
    int count;

    RecyclerView categoryRecyclerView;
    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryModelList;

    FirebaseFirestore db;
    FirebaseAuth auth;

    TextView cartCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_category);

        createToolbar();

        createCategoryView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartCount();
    }

    private void createCategoryView() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        categoryRecyclerView = findViewById(R.id.all_cat_rec);
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(AllCategoryActivity.this,2));
        categoryModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(AllCategoryActivity.this,categoryModelList,R.layout.category_large);
        categoryRecyclerView.setAdapter(categoryAdapter);

        db.collection("Category").orderBy("name")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            count = task.getResult().size();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                CategoryModel categoryModel = document.toObject(CategoryModel.class);
                                categoryModelList.add(categoryModel);
                                categoryAdapter.notifyDataSetChanged();

                            }
                        } else {
                            Toast.makeText(AllCategoryActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(AllCategoryActivity.this, CartActivity.class));
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