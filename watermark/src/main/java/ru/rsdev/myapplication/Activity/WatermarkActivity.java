package ru.rsdev.myapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import ru.rsdev.myapplication.R;
import ru.rsdev.myapplication.Utils.WatermarkSettings;

public class WatermarkActivity extends AppCompatActivity {

    private static final int CHOOSE_BASE_PHOTO_OPEN = 1001;
    private static final int CHOOSE_BASE_PHOTO_IMPORT = 1002;

    Button openBackImage;


    private ImageView imageView;

    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar toolbar;
    private DrawerLayout navigationDrawer;
    LinearLayout llBottomSheet;
    BottomSheetBehavior bottomSheetBehavior;
    ImageView imgHelp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watermark);

        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.main_coordinator_container);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        navigationDrawer = (DrawerLayout)findViewById(R.id.navigation_drawer);

        setupToolbar();
        setupDrawer();

        openBackImage = (Button)findViewById(R.id.btn_open_back_image);
        //backImage = (ImageView) findViewById(R.id.img_back_image);
        openBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageBack();
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
        textView.setText("Выберите фоновый рисунок");

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

    private void showIconActivity(){
        Intent intent = new Intent(this, IconActivity.class);
        startActivity(intent);
    }

    private void showIconShowActivity(){
        Intent intent = new Intent(this, IconShowActivity.class);
        startActivity(intent);
    }

    private void openImagetoImportBack() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, CHOOSE_BASE_PHOTO_IMPORT);
    }

    private void openImageBack() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        File folderToSave = new File(Environment.getExternalStorageDirectory() +
                File.separator + "WatermarkBack/");
        Uri uri = Uri.fromFile(folderToSave);
        photoPickerIntent.setDataAndType(uri,"image/*");
        startActivityForResult(photoPickerIntent, CHOOSE_BASE_PHOTO_OPEN);
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case CHOOSE_BASE_PHOTO_OPEN:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImageUri = imageReturnedIntent.getData();
                    WatermarkSettings.getInstance().setSelectedImageUri(selectedImageUri);
                    Bitmap galleryBitmap = null;
                    try {
                        galleryBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                selectedImageUri);
                    }  catch (IOException e) {
                        e.printStackTrace();
                    }
                    //backImage.setImageBitmap(galleryBitmap);
                    WatermarkSettings.getInstance().setBackImageBitmap(galleryBitmap);
                    showIconActivity();
                }
                break;

            case CHOOSE_BASE_PHOTO_IMPORT:
                if (resultCode == Activity.RESULT_OK) {
                    //Проверяем существование папки и создаем ее
                    File folderToSave = new File(Environment.getExternalStorageDirectory() +
                            File.separator + "WatermarkBack");
                    if (!folderToSave.exists()) {
                        folderToSave.mkdir();
                    }

                    OutputStream fOut = null;
                    Date date = new Date();

                    Uri selectedImageUri = imageReturnedIntent.getData();

                    try {
                        File file = new File(folderToSave, date.toString() +".jpg"); // создать уникальное имя для файла основываясь на дате сохранения
                        fOut = new FileOutputStream(file);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                selectedImageUri);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                    }catch (Exception e) // здесь необходим блок отслеживания реальных ошибок и исключений, общий Exception приведен в качестве примера
                    {
                        // return e.getMessage();
                    }

                    Toast.makeText(this, "Файл успешно импортирован", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public static boolean copy(String from, String to) {
        try {
            File fFrom = new File(from);
            File fTo = new File(to + "123.jpg");
            InputStream in = new FileInputStream(fFrom); // Создаем потоки
            OutputStream out = new FileOutputStream(fTo);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close(); // Закрываем потоки
            out.close();

        } catch (FileNotFoundException ex) { // Обработка ошибок
        } catch (IOException e) { // Обработка ошибок
        }
        return true; // При удачной операции возвращаем true
    }



    private void showCombinationActivity(){
        Intent intent = new Intent(this, CombinationActivity.class);
        startActivity(intent);
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
    public void onBackPressed() {
        if (navigationDrawer !=null && navigationDrawer.isDrawerVisible(GravityCompat.START)){
            navigationDrawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            navigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackbar(String message){
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

}
