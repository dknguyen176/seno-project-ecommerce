package com.example.senomerc.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.senomerc.R;
import com.example.senomerc.adapters.ProductsAdapter;
import com.example.senomerc.fragments.HomeFragment;
import com.example.senomerc.helper.Currency;
import com.example.senomerc.model.ProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailedActivity extends AppCompatActivity {

    ImageView detailedImg, favorite;
    TextView name, description, price, quantity;
    Button addToCart;
    ImageButton plus, minus;
    RatingBar rating;

    int price1, count;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private ProductsModel productsModel = null;

    Toolbar toolbar;

    TextView cartCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        final Object obj = getIntent().getSerializableExtra("product");
        productsModel = (ProductsModel) obj;

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        createToolbar();

        onBindView();
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

    private void onBindView() {
        // Bind view here
        detailedImg = findViewById(R.id.detailed_img);
        name = findViewById(R.id.detailed_name);
        description = findViewById(R.id.detailed_description);
        price = findViewById(R.id.detailed_price);
        rating = findViewById(R.id.detailed_rating);
        addToCart = findViewById(R.id.btn_addToCart);
        quantity = findViewById(R.id.cart_quantity);
        favorite = findViewById(R.id.detailed_favorite);
        plus = findViewById(R.id.cart_add);
        minus = findViewById(R.id.cart_remove);

        // Set view's content here
        if (productsModel != null) {
            price1 = productsModel.getPrice();
            count = 1;

            Glide.with(this).load(productsModel.getImg_url()).into(detailedImg);
            name.setText(productsModel.getName());
            rating.setRating(Float.parseFloat(productsModel.getRating()));
            description.setText(productsModel.getDescription());
            description.setMovementMethod(new ScrollingMovementMethod());
            price.setText(Currency.toVND(price1));
            addToCart.setText(Currency.toVND(price1) + " - Add to Cart");
            quantity.setText("1");
            favorite.setImageResource(productsModel.isFavorite()
                    ? R.drawable.icons8_favorite_64_1
                    : R.drawable.icons8_favorite_64);
            favorite.setTag(productsModel.isFavorite());
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeFragment.onClickFavorites(favorite, productsModel);
                }
            });
        } else {
            price1 = 0;
            count = 0;
        }

        // Set on click listener
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddToCart();
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count < 99) {
                    count = count + 1;
                    int totalPrice = price1 * count;
                    quantity.setText(String.valueOf(count));
                    addToCart.setText(Currency.toVND(totalPrice) + " - Add to Cart");
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 1) {
                    count = count - 1;
                    int totalPrice = price1 * count;
                    quantity.setText(String.valueOf(count));
                    addToCart.setText(Currency.toVND(totalPrice) + " - Add to Cart");
                }
            }
        });
    }

    private void onClickAddToCart() {
        final HashMap<String, Object> cartMap = new HashMap<>();

        cartMap.put("img_url", productsModel.getImg_url());
        cartMap.put("name", name.getText());
        cartMap.put("price", price1);
        cartMap.put("quantity", count);
        cartMap.put("totalPrice", price1 * count);

        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid()).collection("User")
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
        getMenuInflater().inflate(R.menu.empty_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}