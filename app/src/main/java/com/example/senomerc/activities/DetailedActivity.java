package com.example.senomerc.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.senomerc.R;
import com.example.senomerc.model.ProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailedActivity extends AppCompatActivity {

    ImageView detailedImg;
    TextView name, description, price, quantity;
    Button addToCart;
    ImageView addItems, removeItems;
    RatingBar rating;

    private FirebaseFirestore firestore;

    private ProductsModel productsModel = null;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        Intent intent = getIntent();

        final Object obj = getIntent().getSerializableExtra("product");

        productsModel = (ProductsModel) obj;

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

        firestore = FirebaseFirestore.getInstance();

        onBindView();
    }

    private void onBindView() {
        // Bind view here
        detailedImg = findViewById(R.id.detailed_img);
        name = findViewById(R.id.detailed_name);
        description = findViewById(R.id.detailed_description);
        price = findViewById(R.id.detailed_price);
        rating = findViewById(R.id.detailed_rating);
        addToCart = findViewById(R.id.btn_addToCart);

        // Set view's content here
        if (productsModel != null){
            Glide.with(this).load(productsModel.getImg_url()).into(detailedImg);
            name.setText(productsModel.getName());
            description.setText(productsModel.getDescription());
            price.setText(String.valueOf(productsModel.getPrice()));
            rating.setRating(Float.parseFloat(productsModel.getRating()));
        }

        // Set on click listener
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddToCart();
            }
        });
    }

    private void onClickAddToCart() {
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("productName", name.getText().toString());
        cartMap.put("productPrice", price.getText().toString());


        cartMap.put("img_url", productsModel.getImg_url());
        cartMap.put("name", name.getText());
        cartMap.put("price", Integer.parseInt(price.getText().toString()));
        cartMap.put("quantity", 1);
        cartMap.put("totalPrice", Integer.parseInt(price.getText().toString()));

        firestore.collection("AddToCart")
                .add(cartMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(DetailedActivity.this, "Added To Cart", Toast.LENGTH_SHORT);
                        finish();
                    }
                });
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

        return true;
    }

}