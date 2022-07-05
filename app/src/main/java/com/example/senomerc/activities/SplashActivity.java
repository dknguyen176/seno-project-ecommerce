package com.example.senomerc.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.senomerc.R;
import com.example.senomerc.adapters.CategoryAdapter;
import com.example.senomerc.adapters.ProductsAdapter;
import com.example.senomerc.model.CategoryModel;
import com.example.senomerc.model.ProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    final private  int LAUNCH_MAIN_ACTIVITY = 1;

    private FirebaseFirestore db;

    final int popular_shown = 6;
    final int new_shown = 6;

    int count = 0;

    ArrayList<CategoryModel> categoryModelList;
    ArrayList<ProductsModel> newProductsModelList;
    ArrayList<ProductsModel> popularProductsModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        db = FirebaseFirestore.getInstance();

        LoadHomeData();
    }

    private void LoadHomeData() {
        createCategoryList();

        createNewProductsList();

        createPopularProductsList();
    }

    private void createPopularProductsList() {
        popularProductsModelList = new ArrayList<>();

        db.collection("Product").orderBy("rating", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int cnt = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductsModel popularProductsModel = document.toObject(ProductsModel.class);
                                if (popularProductsModel.getTags().contains("popular")) {
                                    popularProductsModelList.add(popularProductsModel);
                                    ++cnt;
                                    if (cnt >= popular_shown) break;
                                }

                            }

                            if (++count == 3) LoadingDone();
                        } else {
                            Toast.makeText(SplashActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createNewProductsList() {
        newProductsModelList = new ArrayList<>();

        db.collection("Product").orderBy("rating", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int cnt = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductsModel popularProductsModel = document.toObject(ProductsModel.class);
                                if (popularProductsModel.getTags().contains("new")) {
                                    newProductsModelList.add(popularProductsModel);
                                    ++cnt;
                                    if (cnt >= new_shown) break;
                                }
                            }

                            if (++count == 3) LoadingDone();
                        } else {
                            Toast.makeText(SplashActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createCategoryList() {
        categoryModelList = new ArrayList<>();

        db.collection("CategoryCircle").orderBy("name")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                CategoryModel categoryModel = document.toObject(CategoryModel.class);
                                categoryModelList.add(categoryModel);
                            }

                            if (++count == 3) LoadingDone();
                        } else {
                            Toast.makeText(SplashActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void LoadingDone() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        intent.putExtra("CategoryList", categoryModelList);
        intent.putExtra("NewList", newProductsModelList);
        intent.putExtra("PopularList", popularProductsModelList);
        startActivity(intent);

        finish();
    }
}