package com.example.senomerc.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.senomerc.R;
import com.example.senomerc.adapters.MyCartAdapter;
import com.example.senomerc.helper.Currency;
import com.example.senomerc.model.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView total;
    Button checkOut;

    int totalAmount = 0;

    List<MyCartModel> cartModelList;
    MyCartAdapter cartAdapter;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        createList();

        createToolbar();

        createCheckout();
    }

    private void createCheckout() {
        checkOut = findViewById(R.id.checkout);
        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    for(MyCartModel cartModel : cartModelList) {
                        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid()).collection("User")
                                .document(cartModel.getDocumentId())
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                    }
                } catch (Exception e) {
                    System.err.println("Error deleting collection : " + e.getMessage());
                }

                startActivity(new Intent(CartActivity.this, SuccessActivity.class));
                finish();
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

    private void createList() {
        recyclerView = findViewById(R.id.cart_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(this, cartModelList);
        recyclerView.setAdapter(cartAdapter);

        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid()).collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                MyCartModel myCartModel = document.toObject(MyCartModel.class);
                                myCartModel.setDocumentId(document.getId());
                                cartModelList.add(myCartModel);
                                cartAdapter.notifyDataSetChanged();

                                totalAmount += myCartModel.getTotalPrice();

                            }
                            createTotal();
                        }

                    }
                });

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver, new IntentFilter("ChangeTotal"));
    }

    private void createTotal() {
        total = findViewById(R.id.total);
        total.setText(Currency.toVND(totalAmount));
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int change = intent.getIntExtra("change", 0);
            totalAmount += change;
            total.setText(Currency.toVND(totalAmount));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        /*if (id == R.id.menu_cart) {
            startActivity(new Intent(this, CartActivity.class));
        }*/

        if (id == R.id.map) {
            startActivity(new Intent(this, MapsActivity.class));
        }

        return true;
    }
}