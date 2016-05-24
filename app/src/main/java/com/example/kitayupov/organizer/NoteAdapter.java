package com.example.kitayupov.organizer;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class NoteAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Note> noteArrayList;

    public NoteAdapter(Context context, ArrayList<Note> noteArrayList) {
        this.noteArrayList = noteArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return noteArrayList.size();
    }

    @Override
    public Note getItem(int position) {
        return noteArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_layout, null);
        }
        fillView(convertView, position);
        return convertView;
    }

    private void fillView(View view, int position) {
        final Note item = getItem(position);
        if (item.getIsDone()) {
            ((CheckBox) view.findViewById(R.id.checkBox)).setChecked(true);
            return;
        }
        ((TextView) view.findViewById(R.id.bodyTextView)).setText(item.getBody());
        ((TextView) view.findViewById(R.id.typeTextView)).setText(item.getType());
        ((RatingBar) view.findViewById(R.id.itemRatingBar)).setRating(item.getRating());
        long date = item.getDate();
        if (date > 0) {
            TextView dateTextView = (TextView) view.findViewById(R.id.dateTextView);
            dateTextView.setText(DateFormat.format("dd.MM.yyyy", item.getDate()));
            long today = System.currentTimeMillis();
            if (today > date) {
                dateTextView.setTextColor(Color.RED);
            }
        }
    }
}
