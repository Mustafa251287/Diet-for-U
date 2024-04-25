package com.project.dietician;

import static com.project.dietician.MainActivity.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ExerciseActivity extends AppCompatActivity {

    private TextView tv1, tv2, execType, execDesc;
    private ImageView execImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Exercise Suggestion");

        tv1 = findViewById(R.id.bmiTV);
        tv2 = findViewById(R.id.bmiRes);
        execType = findViewById(R.id.exerType);
        execDesc = findViewById(R.id.exerDesc);
        execImg = findViewById(R.id.exerImg);

        tv1.setText("Your BMI is: " + String.format("%.1f", bmi));
        if (bmi >= 30) {
            tv2.setText("Obesity");
        } else if ((bmi >= 25) && (bmi < 30)) {
            tv2.setText("Overweight");
        } else if (bmi <= 18) {
            tv2.setText("Under Weight");
        } else if ((bmi > 18) && (bmi < 25)) {
            tv2.setText("Normal");
        }

        if ((bmi >= 30) || ((bmi >= 25) && (bmi < 30))) {
            execType.setText("Anaerobic Exercise");
            execImg.setImageDrawable(getDrawable(R.drawable.anaerobic));
            execDesc.setText("Anaerobic exercise includes lifting weights and other resistance exercises such as push ups or stomach crunches. Anaerobic exercise is effective in lowering your BMI, but you may find that you don’t experience a drop in your weight in the short-term as an increase in muscle mass may see the scales showing the same weight even though you are actually burning body fat. In the longer-term, however, as your muscles increase, you should start to notice that your weight, and therefore your BMI, begins to drop. When working a muscle group, make sure you have a day’s rest before exercising those muscles again as this will allow your muscles time to recover and grow. Research shows that combining both aerobic and anaerobic exercise to be successful towards weight loss as well as in improving insulin sensitivity in people with diabetes.");
        } else {
            execType.setText("Aerobic exercise");
            execImg.setImageDrawable(getDrawable(R.drawable.aerobic));
            execDesc.setText("It is recommended by World Health Organisation (WHO) that adults get at least 30 minutes of moderate aerobic exercise , five days a week . Alternatively, this can be 75 minutes of vigorous-intensity activity through the week. You should aim to take part in an hour of physical activity each day. Swimming, running and cycling are all good examples of aerobic exercise, which serves to increase your heart and breathing rate. Aerobic exercise does not lead to putting on muscle mass to the extent that anaerobic exercise does. For this reason, when you perform aerobic exercise, you burn off fat without putting much weight back on as muscle mass. Note that there is nothing unhealthy on putting on muscle mass, it is just that those who need to see their weight dropping for motivation purposes may prefer to concentrate more on aerobic exercises, at least initially.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}