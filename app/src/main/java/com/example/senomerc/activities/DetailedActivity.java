package com.example.senomerc.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.senomerc.R;
import com.example.senomerc.model.NewProductsModel;
import com.example.senomerc.model.PopularProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;

public class DetailedActivity extends AppCompatActivity {

    ImageView detailedImg;
    TextView name, description, price, quantity;
    Button addToCart;
    ImageView addItems, removeItems;
    RatingBar rating;

    private FirebaseFirestore firestore;

    private NewProductsModel newProductsModel = null;
    private PopularProductsModel popularProductsModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        Intent intent = getIntent();

        final Object obj = getIntent().getSerializableExtra("product");

        if (obj instanceof NewProductsModel){
            newProductsModel = (NewProductsModel) obj;
        } else if (obj instanceof PopularProductsModel){
            popularProductsModel = (PopularProductsModel) obj;
        }

        getSupportActionBar().hide();

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
        if (newProductsModel != null){
            Glide.with(this).load(newProductsModel.getImg_url()).into(detailedImg);
            name.setText(newProductsModel.getName());
            description.setText(newProductsModel.getDescription());
            price.setText(String.valueOf(newProductsModel.getPrice()));
            rating.setRating(Float.parseFloat(newProductsModel.getRating()));
        }
        else if (popularProductsModel != null){
            Glide.with(this).load(popularProductsModel.getImg_url()).into(detailedImg);
            name.setText(popularProductsModel.getName());
            description.setText(popularProductsModel.getDescription());
            price.setText(String.valueOf(popularProductsModel.getPrice()));
            rating.setRating(Float.parseFloat(popularProductsModel.getRating()));
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
        SimpleDateFormat dateFormater = new SimpleDateFormat("MM dd, yyyy");
        SimpleDateFormat timeFormater = new SimpleDateFormat("HH:mm:ss a");

        String currentTime = timeFormater.format(Calendar.getInstance());
        String currentDate = dateFormater.format(Calendar.getInstance());

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("productName", name.getText().toString());
        cartMap.put("productPrice", price.getText().toString());
        cartMap.put("currentTime", currentTime);
        cartMap.put("currentDate", currentDate);

        firestore.collection("AddToCart")
                .add(cartMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(DetailedActivity.this, "Added To Cart", Toast.LENGTH_SHORT);
                    }
                });
    }
}