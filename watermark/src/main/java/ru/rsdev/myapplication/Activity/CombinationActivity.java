package ru.rsdev.myapplication.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import ru.rsdev.myapplication.Data.DatabaseHelper;
import ru.rsdev.myapplication.R;
import ru.rsdev.myapplication.Utils.TouchImageView;
import ru.rsdev.myapplication.Utils.WatermarkSettings;

public class CombinationActivity extends AppCompatActivity {

    private ImageView backImageView;
    private TouchImageView watermarkImageView;
    private Button saveButton;
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase sdb;

    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar toolbar;
    private DrawerLayout navigationDrawer;
    private ImageView imageView;

    private float dX = 0;
    private float dY = 0;
    String id_images;

    LinearLayout llBottomSheet;
    BottomSheetBehavior bottomSheetBehavior;
    ImageView imgHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combination);

        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.main_coordinator_container);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        navigationDrawer = (DrawerLayout)findViewById(R.id.navigation_drawer);

        setupToolbar();
        setupDrawer();


        backImageView = (ImageView)findViewById(R.id.img_back_image);
        watermarkImageView = (TouchImageView)findViewById(R.id.img_watermark);

        Bitmap backBitmap = WatermarkSettings.getInstance().getBackImageBitmap();
        if(backBitmap != null){
            backImageView.setImageBitmap(backBitmap);
        }


        //Достаем данные о значке
        Bundle extras = getIntent().getExtras();
        id_images = WatermarkSettings.getInstance().getId_image();
        if(id_images != null) {
            setBitmapInWatermarkImage(id_images);
        }

        watermarkImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - motionEvent.getRawX();
                        dY = view.getY() - motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .x(motionEvent.getRawX() + dX)
                                .y(motionEvent.getRawY() + dY)
                                .setDuration(0)
                                .start();

                        WatermarkSettings.getInstance().setWaterBoundsX(motionEvent.getRawX() + dX);
                        WatermarkSettings.getInstance().setWaterBoundsY(motionEvent.getRawY() + dY);
                        break;
                }
                return true;
            }
        });

        saveButton = (Button)findViewById(R.id.btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File folderToSave = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "WatermarkResult");
                if (!folderToSave.exists()) {
                    folderToSave.mkdir();
                }

                OutputStream fOut = null;
                Date date = new Date();

                try {
                    File file = new File(folderToSave, date.toString() +".jpg"); // создать уникальное имя для файла основываясь на дате сохранения
                    fOut = new FileOutputStream(file);

                    //Bitmap bitmap = ((BitmapDrawable)backImageView.getDrawable()).getBitmap();
                    Bitmap bitmap = associationBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // сохранять картинку в jpeg-формате с 100% сжатия.
                    fOut.flush();
                    fOut.close();
                    //MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), file.getAbsolutePath(), file.getName(),  file.getName()); // регистрация в фотоальбоме
                    Toast.makeText(getApplication(), "Файл успешно сохранен", Toast.LENGTH_LONG).show();

                }catch (Exception e) // здесь необходим блок отслеживания реальных ошибок и исключений, общий Exception приведен в качестве примера
                {
                    Toast.makeText(getApplication(),"Не удалось сохранить файл",Toast.LENGTH_SHORT).show();
                }
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
        textView.setText("Разместите значок на фоновом изображении и нажмите кнопку Сохранить");

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

    private Bitmap associationBitmap(){
        Bitmap bitmap = null;
        //Определение размеров изображений и контейнеров
        int widthBackOrigin = backImageView.getDrawable().getIntrinsicWidth();//Ширина оригинала подложки
        int heightBackOrigin = backImageView.getDrawable().getIntrinsicHeight();//Высота оригинала подложки
        int widthWaterOrigin = watermarkImageView.getDrawable().getIntrinsicWidth();//Ширина оригинала значка
        int heightWaterOrigin = watermarkImageView.getDrawable().getIntrinsicHeight();//Высота оригинала значка

        int waterBoundsX = (int) WatermarkSettings.getInstance().getWaterBoundsX();//Отступ для значка по X
        int waterBoundsY = (int) WatermarkSettings.getInstance().getWaterBoundsY();//Отступ для значка по Y


        int widthWaterNew = watermarkImageView.getWidth();//Ширина контейнера подложки
        int heightWaterNew = watermarkImageView.getHeight();//Высота контейнера подложки
        int widthBackContayner = backImageView.getWidth();//Ширина контейнера значка
        int heightBackContayner = backImageView.getHeight();//Высота контейнера значка




        //Определение коэффициентов для изменениия размеров значка в процессе сохранения
        float Kb = (float)widthBackOrigin/widthBackContayner;
        float Kw = (float)widthWaterOrigin/widthWaterNew;
        float K = (float)Kw/Kb;



        bitmap = Bitmap.createBitmap(widthBackOrigin, heightBackOrigin, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        Resources res = getResources();
        Bitmap bitmap1 = ((BitmapDrawable)backImageView.getDrawable()).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable)watermarkImageView.getDrawable()).getBitmap();

        //Изменияем размер 2го Bitmap в зависимости от размера контейнера
        int width = (int)((float)bitmap2.getWidth()/K);
        int height = (int)((float)bitmap2.getHeight()/K);
        Bitmap bitmap3 = Bitmap.createScaledBitmap(bitmap2, width, height, false);

        Drawable drawable1 = new BitmapDrawable(getResources(), bitmap1);//подложка
        Drawable drawable2 = new BitmapDrawable(getResources(), bitmap3);//значек
        drawable1.setBounds(0, 0, widthBackOrigin, heightBackOrigin);

        drawable2.setBounds(waterBoundsX, waterBoundsY, waterBoundsX + (int)((float)widthWaterOrigin/K), waterBoundsY + (int)((float)heightWaterOrigin/K));
        drawable1.draw(c);
        drawable2.draw(c);

        return bitmap;
    }


    private void setBitmapInWatermarkImage(String value) {
        mDatabaseHelper = new DatabaseHelper(this, DatabaseHelper.DATABASE_NAME, null, 1);
        sdb = mDatabaseHelper.getWritableDatabase();


        ArrayList<String> listIcons = new ArrayList<>();
        String[] selectionArgs = new String[] {value};
        Cursor cursor = sdb.query(DatabaseHelper.DATABASE_TABLE_IMAGES, new String[]{
                        DatabaseHelper._ID, DatabaseHelper.COLUMN_IMAGES_ID_IMAGES, DatabaseHelper.COLUMN_IMAGES_URL},
                DatabaseHelper.COLUMN_IMAGES_ID_IMAGES + " = ?", selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            String URI = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGES_URL));
            listIcons.add(URI);
        }
        cursor.close();
        sdb.close();


        //Проверка на число изображений в значке
        if(listIcons.size() == 0){
            Toast.makeText(this,"Добавьте изображений в значок.", Toast.LENGTH_LONG).show();
            finish();
        }else {


            Random random = new Random();
            int rand = random.nextInt(listIcons.size());
            String newIconURI = listIcons.get(rand);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                        Uri.parse(newIconURI));
            } catch (IOException e) {
                e.printStackTrace();
            }
            watermarkImageView.setImageBitmap(bitmap);
        }
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_list_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer(){
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_back_image__menu_item:
                        showBackImageActivity();
                        break;

                    case R.id.icon__menu_item:
                        showIconShowActivity();
                        break;

                    /*
                    case R.id.add_watermark__menu_item:
                        showCombinationActivity();
                        break;
                        */


                }
                item.setCheckable(true);
                navigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        imageView = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aim);
        RoundedBitmapDrawable rBD = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
        rBD.setCornerRadius(Math.max(bitmap.getHeight(),bitmap.getWidth())/2.0f);
        imageView.setImageDrawable(rBD);
    }

    private void showBackImageActivity() {
        Intent intent = new Intent(this, WatermarkActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            navigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showIconActivity(){
        Intent intent = new Intent(this, IconActivity.class);
        startActivity(intent);
    }

    private void showIconShowActivity(){
        Intent intent = new Intent(this, IconShowActivity.class);
        startActivity(intent);
    }


}
