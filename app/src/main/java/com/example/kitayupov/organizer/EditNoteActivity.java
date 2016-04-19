package com.example.kitayupov.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

public class EditNoteActivity extends AppCompatActivity {

    private EditText bodyEditText;
    private Spinner typeSpinner;
    private RatingBar ratingBar;
    private CalendarView calendarView;
    private ImageButton imageButton;

    private int position;
    private Note item;

    private static final String LOG_TAG = "EditNoteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note_layout);
        if (savedInstanceState != null){
            item = savedInstanceState.getParcelable(Note.class.getCanonicalName());
            Log.d(LOG_TAG, "onCreate received: " + item.toString());
        }
        initialize();
    }

    private void initialize() {
        bodyEditText = (EditText) findViewById(R.id.bodyEditText);
        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });

        typeSpinner.setAdapter(MainActivity.typeAdapter);
        Intent intent = getIntent();
        position = intent.getIntExtra(MainActivity.POSITION, Integer.MIN_VALUE);
        Log.d(LOG_TAG, "position received: " + position);
        item = intent.getParcelableExtra(Note.class.getCanonicalName());
        if (item != null) {
            bodyEditText.setText(item.getBody());
//            typeSpinner.set
            ratingBar.setRating(item.getRating());
            calendarView.setDate(item.getDate());
            Log.d(LOG_TAG, "init received: " + item.toString());
        }
    }

    private void addNote() {
        String string = bodyEditText.getText().toString();
        if (!"".equals(string)) {
            Note note = new Note();
            note.setBody(bodyEditText.getText().toString());
//            note.setType(typeSpinner.);
            note.setRating((int) ratingBar.getRating());
            note.setDate(calendarView.getDate());
            sendResult(note);
        } else {
            Toast.makeText(this, "Empty body", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendResult(Note note) {
        Intent intent = new Intent();
        intent.putExtra(Note.class.getCanonicalName(), note);
//        intent.putExtra(MainActivity.BODY, note.getBody());
//        intent.putExtra(MainActivity.TYPE, note.getType());
//        intent.putExtra(MainActivity.DATE, note.getDate());
//        intent.putExtra(MainActivity.RATING, note.getRating());
        intent.putExtra(MainActivity.POSITION, position);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        bodyEditText.setText(savedInstanceState.getString(MainActivity.BODY));
        typeSpinner.setSelection(savedInstanceState.getInt(MainActivity.TYPE));
        ratingBar.setRating(savedInstanceState.getFloat(MainActivity.RATING));
        calendarView.setDate(savedInstanceState.getLong(MainActivity.DATE));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(MainActivity.BODY, bodyEditText.getText().toString());
        outState.putInt(MainActivity.TYPE, typeSpinner.getSelectedItemPosition());
        outState.putFloat(MainActivity.RATING, ratingBar.getRating());
        outState.putLong(MainActivity.DATE, calendarView.getDate());
        super.onSaveInstanceState(outState);
    }
}
