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
import com.example.senomerc.helper.Currency;
import com.example.senomerc.model.ProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailedActivity extends AppCompatActivity {

    ImageView detailedImg;
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
        auth = FirebaseAuth.getInstance();

        onBindView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartCount();
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
                startActivity(new Intent(DetailedActivity.this, CartActivity.class));
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void loadCartCount() {
        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid()).collection("User")
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