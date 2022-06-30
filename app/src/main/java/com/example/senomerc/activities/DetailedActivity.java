package com.example.senomerc.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.senomerc.R;

public class DetailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        getSupportActionBar().hide();
    }
}