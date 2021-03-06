package net.codelets.wasteless;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mark on 11/26/2014.
 */
public class WasteAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<Food> foodList;
    private LayoutInflater inflater;

    public WasteAdapter (Activity a, ArrayList<Food> f) {
        activity = a;
        foodList = f;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return foodList.size();
    }

    public Object getItem (int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    // Holds data
    public static class ViewHolder {
        public TextView theFood, theExpire;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        final ViewHolder holder;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.list_row, null);
            holder = new ViewHolder();
            holder.theFood = (TextView)vi.findViewById(R.id.tvIngredient);
            holder.theExpire = (TextView)vi.findViewById(R.id.tvExpire);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        if (foodList.size() <= 0) {
            // set holders
            holder.theFood.setText("Buy Groceries!");
            holder.theExpire.setText("n(-,-)n");
        } else {
            // displays ingredient and date
            holder.theFood.setText(foodList.get(position).specify());
            holder.theExpire.setText(foodList.get(position).expireString());
        }
        return vi;
    }
}
