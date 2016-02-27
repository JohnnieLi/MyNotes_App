package com.example.johnnie.mynotes;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Johnnie on 2016-02-26.
 */
public class NotesCursorAdapter extends CursorAdapter {

    public NotesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.note_list_item, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String noteText = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
        int pos = noteText.indexOf(5);
        if (pos != -1){
            noteText =noteText.substring(0, 15) + "...";
        }

        TextView newNote = (TextView)view.findViewById(R.id.newNote);
//        newNote.setText(noteText);

        String dateCreated = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.NOTE_CREATED));

        newNote.setText(noteText + "                 " + dateCreated.substring(0, 10));
    }
}
