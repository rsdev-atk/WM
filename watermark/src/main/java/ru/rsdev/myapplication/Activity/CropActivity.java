package ru.rsdev.myapplication.Activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import ru.rsdev.myapplication.Data.DatabaseHelper;
import ru.rsdev.myapplication.R;
import ru.rsdev.myapplication.Utils.WatermarkSettings;

public class CropActivity extends AppCompatActivity {

    private com.isseiaoki.simplecropview.CropImageView mCropView;
    private int id_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        String str = getIntent().getStringExtra("uri");
        final Uri uri = Uri.parse(str);
        id_image = getIntent().getIntExtra("id",0);

        mCropView = (CropImageView) findViewById(R.id.cropImageView);
        //Bitmap bitmap = WatermarkSettings.getInstance().getWatermarkBitmap();

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }


        mCropView.setImageBitmap(bitmap);
        Button saveButtonCrop = (Button)findViewById(R.id.btn_save);
        saveButtonCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap cropBitmap = mCropView.getCroppedBitmap();
                WatermarkSettings.getInstance().setWatermarkBitmap(cropBitmap);
                //Сохранение
                OutputStream fOut = null;
                Date date = new Date();
                File folderToSave = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "WatermarkIcon");
                if (!folderToSave.exists()) {
                    folderToSave.mkdir();
                }
                try {
                    File file = new File(folderToSave, date.toString() +".png"); // создать уникальное имя для файла основываясь на дате сохранения
                    fOut = new FileOutputStream(file);
                    Bitmap bitmap = mCropView.getCroppedBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    Toast.makeText(getApplication(), "Файл успешно сохранен", Toast.LENGTH_LONG).show();
                    Uri uri = Uri.fromFile(file);
                    updateImageURI(uri);

                }catch (Exception e) // здесь необходим блок отслеживания реальных ошибок и исключений, общий Exception приведен в качестве примера
            {
                Toast.makeText(getApplication(),"Не удалось сохранить файл",Toast.LENGTH_SHORT).show();
            }
                finish();
            }
        });

        Button closeButton = (Button)findViewById(R.id.btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void updateImageURI(Uri uri){
        DatabaseHelper mDatabaseHelper = new DatabaseHelper(this, DatabaseHelper.DATABASE_NAME, null, 1);
        SQLiteDatabase sdb = mDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IMAGES_URL, String.valueOf(uri));
        sdb.update(DatabaseHelper.DATABASE_TABLE_IMAGES,
                values,
                "_id = ?",
                new String[] {Integer.toString(id_image)});
        sdb.close();
    }



}
