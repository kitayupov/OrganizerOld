package com.example.kitayupov.organizer;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
            return;
        }
        TextView bodyTextView = (TextView) view.findViewById(R.id.bodyTextView);
        TextView typeTextView = (TextView) view.findViewById(R.id.typeTextView);
        TextView dateTextView = (TextView) view.findViewById(R.id.dateTextView);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.itemRatingBar);
        bodyTextView.setText(item.getBody());
        typeTextView.setText(item.getType());
        ratingBar.setRating(item.getRating());
        long date = item.getDate();
        if (date > 0){
            dateTextView.setText(DateFormat.format("dd.MM.yyyy", item.getDate()));
            if (date < System.currentTimeMillis()) {
                dateTextView.setTextColor(Color.RED);
            }
        }
    }
}
