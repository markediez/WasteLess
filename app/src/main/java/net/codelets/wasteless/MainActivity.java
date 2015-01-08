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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TreeSet;

// TODO handle itemId to check if a notification with the ID exists or is set

public class MainActivity extends ListActivity implements View.OnClickListener {
    // Global Variables
    final int ADD = 0;
    final int SET = 1;
    final int EDIT = 2;
    private int h, m, itemId;
    private ImageView add, set, help;
    private EditText foodField;
    private Food newFood, myFood;
    private ArrayList<String> keyList, foodList, expireList;
    private WasteAdapter adapter;
    private SharedPreferences pref;

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
        // Inflates item on tap
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String itemKey = keyList.get(position);
                Intent i = new Intent(MainActivity.this, CloseUp.class);
                i.putExtra("itemKey", itemKey);
                startActivityForResult(i, EDIT);
            }
        });

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
                    Toast.makeText(MainActivity.this, "Yup", Toast.LENGTH_SHORT).show();
                    break;
                case SET:
                    // grabs new time
                    h = data.getIntExtra("hour", h);
                    m = data.getIntExtra("min", m);
                    // sets new time
                    setAlarmDaily(h,m);
                    // saves new time
                    pref.edit().putInt("hour", h).apply();
                    pref.edit().putInt("min", m).apply();

                    Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    break;
                case ADD:
                    // Generate unique key
                    Calendar cal = Calendar.getInstance();                      // Get Calendar
                    String userKey = cal.getTime().toString();                  // Generate Key

                    int date[] = data.getIntArrayExtra("date");
                    GregorianCalendar expire = new GregorianCalendar(date[2], date[0], date[1]);
                    DateFormat df = DateFormat.getDateInstance();

                    // add to array list
                    String userFood, userExpire;
                    userFood = foodField.getText().toString()
                            .toUpperCase();
                    userExpire = "Expires: " + df.format(expire.getTime());
                    // add expire in this line
                    foodList.add(userFood);
                    expireList.add(userExpire);

                    // Create new food object
                    newFood = new Food(userFood, userKey, expire, itemId);
                    itemId++;
                    pref.edit().putInt("itemId", itemId);

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
        set = (ImageView)findViewById(R.id.ivSetting);
        help = (ImageView)findViewById(R.id.ivHelp);
        help.setOnClickListener(MainActivity.this);
        set.setOnClickListener(MainActivity.this);
        add.setOnClickListener(MainActivity.this);
        foodField = (EditText)findViewById(R.id.etFood);
        foodList = new ArrayList<String>();
        expireList = new ArrayList<String>();
        keyList = new ArrayList<String>();

        itemId = pref.getInt("itemId", 0);

        // Retrieves food objects and fills arrays
        Set<String> getKeys = getFoodList();
        for (String key : getKeys) {
            myFood = getFood(key, pref);
            foodList.add(myFood.specify());
            expireList.add(myFood.expireString());
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
        PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 6275, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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

    public void setAlarm(Food foodAlarm) {
        int days = -7;
        GregorianCalendar expires = foodAlarm.expireCal();
        GregorianCalendar notificationDay = expires;
        notificationDay.add(Calendar.DAY_OF_MONTH, days);
        // TODO TEST THE COMPARISONS
        int test = notificationDay.compareTo(expires);
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
