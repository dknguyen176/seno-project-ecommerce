package com.example.senomerc.activities;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.senomerc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Button signIn;
    TextView signUp;
    EditText email, password;

    private FirebaseAuth auth;

    final private int LAUNCH_MAIN_ACTIVITY = 1;
    final private int LAUNCH_REGISTER_ACTIVITY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent oldIntent = LoginActivity.this.getIntent();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtras(oldIntent);
            startActivityForResult(intent, LAUNCH_MAIN_ACTIVITY);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        signIn = findViewById(R.id.btn_signin);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();

                if (TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(LoginActivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(userPassword)) {
                    Toast.makeText(LoginActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                login(userEmail, userPassword);
            }
        });

        signUp = findViewById(R.id.text_signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivityForResult(intent, LAUNCH_REGISTER_ACTIVITY);
            }
        });
    }

    private void login(String userEmail, String userPassword) {
        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            Intent oldIntent = LoginActivity.this.getIntent();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtras(oldIntent);
                            startActivityForResult(intent, LAUNCH_MAIN_ACTIVITY);
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_REGISTER_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                String userEmail = data.getStringExtra("userEmail");
                String userPassword = data.getStringExtra("userPassword");
                login(userEmail, userPassword);
            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        } else if (requestCode == LAUNCH_MAIN_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {

            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        }
    }
}