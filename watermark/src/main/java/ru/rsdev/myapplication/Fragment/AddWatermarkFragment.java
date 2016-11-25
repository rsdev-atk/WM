package ru.rsdev.myapplication.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import ru.rsdev.myapplication.Utils.DialogIcon;
import ru.rsdev.myapplication.Utils.TouchImageView;
import ru.rsdev.myapplication.Utils.WatermarkSettings;

public class AddWatermarkFragment extends Fragment {

    private ImageView backImageView;
    private TouchImageView watermarkImageView;
    private static final int CHOOSE_BASE_PHOTO = 1001;
    public static final int TAG_ITEM_SELECTED = 1002;

    private int zoomParam = 30;
    private ProgressBar progressBar;
    private TextView textViewSensitivity;
    private CropFragment cropFragment;
    private FragmentTransaction fTrans;

    private DatabaseHelper mDatabaseHelper;
    SQLiteDatabase sdb;




    private float dX = 0;
    private float dY = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_watermark, null);
        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        textViewSensitivity = (TextView)v.findViewById(R.id.txt_sensitivity);

        backImageView = (ImageView)v.findViewById(R.id.img_back_image_frag2);
        watermarkImageView = (TouchImageView)v.findViewById(R.id.img_watermark);

        Bitmap backBitmap = WatermarkSettings.getInstance().getBackImageBitmap();
            if(backBitmap != null){
                backImageView.setImageBitmap(backBitmap);
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

        Button saveButton = (Button)v.findViewById(R.id.btn_save);
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
                    Toast.makeText(getActivity(), "Файл успешно сохранен", Toast.LENGTH_LONG).show();

                }catch (Exception e) // здесь необходим блок отслеживания реальных ошибок и исключений, общий Exception приведен в качестве примера
                {
                    Toast.makeText(getActivity(),"Не удалось сохранить файл",Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
        Button zoomIn = (Button)v.findViewById(R.id.btn_zoom_in);
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) watermarkImageView.getLayoutParams();
                params.width += zoomParam;
                params.height += zoomParam;
                watermarkImageView.setLayoutParams(params);
            }
        });

        Button zoomOut = (Button)v.findViewById(R.id.btn_zoom_out);
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) watermarkImageView.getLayoutParams();
                params.width -= zoomParam;
                params.height -= zoomParam;
                watermarkImageView.setLayoutParams(params);
            }
        });
*/



        /*
        seekBar = (SeekBar)v.findViewById(R.id.seekbar);
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
        */

        /*
        Button cropButton = (Button)v.findViewById(R.id.btn_crop);
        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WatermarkSettings.getInstance().setWatermarkBitmap(((BitmapDrawable)watermarkImageView.getDrawable()).getBitmap());
                cropFragment = new CropFragment();
                fTrans = getActivity().getSupportFragmentManager().beginTransaction();
                fTrans.replace(R.id.fragment_container, cropFragment);
                fTrans.commit();
            }
        });
        */

        /////////////////////////////////////////////////////////////////////////////////
        // /////////Старый функционал выбора изображения
        /////////////////////////////////////////////////////////////////////////////////
        /*
        Bitmap watermarkBitmap = WatermarkSettings.getInstance().getWatermarkBitmap();
        if(watermarkBitmap != null){
            watermarkImageView.setImageBitmap(watermarkBitmap);
        }else {
            if(WatermarkSettings.getInstance().getSelectedImageUri() != null) {
                Bitmap galleryBitmap = null;
                try {
                    galleryBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                            WatermarkSettings.getInstance().getSelectedImageUri());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                backImageView.setImageBitmap(galleryBitmap);
                pickImageFromGallery();
            }

        }
        */


        Button openButton = (Button)v.findViewById(R.id.btn_open_image);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIconDialog();
                /*
                Toast.makeText(getActivity(), "WORK",Toast.LENGTH_SHORT).show();
                Bitmap galleryBitmap = null;
                try {
                    galleryBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                            WatermarkSettings.getInstance().getSelectedImageUri());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                backImageView.setImageBitmap(galleryBitmap);
                pickImageFromGallery();
                */
            }
        });






        return v;
    }


    private void pickImageFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, CHOOSE_BASE_PHOTO);
    }

    private void showIconDialog(){
        DialogIcon fragment = new DialogIcon();
        fragment.setTargetFragment(this, TAG_ITEM_SELECTED);
        fragment.show(getFragmentManager(), fragment.getClass().getName());
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case CHOOSE_BASE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {

                    Uri selectedImageUri = imageReturnedIntent.getData();
                    WatermarkSettings.getInstance().setSelectedWatermarkUri(selectedImageUri);
                    Bitmap galleryBitmap = null;
                    try {
                        galleryBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                                selectedImageUri);
                    }  catch (IOException e) {
                        e.printStackTrace();
                    }
                    watermarkImageView.setImageBitmap(galleryBitmap);
                }
                break;

            case TAG_ITEM_SELECTED:
                //id значка для вставки
                int result = imageReturnedIntent.getIntExtra(DialogIcon.TAG_ITEM_SELECTED, -1);
                //Toast.makeText(getActivity(),String.valueOf(result),Toast.LENGTH_SHORT).show();
                mDatabaseHelper = new DatabaseHelper(getContext(), DatabaseHelper.DATABASE_NAME, null, 1);
                sdb = mDatabaseHelper.getWritableDatabase();

                ArrayList<String> listIcons = new ArrayList<>();
                String[] selectionArgs = new String[] {String.valueOf(result)};
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

                //Toast.makeText(getContext(),String.valueOf(listIcons.size()),Toast.LENGTH_SHORT).show();

                Random random = new Random();
                int rand = random.nextInt(listIcons.size() + 1);
                String newIconURI = listIcons.get(rand);

                //bitmap
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),
                            Uri.parse(newIconURI));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                watermarkImageView.setImageBitmap(bitmap);






        }

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


    /*
    class CutAsyncTask extends AsyncTask<Void, Void, Void> {

        private Bitmap src, dest;
        private int sensitivity = seekBar.getProgress();
        int pixel;

        int backgroundPixel;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

            //containerRelativeLayout.setDrawingCacheEnabled(true);
            //containerRelativeLayout.buildDrawingCache();

            src = ((BitmapDrawable)watermarkImageView.getDrawable()).getBitmap();
            dest = Bitmap.createBitmap(
                    src.getWidth(), src.getHeight(), src.getConfig());


            //Определение цвета фона автоматически
            //1.Получение цветов пивселей с разных сторон
            int width1 = (int)(float)src.getWidth()/60;
            int height1 = (int)(float)src.getHeight()/60;
            int pixel1 = src.getPixel(width1,height1);
            int pixelRed = Color.red(pixel1);
            int pixelGreen = Color.green(pixel1);
            int pixelBlue = Color.blue(pixel1);

            int width2 = src.getWidth() - (int)(float)src.getWidth()/60;
            int height2 = (int)(float)src.getHeight()/60;
            int pixel2 = src.getPixel(width2,height2);


            int width3 = src.getWidth() - (int)(float)src.getWidth()/60;
            int height3 = src.getHeight() - (int)(float)src.getHeight()/60;
            int pixel3 = src.getPixel(width3,height3);

            int width4 = (int)(float)src.getWidth()/60;
            int height4 = src.getHeight() - (int)(float)src.getHeight()/60;
            int pixel4 = src.getPixel(width4,height4);

            //2. Сравниваем цвета
            int colorRed12 = Math.abs(Color.red(pixel1) - (Color.red(pixel1) + Color.red(pixel2))/2);
            int colorRed34 = Math.abs(Color.red(pixel3) - (Color.red(pixel3) + Color.red(pixel4))/2);
            int colorGreen12 = Math.abs(Color.green(pixel1) - (Color.green(pixel1) + Color.green(pixel2))/2);
            int colorGreen34 = Math.abs(Color.green(pixel3) - (Color.green(pixel3) + Color.green(pixel4))/2);
            int colorBlue12 = Math.abs(Color.blue(pixel1) - (Color.blue(pixel1) + Color.blue(pixel2))/2);
            int colorBlue34 = Math.abs(Color.blue(pixel3) - (Color.blue(pixel3) + Color.blue(pixel4))/2);

            if(     Math.abs(colorRed12-colorRed34)<10 &&
                    Math.abs(colorGreen12-colorGreen34)<10 &&
                    Math.abs(colorBlue12-colorBlue34)<10){
                pixel = pixel1;
            }





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
            watermarkImageView.setImageBitmap(dest);
            progressBar.setVisibility(View.INVISIBLE);


        }

    }
    */

}
