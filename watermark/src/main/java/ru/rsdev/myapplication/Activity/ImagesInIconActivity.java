package ru.rsdev.myapplication.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.rsdev.myapplication.Adapter.ImagesInIconAdapter;
import ru.rsdev.myapplication.Data.DatabaseHelper;
import ru.rsdev.myapplication.Model.Images;
import ru.rsdev.myapplication.R;
import ru.rsdev.myapplication.Utils.DialogIconName;

public class ImagesInIconActivity extends AppCompatActivity
        implements DialogIconName.onSomeEventListener{

    private static final int CHOOSE_BASE_PHOTO = 1001;

    ArrayList<Images> images = new ArrayList<>();
    ImagesInIconAdapter imagesInIconAdapter;
    private DatabaseHelper mDatabaseHelper;
    SQLiteDatabase sdb;

    ListView imagesList;
    Button addButton, dellButton, backButton, renameButton;
    int id_images;
    String nameIcon;
    TextView headText;
    LinearLayout llBottomSheet;
    BottomSheetBehavior bottomSheetBehavior;
    ImageView imgHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_in_icon);

        imagesList = (ListView)findViewById(R.id.lv_images);
        addButton = (Button)findViewById(R.id.btn_images_add);
        mDatabaseHelper = new DatabaseHelper(this, DatabaseHelper.DATABASE_NAME, null, 1);
        //sdb = mDatabaseHelper.getWritableDatabase();

        headText = (TextView)findViewById(R.id.head_text_icon);

        Bundle extras = getIntent().getExtras();
        id_images = extras.getInt("id");
        nameIcon = extras.getString("name");
        headText.setText(nameIcon);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, CHOOSE_BASE_PHOTO);
            }
        });

        dellButton = (Button)findViewById(R.id.btn_images_dell);
        dellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> dellList = new ArrayList<>();
                for (Images images : imagesInIconAdapter.getChek()) {
                    if (images.chek)
                        dellList.add(images.uri);
                }
                dellIcon(dellList);
                fillDataInDB();
                updateUI();
            }
        });

        backButton = (Button)findViewById(R.id.btn_back_step);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        renameButton = (Button)findViewById(R.id.btn_images_rename);
        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Вызов диалога для изменения имени
                FragmentManager manager = getSupportFragmentManager();
                DialogIconName dialogIconName = new DialogIconName();

                Bundle bundle = new Bundle();
                bundle.putInt("id", id_images);
                bundle.putString("name", nameIcon);
                dialogIconName.setArguments(bundle);
                dialogIconName.show(manager, "dialog");
            }
        });

        fillDataInDB();
        updateUI();

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
        textView.setText("Вы можете добавить изображения знака кнопкой добавить. Чтобы обрезать изображение значка, выберите его и нажмите кнопку Обрезать.");

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
        headText.setText(nameIcon);

    }

    private void updateUI(){
        imagesInIconAdapter = new ImagesInIconAdapter(this,images);
        imagesList.setAdapter(imagesInIconAdapter);
    }

    private void fillDataInDB() {
        images.clear();
        sdb = mDatabaseHelper.getWritableDatabase();

/*
        Cursor cursor = sdb.query(DatabaseHelper.DATABASE_TABLE_IMAGES, new String[]{
                        DatabaseHelper._ID, DatabaseHelper.COLUMN_IMAGES_ID_IMAGES, DatabaseHelper.COLUMN_IMAGES_URL},
                null, null, null, null, null);

*/
        // переменные для query
        String[] columns = null;
        String selection = null;
        String[] selectionArgs = new String[] {String.valueOf(id_images)};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Cursor cursor = sdb.query(DatabaseHelper.DATABASE_TABLE_IMAGES, new String[]{
                        DatabaseHelper._ID, DatabaseHelper.COLUMN_IMAGES_ID_IMAGES, DatabaseHelper.COLUMN_IMAGES_URL},
                DatabaseHelper.COLUMN_IMAGES_ID_IMAGES + " = ?", selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            String URI = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGES_URL));

            images.add(new Images(id, URI, false));
        }
        cursor.close();
        sdb.close();
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case CHOOSE_BASE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    sdb = mDatabaseHelper.getWritableDatabase();
                    String selectedImageUri = imageReturnedIntent.getData().toString();
                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_IMAGES_ID_IMAGES, id_images);
                    values.put(DatabaseHelper.COLUMN_IMAGES_URL, selectedImageUri);
                    sdb.insert(DatabaseHelper.DATABASE_TABLE_IMAGES, null, values);
                    sdb.close();

                    fillDataInDB();
                    updateUI();
                }
        }
    }

    private void dellIcon(ArrayList<String> dellList){
        sdb = mDatabaseHelper.getWritableDatabase();
        for(int i = 0;i<dellList.size();i++){
            sdb.delete(DatabaseHelper.DATABASE_TABLE_IMAGES, DatabaseHelper.COLUMN_IMAGES_URL + " = ?",
                    new String[]{dellList.get(i)});
        }
        sdb.close();
    }




    @Override
    public void someEvent(String newName) {

        DatabaseHelper mDatabaseHelper = new DatabaseHelper(this, DatabaseHelper.DATABASE_NAME, null, 1);
        SQLiteDatabase sdb = mDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_OBJECTS_NAME, String.valueOf(newName));
        sdb.update(DatabaseHelper.DATABASE_TABLE_OBJECTS,
                values,
                "_id = ?",
                new String[] {Integer.toString(id_images)});
        sdb.close();


    }
}
