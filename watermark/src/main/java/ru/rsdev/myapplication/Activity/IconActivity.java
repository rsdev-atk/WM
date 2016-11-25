package ru.rsdev.myapplication.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.rsdev.myapplication.Adapter.IconAdapter;
import ru.rsdev.myapplication.Data.DatabaseHelper;
import ru.rsdev.myapplication.Model.Icon;
import ru.rsdev.myapplication.R;
import ru.rsdev.myapplication.Utils.DialogIconName;
import ru.rsdev.myapplication.Utils.WatermarkSettings;

public class IconActivity extends AppCompatActivity
implements DialogIconName.onSomeEventListener {

    private static final int CHOOSE_NAME = 1003;


    private DatabaseHelper mDatabaseHelper;
    ArrayList<Icon> icons = new ArrayList<>();
    IconAdapter iconAdapter;
    SQLiteDatabase sdb;


    ListView lvMain;
    LinearLayout llBottomSheet;
    BottomSheetBehavior bottomSheetBehavior;
    ImageView imgHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon);

        mDatabaseHelper = new DatabaseHelper(this, DatabaseHelper.DATABASE_NAME, null, 1);
        sdb = mDatabaseHelper.getWritableDatabase();

        fillDataInDB();
        iconAdapter = new IconAdapter(this, icons);
        lvMain = (ListView) findViewById(R.id.icon_list);
        updateUI();

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Icon icon = (Icon) iconAdapter.getItem(i);
                Integer id = icon.id;
                WatermarkSettings.getInstance().setId_image(String.valueOf(id));
                showCombinationActivity(String.valueOf(id));
            }
        });




        llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setHideable(true);


        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        TextView textView = (TextView)findViewById(R.id.txt_help);
        textView.setText("Выберите значок");

        imgHelp = (ImageView)findViewById(R.id.img_help);
        imgHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        ImageView imgClose = (ImageView)findViewById(R.id.img_close);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });



    }

    @Override
    protected void onResume(){
        super.onResume();
        fillDataInDB();
        updateUI();
    }

    private void addIcon(String string) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_OBJECTS_NAME, string);
        sdb.insert(DatabaseHelper.DATABASE_TABLE_OBJECTS, null, values);
        updateUI();
    }


    private void fillDataInDB() {
        icons.clear();
        sdb = mDatabaseHelper.getWritableDatabase();
        //Cursor cursor = sdb.query(DatabaseHelper.DATABASE_TABLE_OBJECTS, new String[]{DatabaseHelper.COLUMN_OBJECTS_NAME},
        Cursor cursor = sdb.query(DatabaseHelper.DATABASE_TABLE_OBJECTS, new String[]{
                DatabaseHelper._ID, DatabaseHelper.COLUMN_OBJECTS_NAME},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            String iconName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_OBJECTS_NAME));
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            icons.add(new Icon(id, iconName, false));
        }
        cursor.close();
    }

    private void updateUI(){
        fillDataInDB();
        lvMain.setAdapter(iconAdapter);
        iconAdapter.notifyDataSetChanged();
    }

    private void dellIcon(ArrayList<String> dellList){
        sdb = mDatabaseHelper.getWritableDatabase();
        for(int i = 0;i<dellList.size();i++){
        sdb.delete(DatabaseHelper.DATABASE_TABLE_OBJECTS, DatabaseHelper.COLUMN_OBJECTS_NAME + " = ?",
                new String[]{dellList.get(i)});
        }
        sdb.close();
    }

    private void showCombinationActivity(String i){
        Intent intent = new Intent(this, CombinationActivity.class);
        intent.putExtra("id", i);
        startActivity(intent);
    }




    @Override
    public void someEvent(String s) {
        //Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        addIcon(s);
    }
}
