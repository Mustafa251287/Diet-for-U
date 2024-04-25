package com.project.dietician;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.dietician.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SeekBar seekBar;
    double water = 0;
    int dwater = 0;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private TextView tv1, tv2, tv6, remTV;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private LinearLayout addWaterLL;
    private Button caloriesBtn;
    public static double bmi;
    public String channelID = "channel2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.project.dietician.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createNotificationChannel();

        progressBar = findViewById(R.id.progressBar);
        addWaterLL = findViewById(R.id.addDrinkLL);

        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        };

        DrawerLayout drawer = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tv1 = findViewById(R.id.bmiTV1);
        tv2 = findViewById(R.id.bmiTV2);
        tv6 = findViewById(R.id.waterTV);
        remTV = findViewById(R.id.remWater);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        addWaterLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                dwater += 200;
                map.put("water", String.valueOf(dwater));
                FirebaseDatabase
                        .getInstance()
                        .getReference("Users/" + FirebaseAuth.getInstance().getUid() + "/DATA/" + new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()))
                        .updateChildren(map);
                Toast.makeText(MainActivity.this, "Drinking 200ML Water...!", Toast.LENGTH_SHORT).show();
            }
        });

        caloriesBtn = findViewById(R.id.caloriesBtn);
        caloriesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FoodActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            mAuth.signOut();
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_reminder) {
            Intent intent = new Intent(this, RemindersActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_calories) {
            Intent intent = new Intent(this, FoodActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_exercise) {
            Intent intent = new Intent(this, ExerciseActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_meals) {
            Intent intent = new Intent(this, MealsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data, Please Wait!");
        progressDialog.show();
        getData();

        Intent intent = new Intent(MainActivity.this, RemindersActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this)
                .setChannelId(channelID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle("Diet4U")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.logo)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Start your day with a healthy breakfast and be more productive, Here is the Diet4U \uD83E\uDD29"))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, builder.build());
    }

    private void getData() {
        FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bmi = (Double.parseDouble(snapshot.child("weight").getValue().toString()) / Double.parseDouble(snapshot.child("height").getValue().toString()) / Double.parseDouble(snapshot.child("height").getValue().toString())) * 10000;

                        tv1.setText("Your BMI: " + String.format("%.1f", bmi));

                        if (snapshot.child("DATA").child(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())).child("water").exists()) {
                            Toast.makeText(MainActivity.this, snapshot.child("DATA").child(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())).child("water").getValue().toString(), Toast.LENGTH_SHORT).show();
                            dwater = Integer.parseInt(snapshot.child("DATA").child(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())).child("water").getValue().toString());
                        } else {
                            dwater = 0;
                        }

                        if (bmi >= 30) {
                            tv2.setText("Obesity");
                        } else if ((bmi >= 25) && (bmi < 30)) {
                            tv2.setText("Overweight");
                        } else if (bmi <= 18) {
                            tv2.setText("Under Weight");
                        } else if ((bmi > 18) && (bmi < 25)) {
                            tv2.setText("Normal");
                        }

                        seekBar.setProgress((int) Math.round(bmi));

                        // Calculate Body Water
                        String age = snapshot.child("age").getValue().toString();
                        String weight = snapshot.child("weight").getValue().toString();

                        if (Integer.parseInt(age) <= 30) {
                            water = ((Integer.parseInt(weight) * 42 * 2.95) / (28.3 * 100));

                        } else if (Integer.parseInt(age) > 30 && Integer.parseInt(age) <= 35) {
                            water = ((Integer.parseInt(weight) * 37 * 2.95) / (28.3 * 100));

                        } else if (Integer.parseInt(age) > 35) {
                            water = ((Integer.parseInt(weight) * 32 * 2.95) / (28.3 * 100));
                        }

                        tv6.setText("You need " + (Math.round(water * 10d) / 10d) + " Litre / Day");
                        progressBar.setMax((int) (Math.round(water * 10d) / 10d) + 1);
                        remTV.setText("Remaining Water to Drink: " + Math.round(((water * 10d) / 10d) - (dwater / 1000)) + " Litre(s)");
                        progressBar.setProgress((int) (dwater / 1000));
                        progressDialog.cancel();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.cancel();
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, "Channel", importance);
            channel.setDescription("This is channel");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}