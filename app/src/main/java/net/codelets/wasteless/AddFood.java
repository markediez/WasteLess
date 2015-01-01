package net.codelets.wasteless;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

/**
 * Created by Mark Diez on 12/2/2014.
 */
public class AddFood extends Activity implements View.OnClickListener {
    int[] date;
    Button yes, no;
    Intent data;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_add);

        yes = (Button)findViewById(R.id.bYes);
        no = (Button)findViewById(R.id.bNo);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bYes:
                int month, day, year;

                final DatePicker expire = (DatePicker)findViewById(R.id.dtExpire);
                month = expire.getMonth();
                day = expire.getDayOfMonth();
                year = expire.getYear();

                date = new int[]{month, day, year};
                data = getIntent();
                data.putExtra("date", date);
                setResult(RESULT_OK, data);
                break;
            case R.id.bNo:
                setResult(RESULT_CANCELED);
                break;
        }

        finish();
    }
}
