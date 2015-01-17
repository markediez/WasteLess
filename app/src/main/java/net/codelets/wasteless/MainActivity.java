package net.codelets.wasteless;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
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
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends ListActivity implements View.OnClickListener {
    // Global Variables
    final int ADD = 0;
    final int SET = 1;
    final int EDIT = 2;
    private int h, m, itemId;
    private ImageView add, set, help;
    private EditText foodField;
    private Food newFood, myFood;
    private ArrayList<String> keyList;
    private ArrayList<Food> foodArrayList;
    private WasteAdapter adapter;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        init();
        adapter = new WasteAdapter(MainActivity.this, foodArrayList);
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
                foodArrayList.get(position).clearAlarm(MainActivity.this, Individual.class);        // Remove alarm
                pref.edit().remove(keyList.get(position)).apply();                                  // Remove chosen object from SharedPreferences
                keyList.remove(position);                                                           // Remove key from list
                foodArrayList.remove(position);                                                     // Remove FoodObject

                // Creates the new set of keys
                Set<String> keySet = new TreeSet<String>();
                for (String keys : keyList)
                    keySet.add(keys);

                // Save to user preference
                pref.edit().putStringSet("foodList", keySet).apply();                               // Save new key set
                adapter.notifyDataSetChanged();                                                     // Updates the List
                Toast.makeText(MainActivity.this, "Removed",                                        // Notifies User
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
            case R.id.ivHelp:
                startActivity(new Intent(MainActivity.this, Help.class));
                break;
            case R.id.ivSetting:
                startActivityForResult(new Intent(MainActivity.this, Settings.class), SET);
                break;
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
                case EDIT:
                    Toast.makeText(MainActivity.this, "Yup", Toast.LENGTH_SHORT).show();            // TODO for close up?
                    break;
                case SET:
                    // grabs new time
                    h = data.getIntExtra("hour", 17);
                    m = data.getIntExtra("min", 0);
                    // sets new time
                    setAlarmDaily(h,m);
                    // saves new time
                    pref.edit().putInt("hour", h).apply();
                    pref.edit().putInt("min", m).apply();
                    // updates the alarms
                    for (Food updateFood : foodArrayList)
                        updateFood.setTime(MainActivity.this, h, m);


                    Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    break;
                case ADD:
                    // Generate unique key
                    Calendar cal = Calendar.getInstance();                                          // Get Calendar
                    String userKey = cal.getTime().toString();                                      // Generate Key

                    int date[] = data.getIntArrayExtra("date");
                    GregorianCalendar expire = new GregorianCalendar(date[2], date[0], date[1],h,m);

                    // add to array list
                    String userFood = foodField.getText().toString()
                            .toUpperCase();
                    // Create new food object
                    newFood = new Food(userFood, userKey, expire, itemId);
                    setAlarm(newFood);
                    foodArrayList.add(newFood);
                    itemId++;
                    pref.edit().putInt("itemId", itemId);

                    foodField.setText("");
                    adapter.notifyDataSetChanged();
                    saveList();
                    Toast.makeText(MainActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    // *******************************************************************
    // Initiates variables and loads preferences
    // *******************************************************************
    private void init() {
        h = pref.getInt("hour", 17);
        m = pref.getInt("min", 0);
        add = (ImageView)findViewById(R.id.ivAdd);
        set = (ImageView)findViewById(R.id.ivSetting);
        help = (ImageView)findViewById(R.id.ivHelp);
        help.setOnClickListener(MainActivity.this);
        set.setOnClickListener(MainActivity.this);
        add.setOnClickListener(MainActivity.this);
        foodField = (EditText)findViewById(R.id.etFood);
        foodArrayList = new ArrayList<Food>();
        keyList = new ArrayList<String>();

        itemId = pref.getInt("itemId", 0);

        // Retrieves food objects and fills arrays
        Set<String> getKeys = getFoodList();
        for (String key : getKeys) {
            myFood = getFood(key, pref);
            foodArrayList.add(myFood);
            keyList.add(key);
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

    // *******************************************************************
    // This function sets the alarm.
    // *******************************************************************
    public void setAlarmDaily(int h, int m) {
        // set an alarm a minute from open
        Calendar currentCal = Calendar.getInstance();
        Calendar alarmCal = Calendar.getInstance();
        alarmCal.set(Calendar.HOUR_OF_DAY, h);
        alarmCal.set(Calendar.MINUTE, m);
        alarmCal.set(Calendar.SECOND, 59);

        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        alarmIntent.putExtra("alarm", 6275);
        PendingIntent pi = PendingIntent
                .getBroadcast(MainActivity.this, 6275,
                        alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        long currenTime = currentCal.getTimeInMillis();
        long wantedTime = alarmCal.getTimeInMillis();

        // if time already passed, wait till tomorrow
        if (wantedTime <= currenTime) {
            alarmCal.add(Calendar.DAY_OF_MONTH, 1);
            wantedTime = alarmCal.getTimeInMillis();
        }

        // set daily
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, wantedTime, AlarmManager.INTERVAL_DAY, pi);

    }

    // *******************************************************************
    // Sets individual notifications
    // pre: new food object is made
    // post: a notification for the new food object is made
    // *******************************************************************
    public void setAlarm(Food foodAlarm) {
        // TODO set days = variable for customizeable notifications
        int days = -7;
        Calendar notificationTime, currTime;
        notificationTime = foodAlarm.expireCal();
        notificationTime.add(Calendar.DAY_OF_MONTH, days);
        notificationTime.set(Calendar.HOUR, h);
        notificationTime.set(Calendar.MINUTE, m);

        Intent individual = new Intent(MainActivity.this, Individual.class);
        individual.putExtra("notification", foodAlarm.getKey());

        PendingIntent pIndividual = PendingIntent.getBroadcast(MainActivity.this, foodAlarm.getId(),
                individual,PendingIntent.FLAG_UPDATE_CURRENT);

        currTime = Calendar.getInstance();

        // Set notification closer if the food item expires in less than a week
        while (notificationTime.compareTo(currTime) == -1) {
            notificationTime.add(Calendar.DAY_OF_MONTH, 1);
            if(notificationTime.get(Calendar.DAY_OF_MONTH) == currTime.get(Calendar.DAY_OF_MONTH)   // If its on the same day
                    && notificationTime.get(Calendar.MONTH) == currTime.get(Calendar.MONTH)
                    && notificationTime.get(Calendar.YEAR) == currTime.get(Calendar.YEAR)) {
                break;                                                                              // Set alarm @ notification Time
            }
        }
        // Set notification Calendar
        foodAlarm.setNotify(notificationTime);
        // Set Notification
        if(notificationTime.getTimeInMillis() > currTime.getTimeInMillis()) {                       // No notification if time has passed
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pIndividual);
        }
    }

    // *******************************************************************
    // Returns food object
    // pre: An unused foodkey is passed
    // post: Food object is returned
    // *******************************************************************
    public Food getFood(String key, SharedPreferences sp) {
        Gson gson = new Gson();
        String json = sp.getString(key, null);
        Food food;
        if(json == null)
            food = new Food("Nulled","rando",null, -1);
        else
            food = gson.fromJson(json, Food.class);

        return food;
    }
}
