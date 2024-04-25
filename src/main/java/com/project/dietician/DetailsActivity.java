package com.project.dietician;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;

public class DetailsActivity extends AppCompatActivity {

    private EditText name, email, age, weight, height;
    private RadioGroup gender;
    private Button save;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        age = findViewById(R.id.age);
        weight = findViewById(R.id.weight);
        height = findViewById(R.id.height);
        gender = findViewById(R.id.gender);

        progressDialog = new ProgressDialog(DetailsActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Saving Details, please Wait!");

        save = findViewById(R.id.saveBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(name.getText().toString())) {
                    if (!TextUtils.isEmpty(email.getText().toString())) {
                        if (!TextUtils.isEmpty(age.getText().toString())) {
                            if (!TextUtils.isEmpty(weight.getText().toString())) {
                                if (!TextUtils.isEmpty(height.getText().toString())) {
                                    progressDialog.show();
                                    saveDetails();
                                } else {
                                    height.requestFocus();
                                    Toast.makeText(DetailsActivity.this, "Enter your Height!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                weight.requestFocus();
                                Toast.makeText(DetailsActivity.this, "Enter your Weight!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            age.requestFocus();
                            Toast.makeText(DetailsActivity.this, "Enter your Age!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        email.requestFocus();
                        Toast.makeText(DetailsActivity.this, "Enter your Email Address!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    name.requestFocus();
                    Toast.makeText(DetailsActivity.this, "Enter your Full Name!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveDetails() {
        int selectedId = gender.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("userID", FirebaseAuth.getInstance().getUid());
        map.put("name", name.getText().toString());
        map.put("email", email.getText().toString());
        map.put("mobile", LoginActivity.phone);
        map.put("age", age.getText().toString());
        map.put("weight", weight.getText().toString());
        map.put("height", height.getText().toString());
        map.put("gender", radioButton.getText().toString());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/"+ FirebaseAuth.getInstance().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ref.setValue(map);
                startActivity(new Intent(DetailsActivity.this, MainActivity.class));
                progressDialog.cancel();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.cancel();
                Toast.makeText(DetailsActivity.this, "Fail to add data " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}