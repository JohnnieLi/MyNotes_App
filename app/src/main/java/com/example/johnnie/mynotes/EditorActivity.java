package com.example.johnnie.mynotes;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import at.markushi.ui.CircleButton;

public class EditorActivity extends AppCompatActivity {

    private String action;
    private EditText editor;
    private String noteFilter;
    private String oldText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editor = (EditText) findViewById(R.id.editText);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);
        at.markushi.ui.CircleButton ftbutton = (CircleButton) findViewById(R.id.view2);
        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
            ftbutton.setColor(0xff888888);
            ftbutton.invalidate();
            ftbutton.setEnabled(false);
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            editor.setText(oldText);
            editor.requestFocus();
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)){
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finishEditing();
                break;
        }
        return true;
    }


    private void finishEditing() {
        String newText = editor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0) {
                    deleteNote();
                } else if (oldText.equals(newText)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newText);
                }
        }
        finish();
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI,
                noteFilter, null);
        Toast.makeText(this, R.string.note_deleted,
                Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void updateNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }

    public void deleteNote(View view) {
        DialogInterface.OnClickListener dialogOnClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ( which == DialogInterface.BUTTON_POSITIVE){
                            getContentResolver().delete(NotesProvider.CONTENT_URI,
                                    noteFilter, null);
                            Toast.makeText(EditorActivity.this, R.string.note_deleted,
                                    Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_this_note))
                .setPositiveButton(getString(android.R.string.yes), dialogOnClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogOnClickListener)
                .show();
//        getContentResolver().delete(NotesProvider.CONTENT_URI,
//                noteFilter, null);
//        Toast.makeText(this, R.string.note_deleted,
//                Toast.LENGTH_SHORT).show();
//        setResult(RESULT_OK);
//        finish();
    }
}
