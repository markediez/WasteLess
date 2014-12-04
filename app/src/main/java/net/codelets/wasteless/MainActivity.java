package net.codelets.wasteless;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;


public class MainActivity extends ListActivity implements View.OnClickListener {
    final int EXPIRES = 19;
    final int ADD = 0;
    ImageView add;
    EditText foodField;
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
                    startActivityForResult(new Intent(MainActivity.this, AddFood.class), ADD);
                }   else {
                    Toast.makeText(MainActivity.this, "Food field empty!", Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case ADD:

                    int date[] = data.getIntArrayExtra("date");
                    String userFood, userExpire;

                    // add to array list
                    userFood = foodField.getText().toString().toUpperCase();
                    userExpire = "Expires: " + date[0] + "/" + date[1] + "/" + date[2]
                                    + expireList.size();
                    // add expire in this line
                    foodList.add(userFood);
                    expireList.add(userExpire);

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

        // Sets saved food if exists
        Set<String> getFood;
        getFood = getFoodList();
        for (String meal : getFood)
            foodList.add(meal);

        // Grabs corresponding expiration
        String expireString = getExpireString();
        StringTokenizer st = new StringTokenizer(expireString, ",");

        while (st.hasMoreElements()) {
            expireList.add(st.nextToken());
        }
    }

    // *******************************************************************
    // Returns food data
    // *******************************************************************
    private Set<String> getFoodList() {
        return pref.getStringSet("foodList", new TreeSet<String>());
    }

    // *******************************************************************
    // Returns expiration data
    // *******************************************************************
    private String getExpireString() {
        return pref.getString("expireString", new String());
    }

    // *******************************************************************
    // Function saves the current list view to preferences
    // pre: foodList and expireList is at least initiated
    // post: data for foodList and expireList is saved to preferences
    // *******************************************************************
    private void saveList() {
        Set<String> foodSet = new TreeSet<String>();
        Set<String> expireSet = new TreeSet<String>();
        StringBuilder sb = new StringBuilder();

        for (String food : foodList)
            foodSet.add(food);

        for (String expire : expireList)
            expireSet.add(expire);

        for (String eSave : expireSet) {
            // removes added chars from when turned to an ArrayList
            eSave = eSave.substring(0, EXPIRES);
            sb.append(eSave + ",");
        }

        // Saves to preferences
        pref.edit().putStringSet("foodList", foodSet).apply();
        pref.edit().putString("expireString", sb.toString()).apply();

    }



}
