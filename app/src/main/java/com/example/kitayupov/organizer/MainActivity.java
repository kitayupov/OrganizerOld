package com.example.kitayupov.organizer;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1;
    public static final String LOG_TAG = "MainActivity";

    public static final String BODY = "body";
    public static final String TYPE = "type";
    public static final String DATE = "date";
    public static final String RATING = "rating";
    public static final String IS_DONE = "is_done";
    public static final String POSITION = "position";
    public static ArrayAdapter<String> typeAdapter;
    private Context context = this;
    private ArrayList<Note> noteArrayList;
    private ArrayList<String> typeArrayList;
    private NoteAdapter noteAdapter;
    private ListView noteListView;
    private NoteDBHelper noteDBHelper;

    private ActionMode mActionMode;
    private Toolbar toolbar;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Вызывается при создании контекстного режима
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            Log.d(LOG_TAG, "CAB!!!");

            // Заполняем меню
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        // Вызывается при каждом отображении контекстного режима. Всегда вызывается после onCreateActionMode, но
        // может быть вызван несколько раз
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // возвращаем false, если ничего не сделано
        }

        // Вызывается при выборе действия контекстной панели
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.context_menu_delete:
                    Log.d(LOG_TAG, "context delete");
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Вызывается при выходе из контекстного режима
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //Send intent for note creation
                    Intent intent = new Intent(context, EditNoteActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                    Log.d(LOG_TAG, "click fab");
                }
            });
        }
        initialize();
    }

    //Initialize lists and adapter
    private void initialize() {
        noteArrayList = new ArrayList<>();
        typeArrayList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.note_types)));
        noteDBHelper = new NoteDBHelper(context);
        if (!isChangingConfigurations()) {
            readDatabase();
        }
        noteAdapter = new NoteAdapter(context, noteArrayList);
        noteListView = (ListView) findViewById(R.id.noteListView);
        if (noteListView != null) {
            noteListView.setAdapter(noteAdapter);
            /*noteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    contextMenu(position);
                    return false;
                }
            });*/
//        registerForContextMenu(noteListView);
            registerContextualActionBar();
        }
    }

    private void registerContextualActionBar() {

//        final ArrayList<Long> list = new ArrayList<Long>();

        noteListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        noteListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // Здесь можно что-то делать с элементами, которые были выбраны или для которых выбор отменен
//                if (checked) {
//                    list.add(id);
//                } else {
//                    list.remove(id);
//                }
                mode.setTitle(String.valueOf(noteListView.getCheckedItemCount()));
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Отклик на нажатие в CAB
                switch (item.getItemId()) {
                    case R.id.context_menu_delete:
//                        Log.d(LOG_TAG, "deleted: " + Arrays.asList(list).toString());
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Заполнение меню для CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
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

    private void readDatabase() {
        SQLiteDatabase db = noteDBHelper.getReadableDatabase();
        Cursor cursor = db.query(NoteDBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getColumnIndex(BaseColumns._ID);
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
                Log.d(LOG_TAG, "Readed " + item.toString());
                if (!typeArrayList.contains(type)) {
                    typeArrayList.add(type);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.i(LOG_TAG, "Readed " + (noteArrayList == null ? 0 : noteArrayList.size()) + " items.");
        typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, typeArrayList);
    }

    //Receive and parsing information
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.d(LOG_TAG, "result ok!");
            switch (requestCode) {
                case REQUEST_CODE:
                    Log.d(LOG_TAG, "request code!");
                    addNote(data);
                    break;
                default:
                    Log.e(LOG_TAG, "unknown request " + requestCode);
                    break;
            }
        } else {
            Log.e(LOG_TAG, "unknown result " + resultCode);
        }
    }

    private void addNote(Intent data) {
        if (data != null) {
//            String body = data.getStringExtra(BODY);
//            String type = data.getStringExtra(TYPE);
//            long date = data.getLongExtra(DATE, -1);
//            int rating = data.getIntExtra(RATING, 0);
//            Note item = new Note(body, type, date, rating);
            //if extra is not exist, add to the end, otherwise, replace item
            int position = data.getIntExtra(POSITION, Integer.MIN_VALUE);
            position = (position >= 0 && position < noteArrayList.size()) ? position : noteArrayList.size();
            Note item = data.getParcelableExtra(Note.class.getCanonicalName());
            noteArrayList.add(position, item);
            noteAdapter.notifyDataSetChanged();
            Log.i(LOG_TAG, "added: " + item.toString());

            SQLiteDatabase db = noteDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(BODY, item.getBody());
            values.put(TYPE, item.getType());
            values.put(DATE, item.getDate());
            values.put(RATING, item.getRating());
            values.put(IS_DONE, item.getIsDone());
            if (position == noteArrayList.size() - 1) {
                db.insert(NoteDBHelper.TABLE_NAME, null, values);
                Log.d(LOG_TAG, "saved: " + item.toString());
            } else {
//                db.update(NoteDBHelper.TABLE_NAME, values, BaseColumns._ID + "= ?", new String[] );
                db.insert(NoteDBHelper.TABLE_NAME, null, values);
                Log.d(LOG_TAG, "edited: " + item.toString());
                /*String whereClause = BODY + "=? and " + TYPE + "=? and " + DATE + "=? and " +
                        RATING + "=? and " + IS_DONE + "=?";
                String[] whereArgs = new String[]{
                        item.getBody(), item.getType(), String.valueOf(item.getDate()),
                        String.valueOf(item.getRating()), String.valueOf(item.getIsDone())};
                db.delete(NoteDBHelper.TABLE_NAME, whereClause, whereArgs);*/
            }
        }
    }

    private void deleteNote(int position) {
        Note item = noteArrayList.get(position);
        noteArrayList.remove(position);
        noteAdapter.notifyDataSetChanged();
        SQLiteDatabase db = noteDBHelper.getWritableDatabase();
        String whereClause = BODY + "=? and " + TYPE + "=? and " + DATE + "=? and " +
                RATING + "=? and " + IS_DONE + "=?";
        String[] whereArgs = new String[]{
                item.getBody(), item.getType(), String.valueOf(item.getDate()),
                String.valueOf(item.getRating()), String.valueOf(item.getIsDone())};
        db.delete(NoteDBHelper.TABLE_NAME, whereClause, whereArgs);
        Log.d(LOG_TAG, "deleted: " + item.toString());
    }

    private void markAsDone(int position) {
        //todo mark as done
        noteArrayList.get(position).setIsDone(true);
        noteAdapter.notifyDataSetChanged();
        Log.i(LOG_TAG, "marked as done: " + noteArrayList.get(position).toString());
    }

    private void editNote(int position) {
        Intent intent = new Intent(context, EditNoteActivity.class);
//        Note item = noteArrayList.get(position);
//        intent.putExtra(BODY, item.getBody());
//        intent.putExtra(TYPE, item.getType());
//        intent.putExtra(DATE, item.getDate());
//        intent.putExtra(RATING, item.getRating());
        intent.putExtra(Note.class.getCanonicalName(), noteArrayList.get(position));
        intent.putExtra(POSITION, position);
        startActivityForResult(intent, REQUEST_CODE);
        //send note to editor activity and delete note
        deleteNote(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // todo context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isChangingConfigurations()) {
//            saveDatabase();
        }
    }

    private void contextMenu(final int position) {
        final CharSequence[] items = {"Редактировать", "Удалить", null};
        if (noteArrayList.get(position).getIsDone()) {
            items[2] = "Сделать активным";
        } else {
            items[2] = "Отметить выполненным";
        }
        new AlertDialog.Builder(this)
                .setTitle("Настройка задачи")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                editNote(position);
                                break;
                            case 1:
                                deleteNote(position);
                                break;
                            case 2:
                                markAsDone(position);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create()
                .show();
    }

    private void saveFile() {
        try (FileOutputStream fos = openFileOutput("notebase.dat", MODE_PRIVATE)) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            for (Note item : noteArrayList) {
                oos.writeObject(item);
                Log.i(LOG_TAG, "save item " + item.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile() {
        try (FileInputStream fis = openFileInput("notebase.dat")) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            Note item;
            while ((item = (Note) ois.readObject()) != null) {
                noteArrayList.add(item);
                Log.i(LOG_TAG, "read item " + item.toString());
                if (!typeArrayList.contains(item.getType())) {
                    typeArrayList.add(item.getType());
                }
            }
            typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, typeArrayList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveDatabase() {
        Log.d(LOG_TAG, "save database");
        noteDBHelper = new NoteDBHelper(context);
        SQLiteDatabase db = noteDBHelper.getWritableDatabase();
        for (Note item : noteArrayList) {
            ContentValues values = new ContentValues();
            values.put(BODY, item.getBody());
            values.put(TYPE, item.getType());
            values.put(DATE, item.getDate());
            values.put(RATING, item.getRating());
            values.put(IS_DONE, item.getIsDone());
            db.insert(NoteDBHelper.TABLE_NAME, null, values);
        }
    }
}
