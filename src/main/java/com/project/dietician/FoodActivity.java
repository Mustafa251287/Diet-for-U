package com.project.dietician;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FoodActivity extends AppCompatActivity {

    String food;
    EditText et;
    TextView tv2, tv3, tv4, tv5, tv6, carb;
    RequestQueue requestQueue;

    private Button calculate;

    private double calories, protein, fat, carbohydrates;
    private String API_KEY = "52458920d1de5f031cf765cad4ebdf73";
    private String APP_ID = "09d91a50";
    private String ingredientsURL = "https://trackapi.nutritionix.com/v2/natural/nutrients";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Calories Calculator");

        et = findViewById(R.id.editText);
        tv2 = findViewById(R.id.textView16);
        tv3 = findViewById(R.id.textView17);
        tv4 = findViewById(R.id.textView18);
        tv5 = findViewById(R.id.textView19);
        tv6 = findViewById(R.id.textView20);
        carb = findViewById(R.id.textView24);

        food = et.getText().toString();

        requestQueue = Volley.newRequestQueue(FoodActivity.this);

        calculate = findViewById(R.id.calculateBtn);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(et.getText().toString())) {
                    StringRequest request = new StringRequest(Request.Method.POST, ingredientsURL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                HashMap<String, Double> hashMap = parseJSON(response);
                                calories = hashMap.get("calories");
                                protein = hashMap.get("protein");
                                fat = hashMap.get("fat");
                                carbohydrates = hashMap.get("carbohydrates");

                                tv2.setText("Nutrition Facts");
                                tv3.setText("Food Name: " + et.getText().toString());
                                tv4.setText("Calories: " + calories);
                                tv5.setText("Total Fat: " + fat);
                                tv6.setText("Protein: " + protein);
                                carb.setText("Carbohydrates: " + carbohydrates);
                            } catch (JSONException e) {
                                Toast.makeText(FoodActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            //Put a query into the POST request to get nutrition value back
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("query", et.getText().toString());
                            return hashMap;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            //Add authentication to headers to verify API
                            HashMap<String, String> headers = new HashMap();
                            headers.put("x-app-id", APP_ID);
                            headers.put("x-remote-user-id", APP_ID);
                            headers.put("x-app-key", API_KEY);
                            return headers;
                        }
                    };
                    requestQueue.add(request);
                } else {
                    et.requestFocus();
                    Toast.makeText(FoodActivity.this, "Enter Food Name to search!", Toast.LENGTH_SHORT).show();
                }
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

    //Parse a JSON string from the Nutritionix API to extract information from the API result, and put information into a hashmap
    public HashMap<String, Double> parseJSON(String JSONstring) throws JSONException {
        HashMap<String, Double> hashMap = new HashMap<String, Double>();
        Double calories, protein, fat, carbohydrates;

        //Parse the JSON string to extract nutritional information
        JSONObject jo = new JSONObject(JSONstring);
        JSONArray ja = jo.getJSONArray("foods");
        JSONObject foods = ja.getJSONObject(0);

        calories = foods.getDouble("nf_calories");
        protein = foods.getDouble("nf_protein");
        fat = foods.getDouble("nf_total_fat");
        carbohydrates = foods.getDouble("nf_total_carbohydrate");

        hashMap.put("calories", calories);
        hashMap.put("protein", protein);
        hashMap.put("fat", fat);
        hashMap.put("carbohydrates", carbohydrates);
        return hashMap;
    }
}