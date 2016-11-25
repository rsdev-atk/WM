package ru.rsdev.myapplication.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ru.rsdev.myapplication.Data.DatabaseHelper;
import ru.rsdev.myapplication.Model.Icon;
import ru.rsdev.myapplication.R;

public class DialogIcon extends DialogFragment {

    SQLiteDatabase sdb;
    private DatabaseHelper mDatabaseHelper;

    private int iconPosition;
    public static final String TAG_ITEM_SELECTED = "item";
    ListView listView;
    ArrayList<Icon> icons = new ArrayList<>();

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_icon, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        mDatabaseHelper = new DatabaseHelper(getContext(), DatabaseHelper.DATABASE_NAME, null, 1);
        sdb = mDatabaseHelper.getWritableDatabase();

        Cursor cursor = sdb.query(DatabaseHelper.DATABASE_TABLE_OBJECTS, new String[]{
                        DatabaseHelper._ID, DatabaseHelper.COLUMN_OBJECTS_NAME},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            String iconName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_OBJECTS_NAME));
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            icons.add(new Icon(id, iconName, false));
        }
        cursor.close();

        ArrayList<String> nameList = new ArrayList<>();
        for (int i=0;i<icons.size();i++){
            nameList.add(icons.get(i).name);
        }

        String[] nameArray = nameList.toArray(new String[nameList.size()]);
        listView = (ListView) view.findViewById(R.id.dialog_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, nameArray);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                iconPosition = icons.get(i).id;
            }
        });
        builder.setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(TAG_ITEM_SELECTED, iconPosition);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);

                    }
                });
        return builder.create();
    }
}
