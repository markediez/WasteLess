package net.codelets.wasteless;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

/**
 * Created by MarkDiez on 12/26/14.
 */
public class Settings extends Activity {
    TimePicker userTime;
    @Override
    protected  void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_settings);
    }

    // Returns to main activity updating the time for notification
    public void update(View view) {
        Intent timeIntent = getIntent();
        int h, m;
        userTime = (TimePicker)findViewById(R.id.timePicker);
        h = userTime.getCurrentHour();
        m = userTime.getCurrentMinute();
        timeIntent.putExtra("hour", h);
        timeIntent.putExtra("min", m);
        setResult(RESULT_OK, timeIntent);
        finish();
    }

    // Returns to main activity with no change
    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
