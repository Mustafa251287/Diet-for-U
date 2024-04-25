package com.project.dietician;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RemindersActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Button reminderBtn, reminderStop;
    private RadioGroup reminderRG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reminders");

        sharedPreferences = getSharedPreferences("SettingsData", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        reminderRG = findViewById(R.id.reminderRG);

        reminderBtn = findViewById(R.id.reminderBTN);
        reminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = reminderRG.getCheckedRadioButtonId();
                int delay;
                if (selectedId == -1) {
                    Toast.makeText(RemindersActivity.this, "Select a Reminder Period!", Toast.LENGTH_SHORT).show();
                } else {

                    if (selectedId == R.id.m15) {
                        delay = 15;
                    } else if (selectedId == R.id.m30) {
                        delay = 30;
                    } else if (selectedId == R.id.hr1) {
                        delay = 60;
                    } else if (selectedId == R.id.hr1m30) {
                        delay = 90;
                    } else {
                        delay = 120;
                    }

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent alarmIntent = new Intent(RemindersActivity.this, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(RemindersActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 6000, pendingIntent);
                    editor.putInt("water_delay", delay);
                    editor.putString("water_reminder", "true");
                    editor.commit();
                    Toast.makeText(RemindersActivity.this, "Water Reminder Started Successfully!", Toast.LENGTH_SHORT).show();
                    reminderBtn.setVisibility(View.GONE);
                    reminderStop.setVisibility(View.VISIBLE);
                }
            }
        });


        reminderStop = findViewById(R.id.remStopBtn);
        reminderStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("water_reminder", "false");
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(RemindersActivity.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(RemindersActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
                editor.commit();
                reminderRG.clearCheck();
                Toast.makeText(RemindersActivity.this, "Water Reminders Stopped Successfully!", Toast.LENGTH_SHORT).show();
                reminderStop.setVisibility(View.GONE);
                reminderBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sharedPreferences.getString("water_reminder", "").equals("true")) {
            reminderBtn.setVisibility(View.GONE);
            reminderStop.setVisibility(View.VISIBLE);

            if (sharedPreferences.getInt("water_delay", 0) == 15) {
                RadioButton rb = findViewById(R.id.m15);
                rb.setChecked(true);
            } else if (sharedPreferences.getInt("water_delay", 0) == 30) {
                RadioButton rb = findViewById(R.id.m30);
                rb.setChecked(true);
            } else if (sharedPreferences.getInt("water_delay", 0) == 60) {
                RadioButton rb = findViewById(R.id.hr1);
                rb.setChecked(true);
            } else if (sharedPreferences.getInt("water_delay", 0) == 90) {
                RadioButton rb = findViewById(R.id.hr1m30);
                rb.setChecked(true);
            } else {
                RadioButton rb = findViewById(R.id.hr2);
                rb.setChecked(true);
            }
        } else {
            reminderStop.setVisibility(View.GONE);
            reminderBtn.setVisibility(View.VISIBLE);
        }
    }
}