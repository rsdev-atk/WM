package ru.rsdev.myapplication.Activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import ru.rsdev.myapplication.Data.DatabaseHelper;
import ru.rsdev.myapplication.R;
import ru.rsdev.myapplication.Utils.DialogColor;
import ru.rsdev.myapplication.Utils.WatermarkSettings;

public class ModificationImageActivity extends AppCompatActivity
        implements DialogColor.onSomeEventListener{

    private float dX = 0;
    private float dY = 0;

    private ImageView imageWatermark, backImage;
    private SeekBar seekBar;
    private TextView textViewSensitivity;
    private CutAsyncTask cutAsyncTask;
    private ProgressBar progressBar;
    private ImageView paletteImageView;
    private int id_image;

    LinearLayout llBottomSheet;
    BottomSheetBehavior bottomSheetBehavior;
    ImageView imgHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modification_image);

        String str = getIntent().getStringExtra("uri");
        final Uri uri = Uri.parse(str);
        id_image = getIntent().getIntExtra("id",0);

        imageWatermark = (ImageView)findViewById(R.id.img_watermark);
        backImage = (ImageView)findViewById(R.id.img_back_image);

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageWatermark.setImageBitmap(bitmap);

        Bitmap backBitmap = WatermarkSettings.getInstance().getBackImageBitmap();
        if(backBitmap != null){
            backImage.setImageBitmap(backBitmap);
        }

        paletteImageView = (ImageView)findViewById(R.id.img_palette);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        imageWatermark.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        //dX = view.getX() - motionEvent.getRawX();
                        //dY = view.getY() - motionEvent.getRawY();
                        Bitmap src = ((BitmapDrawable)imageWatermark.getDrawable()).getBitmap();
                        int widhtBitmap = src.getWidth();
                        int widthImageView = imageWatermark.getWidth();
                        float scale = (float)widhtBitmap / widthImageView;
                        dX = (int)motionEvent.getX()*scale;
                        dY = (int)motionEvent.getY()*scale;

                        int x = Math.abs((int)dX);
                        int y = Math.abs((int)dY);

                        int pixel = src.getPixel(x,y);
                        int pixelRed = Color.red(pixel);
                        int pixelGreen = Color.green(pixel);
                        int pixelBlue = Color.blue(pixel);

                        Bitmap paletteBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                        paletteBitmap.eraseColor(Color.argb(255,pixelRed,pixelGreen,pixelBlue));
                        paletteImageView.setImageBitmap(paletteBitmap);

                        break;
                }
                return true;
            }

        });

        textViewSensitivity = (TextView)findViewById(R.id.txt_sensitivity);

        seekBar = (SeekBar)findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewSensitivity.setText(String.valueOf(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewSensitivity.setText(String.valueOf(seekBar.getProgress()));
                cutAsyncTask = new CutAsyncTask();
                cutAsyncTask.execute();
            }
        });

        Button saveButton = (Button)findViewById(R.id.btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    Bitmap bitmap = ((BitmapDrawable)imageWatermark.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    Toast.makeText(getApplication(), "Файл успешно сохранен", Toast.LENGTH_LONG).show();
                    Uri uri = Uri.fromFile(file);
                    //id_image
                    //Замена URI на новый файл
                    updateImageURI(uri);
                    //Toast.makeText(getApplication(), String.valueOf(uri), Toast.LENGTH_LONG).show();
                }catch (Exception e) // здесь необходим блок отслеживания реальных ошибок и исключений, общий Exception приведен в качестве примера
                {
                    Toast.makeText(getApplication(), "Не удалось сохранить файл", Toast.LENGTH_LONG).show();
                }

            }
        });

        //Определяем цвет фона автоматически
        getPixelColor();
        int chooseColor = WatermarkSettings.getInstance().getChooseColor();
        if(chooseColor != 0){

            int pixelRed = Color.red(chooseColor);
            int pixelGreen = Color.green(chooseColor);
            int pixelBlue = Color.blue(chooseColor);
            Bitmap paletteBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
            paletteBitmap.eraseColor(Color.argb(255,pixelRed,pixelGreen,pixelBlue));
            paletteImageView.setImageBitmap(paletteBitmap);
        }

        Button chooseColorButton = (Button)findViewById(R.id.btn_choose_pallette);
        chooseColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showColorDialog();
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
        textView.setText("Чтобы отделить фон коснитесь фона или выберите цвет фона вручную. После чего используя регулятор Чувствительность настройте режим отделения фона. Фон будет заменен прозрачным. Процедуру можно повторить если фон неоднороден");

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




    private void showColorDialog(){

        FragmentManager manager = getSupportFragmentManager();
        DialogColor dialogColor = new DialogColor();
        dialogColor.show(manager, "dialog");
    /*
        DialogColor fragment = new DialogColor();
        fragment.setTargetFragment(this, REQUEST_WEIGHT);
        fragment.show(getFragmentManager(), fragment.getClass().getName());
      */

    }


    private void getPixelColor() {

        Bitmap src = ((BitmapDrawable)imageWatermark.getDrawable()).getBitmap();
        int width = (int)(float)src.getWidth()/60;
        int height = (int)(float)src.getHeight()/60;
        int pixel = src.getPixel(width,height);
        int pixelRed = Color.red(pixel);
        int pixelGreen = Color.green(pixel);
        int pixelBlue = Color.blue(pixel);
        Bitmap paletteBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        paletteBitmap.eraseColor(Color.argb(255,pixelRed,pixelGreen,pixelBlue));
        paletteImageView.setImageBitmap(paletteBitmap);

    }



    @Override
    public void someEvent(int s) {
        int pixelRed = Color.red(s);
        int pixelGreen = Color.green(s);
        int pixelBlue = Color.blue(s);
        Bitmap paletteBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        paletteBitmap.eraseColor(Color.argb(255,pixelRed,pixelGreen,pixelBlue));
        paletteImageView.setImageBitmap(paletteBitmap);
    }

    class CutAsyncTask extends AsyncTask<Void, Void, Void> {

        private Bitmap src, dest;
        private int sensitivity = seekBar.getProgress();
        int pixel;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            src = ((BitmapDrawable)imageWatermark.getDrawable()).getBitmap();
            dest = Bitmap.createBitmap(
                    src.getWidth(), src.getHeight(), src.getConfig());
            Bitmap src = ((BitmapDrawable)paletteImageView.getDrawable()).getBitmap();
            int width = 1;
            int height = 1;
            pixel = src.getPixel(width,height);
        }

        @Override
        protected Void doInBackground(Void... params) {
            for(int x = 0; x < src.getWidth(); x++){
                for(int y = 0; y < src.getHeight(); y++){
                    // получим каждый пиксель
                    int pixelColor = src.getPixel(x, y);
                    // получим информацию о прозрачности
                    int pixelAlpha = Color.alpha(pixelColor);
                    // получим цвет каждого пикселя
                    int pixelRed = Color.red(pixelColor);
                    int pixelGreen = Color.green(pixelColor);
                    int pixelBlue = Color.blue(pixelColor);

                    //Проверка цветов
                    if(     pixelRed > Color.red(pixel) - sensitivity &&
                            pixelRed < Color.red(pixel) + sensitivity &&
                            pixelGreen > Color.green(pixel) - sensitivity &&
                            pixelGreen < Color.green(pixel) + sensitivity &&
                            pixelBlue > Color.blue(pixel) - sensitivity &&
                            pixelBlue < Color.blue(pixel) + sensitivity
                            ){
                        pixelAlpha = 0;
                    }
                    int newPixel= Color.argb(
                            pixelAlpha, pixelRed, pixelGreen, pixelBlue);
                    dest.setPixel(x, y, newPixel);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            imageWatermark.setImageBitmap(dest);
            progressBar.setVisibility(View.INVISIBLE);
        }

    }
}
