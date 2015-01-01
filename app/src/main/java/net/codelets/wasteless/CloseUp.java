package net.codelets.wasteless;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

/**
 * Created by MarkDiez on 12/31/14.
 */
public class CloseUp extends Activity{
    String expireDate;
    SharedPreferences sp;
    Food food;
    TextView title, expire;
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_closeup);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        init();
        set();
    }

    // Sets the activity
    private void set() {
        title.setText(food.specify());
        expire.setText("Best by " + expireDate );
    }

    // Initializes Variables
    private void init() {
        // Retrieve food
        String itemKey = getIntent().getStringExtra("itemKey");
        MainActivity util = new MainActivity();
        food = util.getFood(itemKey, sp);

        // Set activity vars
        title = (TextView)findViewById(R.id.tvTitle);
        expire = (TextView)findViewById(R.id.tvExpiration);

        // String for time
        expireDate = food.expireString();
    }
}
