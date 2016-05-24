package com.example.kitayupov.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import java.util.Calendar;

public class ActivityEditor extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_editor;
    private static final String LOG_TAG = "ActivityEditor";

    private EditText bodyEditText;
    private Spinner typeSpinner;
    private RatingBar ratingBar;
    private DatePicker datePicker;
    private int position;
    private Note item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        initialize();
    }

    private void initialize() {
        bodyEditText = (EditText) findViewById(R.id.bodyEditText);
        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        typeSpinner.setAdapter(MainActivity.typeAdapter);

        Intent intent = getIntent();
        position = intent.getIntExtra(MainActivity.POSITION, Integer.MIN_VALUE);
        Log.d(LOG_TAG, "position received: " + position);
        item = intent.getParcelableExtra(Note.class.getCanonicalName());

        if (item != null) {
            bodyEditText.setText(item.getBody());
//            typeSpinner.set
            ratingBar.setRating(item.getRating());
            setDate(item.getDate());
            Log.d(LOG_TAG, "init received: " + item.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                addNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addNote() {
        String string = bodyEditText.getText().toString();
        if (!"".equals(string)) {
            Note note = new Note();
            note.setBody(bodyEditText.getText().toString());
//            note.setType(typeSpinner.);
            note.setRating((int) ratingBar.getRating());
            note.setDate(getDate(datePicker));
            sendResult(note);
        } else {
            bodyEditText.setError(getString(R.string.message_empty_field));
        }
    }

    private void setDate(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePicker.updateDate(year, month, day);
    }

    private long getDate(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime().getTime();
    }

    private void sendResult(Note note) {
        Intent intent = new Intent();
        intent.putExtra(Note.class.getCanonicalName(), note);
        intent.putExtra(MainActivity.POSITION, position);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MainActivity.POSITION, position);
        super.onSaveInstanceState(outState);
    }
}
