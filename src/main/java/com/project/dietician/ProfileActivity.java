package com.project.dietician;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView email, name, phone;
    private ProgressDialog pd;
    private EditText weight, height, age;
    private Button saveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        email = findViewById(R.id.useremail);
        name = findViewById(R.id.username);
        phone = findViewById(R.id.userphone);

        age = findViewById(R.id.userAge);
        weight = findViewById(R.id.userWeight);
        height = findViewById(R.id.userHeight);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Profile");

        saveChanges = findViewById(R.id.saveChanges);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("age", age.getText().toString());
                map.put("weight", weight.getText().toString());
                map.put("height", height.getText().toString());

                FirebaseDatabase
                        .getInstance()
                        .getReference("Users").child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(map);
                Toast.makeText(ProfileActivity.this, "Details Updated Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        pd = new ProgressDialog(ProfileActivity.this);
        pd.setMessage("Fetching Data, Please Wait!");
        pd.setCancelable(false);
        getData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData() {
        FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        email.setText(snapshot.child("email").getValue().toString());
                        name.setText(snapshot.child("name").getValue().toString());
                        phone.setText(snapshot.child("mobile").getValue().toString());
                        age.setText(snapshot.child("age").getValue().toString());
                        weight.setText(snapshot.child("weight").getValue().toString());
                        height.setText(snapshot.child("height").getValue().toString());
                        pd.cancel();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        pd.cancel();
                        Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}