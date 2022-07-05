package com.example.senomerc.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.view.View;

import com.bumptech.glide.load.engine.Initializable;
import com.example.senomerc.R;
import com.example.senomerc.fragments.HomeFragment;
import com.example.senomerc.helper.Currency;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Fragment homeFragment;

    Toolbar toolbar;

    TextView searchView, cartCount;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    final private int LAUNCH_LOGIN_ACTIVITY = 1;

    final int popular_shown = 6;
    final int new_shown = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Intent returnNone = new Intent();
        setResult(Activity.RESULT_CANCELED, returnNone);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createToolbar();

        createSearch();

        homeFragment = new HomeFragment();
        loadHomeFragment(homeFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartCount();
    }

    private void loadHomeFragment(Fragment homeFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, homeFragment);
        transaction.commit();
    }

    private void createSearch() {
        searchView = findViewById(R.id.search);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });
    }

    private void createToolbar() {
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_logout_24);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent signOut = new Intent();
                setResult(Activity.RESULT_OK, signOut);
                finish();
            }
        });
    }

    public void loadCartCount() {
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
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_LOGIN_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {

            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        }
    }
}