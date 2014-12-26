package net.codelets.wasteless;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;


public class MainActivity extends ListActivity implements View.OnClickListener {
    // Global Variables
    final int ADD = 0;
    ImageView add;
    EditText foodField;
    Food newFood, myFood;
    ArrayList<String> keyList;
    ArrayList<String> foodList;
    ArrayList<String> expireList;
    WasteAdapter adapter;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        init();
        adapter = new WasteAdapter(MainActivity.this, foodList, expireList);
        setListAdapter(adapter);
        listFeatures();

    }
    // *******************************************************************
    // Handles list functions/features
    // pre: list exists and initialized
    // post: longclick deletes
    // *******************************************************************
    private void listFeatures() {
        // Deletes list item on long click
        getListView().setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent,
                                     View view, int position, long id) {
                pref.edit().remove(keyList.get(position)).apply();          // Remove chosen object from SharedPreferences
                keyList.remove(position);                                   // Remove key from list
                foodList.remove(position);                                  // Removes food
                expireList.remove(position);                                //          from ListView

                // Creates the new set of keys
                Set<String> keySet = new TreeSet<String>();
                for (String keys : keyList)
                    keySet.add(keys);

                // Save to user preference
                pref.edit().putStringSet("foodList", keySet).apply();       // Save new key set
                adapter.notifyDataSetChanged();                             // Updates the List
                Toast.makeText(MainActivity.this, "Removed",                // Notifies User
                        Toast.LENGTH_SHORT).show();

                return true;
            }
        });
    }

    // *******************************************************************
    // Handles clicks
    // pre: user pushes a button
    // post: appropriate button function starts based on button id
    // *******************************************************************
    @Override
    public void onClick (View v) {
        switch(v.getId()) {
            case R.id.ivAdd:
                String b = foodField.getText().toString();
                if (!b.matches("")) {
                    startActivityForResult(new Intent(MainActivity.this,
                            AddFood.class), ADD);
                }   else {
                    Toast.makeText(MainActivity.this, "Food field empty!"
                            , Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // *******************************************************************
    // Handles onActivityResult
    // pre: a startforactivityresult intent was activated
    // post: appropriate function occurs based on request code and result
    // *******************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case ADD:
                    // Generate unique key
                    Calendar cal = Calendar.getInstance();                      // Get Calendar
                    String userKey = cal.getTime().toString();                  // Generate Key

                    int date[] = data.getIntArrayExtra("date");
                    String userFood, userExpire;

                    // add to array list
                    userFood = foodField.getText().toString()
                            .toUpperCase();
                    userExpire = "Expires: " + date[0] + "/" + date[1]
                            + "/" + date[2];
                    // add expire in this line
                    foodList.add(userFood);
                    expireList.add(userExpire);
                    Toast.makeText(MainActivity.this, userKey,
                            Toast.LENGTH_SHORT).show();

                    // Create new food object
                    newFood = new Food(userFood, userKey,
                            date[0], date[1], date[2]);

                    foodField.setText("");
                    adapter.notifyDataSetChanged();
                    saveList();
                    break;
            }
        }
    }

    // *******************************************************************
    // Initiates variables and loads preferences
    // *******************************************************************
    private void init() {
        add = (ImageView)findViewById(R.id.ivAdd);
        add.setOnClickListener(this);
        foodField = (EditText)findViewById(R.id.etFood);
        foodList = new ArrayList<String>();
        expireList = new ArrayList<String>();
        keyList = new ArrayList<String>();

        Set<String> getKeys = getFoodList();
        for (String key : getKeys) {
            myFood = getFood(key);

            if (myFood.eYear() != 0) {
                foodList.add(myFood.specify());
                String expireDate = "Expires: " +
                                    myFood.eMonth() + "/" +
                                    myFood.eDay() + "/" +
                                    myFood.eYear();
                expireList.add(expireDate);
                keyList.add(key);
            }
        }
    }

    // *******************************************************************
    // Retrieves food keys
    // *******************************************************************
    private Set<String> getFoodList() {
        return pref.getStringSet("foodList", new TreeSet<String>());
    }

    // *******************************************************************
    // Function saves the current list view to preferences
    // pre: foodList and expireList is at least initiated
    // post: data for foodList and expireList is saved to preferences
    // *******************************************************************
    private void saveList() {
        // Saving food object using Gson library
        Food saveFood = newFood;
        Gson gson = new Gson();
        String json = gson.toJson(saveFood);
        Set<String> keySet = new TreeSet<String>();
        keyList.add(saveFood.getKey());                                         // Stores keys

        for(String keys : keyList)
            keySet.add(keys);

        pref.edit().putString(saveFood.getKey(), json).apply();                 // Saves food
        pref.edit().putStringSet("foodList", keySet).apply();                   // Saves keys
    }

    /*
     Returns food object
     pre: An unused foodkey is passed
     post: Food object is returned
     */
    private Food getFood(String key) {
        Gson gson = new Gson();
        String json = pref.getString(key, null);
        Food food;
        if(json == null)
            food = new Food("Nulled","rando",0,0,0);
        else
            food = gson.fromJson(json, Food.class);

        return food;
    }



}
