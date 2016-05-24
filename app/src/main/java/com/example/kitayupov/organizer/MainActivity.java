package com.example.kitayupov.organizer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final int LAYOUT = R.layout.activity_main;

    public static final int REQUEST_CODE = 0;

    public static final String BODY = "body";
    public static final String TYPE = "type";
    public static final String DATE = "date";
    public static final String RATING = "rating";
    public static final String IS_DONE = "is_done";
    public static final String POSITION = "position";
    public static final String LOG_TAG = "MainActivity";

    private Context context;
    public static ArrayAdapter<String> typeAdapter;
    private ArrayList<Note> noteArrayList;
    private ArrayList<String> typeArrayList;
    private NoteAdapter mAdapter;
    private ListView mListView;
    private NoteDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Send intent for note creation
                    Intent intent = new Intent(context, ActivityEditor.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });
        }
        initialize();
    }

    //Initialize lists and adapter
    private void initialize() {
        noteArrayList = new ArrayList<>();
        typeArrayList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.note_types)));
        dbHelper = new NoteDBHelper(context);
        mAdapter = new NoteAdapter(context, noteArrayList);
        mListView = (ListView) findViewById(R.id.noteListView);
        if (mListView != null) {
            mListView.setAdapter(mAdapter);
            readDatabase();
            registerContextualActionBar();
        }
    }

    private void readDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NoteDBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int bodyIndex = cursor.getColumnIndex(BODY);
            int typeIndex = cursor.getColumnIndex(TYPE);
            int dateIndex = cursor.getColumnIndex(DATE);
            int ratingIndex = cursor.getColumnIndex(RATING);
            int isDoneIndex = cursor.getColumnIndex(IS_DONE);
            String type;
            do {
                Note item = new Note(cursor.getString(bodyIndex),
                        type = cursor.getString(typeIndex),
                        cursor.getLong(dateIndex),
                        cursor.getInt(ratingIndex),
                        cursor.getInt(isDoneIndex) == 1);
                noteArrayList.add(item);
                Log.d(LOG_TAG, "readed " + item.toString());
                if (!typeArrayList.contains(type)) {
                    typeArrayList.add(type);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.i(LOG_TAG, "total readed " + (noteArrayList == null ? 0 : noteArrayList.size()) + " items.");
        typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, typeArrayList);
    }

    private void registerContextualActionBar() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ActivityEditor.class);
                intent.putExtra(POSITION, position);
                intent.putExtra(Note.class.getCanonicalName(), mAdapter.getItem(position));
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            ArrayList<Note> list = new ArrayList<>();

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                mode.setTitle(String.valueOf(mListView.getCheckedItemCount()));
                Note item = mAdapter.getItem(position);
                if (checked) {
                    list.add(item);
                } else {
                    list.remove(item);
                }
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.context_menu_delete:
                        deleteNote(list);
                        list = new ArrayList<>();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_contextual, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Здесь можно обновить явление, если CAB был удален. По умолчанию с выбранных элементов снимается выбор.
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Здесь можно обновлять CAB при запросе invalidate()
                return false;
            }
        });
    }

    private void deleteNote(ArrayList<Note> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (Note item : list) {
            String whereClause = BODY + "=? and " + TYPE + "=? and " + DATE + "=? and " +
                    RATING + "=? and " + IS_DONE + "=?";
            String[] whereArgs = new String[]{
                    item.getBody(), item.getType(), String.valueOf(item.getDate()),
                    String.valueOf(item.getRating()), String.valueOf(item.getIsDone() ? 1 : 0)};
            db.delete(NoteDBHelper.TABLE_NAME, whereClause, whereArgs);
            noteArrayList.remove(item);
        }
        mAdapter.notifyDataSetChanged();
    }

    //Receive and parsing information
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.d(LOG_TAG, "result ok!");
            switch (requestCode) {
                case REQUEST_CODE:
                    addNote(data);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addNote(Intent data) {
        if (data != null) {
            int position = data.getIntExtra(POSITION, Integer.MIN_VALUE);
            position = (position >= 0 && position < noteArrayList.size()) ? position : noteArrayList.size();
            Note item = data.getParcelableExtra(Note.class.getCanonicalName());

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(BODY, item.getBody());
            values.put(TYPE, item.getType());
            values.put(DATE, item.getDate());
            values.put(RATING, item.getRating());
            values.put(IS_DONE, item.getIsDone() ? 1 : 0);

            if (position == noteArrayList.size()) {
                db.insert(NoteDBHelper.TABLE_NAME, null, values);
                Log.d(LOG_TAG, "insert #" + position + item.toString());
            } else {
                updateRow(noteArrayList.get(position), values);
                Log.d(LOG_TAG, "update #" + position + item.toString());
            }

            noteArrayList.add(position, item);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void updateRow(Note item, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = BODY + "=? and " + TYPE + "=? and " + DATE + "=? and " +
                RATING + "=? and " + IS_DONE + "=?";
        String[] whereArgs = new String[]{
                item.getBody(), item.getType(), String.valueOf(item.getDate()),
                String.valueOf(item.getRating()), String.valueOf(item.getIsDone() ? 1 : 0)};
        db.update(NoteDBHelper.TABLE_NAME, values, whereClause, whereArgs);
        noteArrayList.remove(item);
    }

    private void markAsDone(int position) {
        noteArrayList.get(position).setIsDone(true);
        mAdapter.notifyDataSetChanged();
        Log.i(LOG_TAG, "marked as done: " + noteArrayList.get(position).toString());
    }

    private void editNote(int position) {
        Intent intent = new Intent(context, ActivityEditor.class);
        intent.putExtra(Note.class.getCanonicalName(), noteArrayList.get(position));
        intent.putExtra(POSITION, position);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_contextual, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
