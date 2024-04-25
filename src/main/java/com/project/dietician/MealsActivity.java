package com.project.dietician;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;

public class MealsActivity extends AppCompatActivity {

    String gender;
    double height, TDEE, weight = 0;
    int age = 0;
    private TextView fatTV, proteinTV, carbohydratesTV, caloriesTV;
    private TextView cereals, pulses, leafVeg, otherVeg, roots, diary, oils, sugar;
    private TextView breakfast, brunch, lunch, evening, dinner, night;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Meals Suggestions");

        fatTV = findViewById(R.id.fat);
        proteinTV = findViewById(R.id.protein);
        carbohydratesTV = findViewById(R.id.carbohydrates);
        caloriesTV = findViewById(R.id.calories);

        cereals = findViewById(R.id.cerealsTV);
        pulses = findViewById(R.id.pulses);
        leafVeg = findViewById(R.id.leafVeg);
        otherVeg = findViewById(R.id.otherVeg);
        roots = findViewById(R.id.roots);
        diary = findViewById(R.id.diary);
        oils = findViewById(R.id.oils);
        sugar = findViewById(R.id.sugar);

        breakfast = findViewById(R.id.breakfast);
        brunch = findViewById(R.id.brunch);
        lunch = findViewById(R.id.lunch);
        evening = findViewById(R.id.evening);
        dinner = findViewById(R.id.dinner);
        night = findViewById(R.id.night);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    private void getData() {

        FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        weight = Double.parseDouble(snapshot.child("weight").getValue().toString());
                        height = Double.parseDouble(snapshot.child("height").getValue().toString());
                        age = Integer.parseInt(snapshot.child("age").getValue().toString());
                        gender = snapshot.child("gender").getValue().toString();

                        // Calculate the TDEE: Total Daily Energy Expenditure aka Calories needed
                        if (gender.equals("Male")) {
                            // RDEE = (10 * weight(kg)) + (6.25 * height(cm)) - (5 * age(years)) + 5
                            double maleRDEE = (10 * weight * 0.45359237) + (6.25 * height) - (5 * age) + 5;
                            TDEE = maleRDEE * 1.375;
                        } else {
                            double femaleRDEE = (10 * weight * 0.45359237) + (6.25 * height) - (5 * age) - 161;
                            TDEE = femaleRDEE * 1.375;
                        }

                        //Calculate the macronutrients
                        double protein = weight * 0.825; //Protein intake should be around 1-.8.25 per lb of body weight
                        double fat = (TDEE * 0.25) / 9; //Fat intake is 25% of TDEE divided by 9 because there are 9 grams of fat per calorie
                        double carbohydrates = (TDEE - (protein + fat)) / 4; //Carbohydrates intake is the leftovers after protein and fat from total, divided by 4 since 1g of carbs is 4 calories

                        caloriesTV.setText("Calories Needed: " + (int) TDEE + " kcal");
                        proteinTV.setText("Protein Needed: " + (int) protein + " grams");
                        fatTV.setText("Fat Needed: " + (int) fat + " grams");
                        carbohydratesTV.setText("Carbohydrates Needed: " + (int) carbohydrates + " grams");

                        if (gender.equals("Male")) {
                            if (age >= 13 && age <= 15) {
                                cereals.setText("400g");
                                pulses.setText("70g");
                                leafVeg.setText("100g");
                                otherVeg.setText("150g");
                                roots.setText("NA");
                                diary.setText("600g");
                                oils.setText("30g");
                                sugar.setText("30g");
                            } else if (age >= 16 && age <= 18) {
                                cereals.setText("420g");
                                pulses.setText("70g");
                                leafVeg.setText("100g");
                                otherVeg.setText("175g");
                                roots.setText("NA");
                                diary.setText("600g");
                                oils.setText("40g");
                                sugar.setText("30g");
                            } else {
                                cereals.setText("520g");
                                pulses.setText("50g");
                                leafVeg.setText("40g");
                                otherVeg.setText("70g");
                                roots.setText("60g");
                                diary.setText("200g");
                                oils.setText("45g");
                                sugar.setText("40g");
                            }
                        } else {
                            if (age >= 13 && age <= 18) {
                                cereals.setText("320g");
                                pulses.setText("70g");
                                leafVeg.setText("150g");
                                otherVeg.setText("150g");
                                roots.setText("NA");
                                diary.setText("600g");
                                oils.setText("30g");
                                sugar.setText("30g");
                            } else {
                                cereals.setText("440g");
                                pulses.setText("45g");
                                leafVeg.setText("100g");
                                otherVeg.setText("40g");
                                roots.setText("50g");
                                diary.setText("150g");
                                oils.setText("25g");
                                sugar.setText("35g");
                            }
                        }

                        if (age >= 13 && age <= 18) {
                            breakfast.setText("Milk with Sugar (200ml)\nBoiled Egg / Omlet (1 Egg)\nBread or Paratha (2 pcs)");
                            brunch.setText("Fruit Salad or Fruit Juice (200ml)");
                            lunch.setText("Rice (One Plate)\nVegetables\nCurd Chapati\nSalad");
                            evening.setText("Tea with Snacks");
                            dinner.setText("Dal / Channa / Chicken Curry / Vegetables / Chapaties");
                            night.setText("Ice Cream / Kheer / Fruit Custard");
                        } else {
                            breakfast.setText("Milk with Sugar or Tea\nEgg with bread or paratha with curd, coffee");
                            brunch.setText("Fruit Salad or Fruit Juice");
                            lunch.setText("Vegetables, Chapati, Rice, Curd, Salad");
                            evening.setText("Tea with Snacks");
                            dinner.setText("Dal / Rajama / Chicken Curry / Vegetables / Chapaties");
                            night.setText("Ice Cream / Kheer / Fruit Custard");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MealsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
}