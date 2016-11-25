package ru.rsdev.myapplication.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import ru.rsdev.myapplication.Utils.DialogColor;
import ru.rsdev.myapplication.R;
import ru.rsdev.myapplication.Utils.WatermarkSettings;

public class ModificationWatermarkFragment extends Fragment {
    private static final int CHOOSE_BASE_PHOTO_OPEN = 1001;
    private static final int CHOOSE_BASE_PHOTO_IMPORT = 1002;

    private static final int REQUEST_WEIGHT = 1;
    private static final int REQUEST_ANOTHER_ONE = 2;

    private float dX = 0;
    private float dY = 0;

    private ImageView imageWatermark;
    private SeekBar seekBar;
    private TextView textViewSensitivity;
    //private RelativeLayout containerRelativeLayout;
    private CutAsyncTask cutAsyncTask;
    private ProgressBar progressBar;

    private CropFragment cropFragment;
    private FragmentTransaction fTrans;
    private ImageView paletteImageView;
    int colorRGB;


    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_modification_watermark, null);
        //containerRelativeLayout = (RelativeLayout)v.findViewById(R.id.container);
        paletteImageView = (ImageView)v.findViewById(R.id.img_palette);
        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);

        imageWatermark = (ImageView) v.findViewById(R.id.img_watermark);

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


        Button openButton = (Button) v.findViewById(R.id.btn_open);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });


        textViewSensitivity = (TextView)v.findViewById(R.id.txt_sensitivity);


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

        Button saveButton = (Button)v.findViewById(R.id.btn_save);
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
                    Toast.makeText(getActivity(), "Файл успешно сохранен", Toast.LENGTH_LONG).show();

                }catch (Exception e) // здесь необходим блок отслеживания реальных ошибок и исключений, общий Exception приведен в качестве примера
                {
                    // return e.getMessage();
                }

            }
        });



        Button importButton = (Button)v.findViewById(R.id.btn_import);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cutAsyncTask = new CutAsyncTask();
                cutAsyncTask.execute();
            }
        });


        final ImageView backImage = (ImageView)v.findViewById(R.id.img_back_image_frag3);
        Bitmap backBitmap = WatermarkSettings.getInstance().getBackImageBitmap();
        if(backBitmap != null){
            backImage.setImageBitmap(backBitmap);
        }

        Button cropButton = (Button)v.findViewById(R.id.btn_crop);
        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WatermarkSettings.getInstance().setWatermarkBitmap(((BitmapDrawable)imageWatermark.getDrawable()).getBitmap());
                cropFragment = new CropFragment();
                fTrans = getActivity().getSupportFragmentManager().beginTransaction();
       //         fTrans.replace(R.id.fragment_container, cropFragment);
                fTrans.commit();
            }
        });


        Bitmap watermarkBitmap = WatermarkSettings.getInstance().getWatermarkBitmap();
        if(watermarkBitmap != null){
            imageWatermark.setImageBitmap(watermarkBitmap);
        }


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


        Button chooseColorButton = (Button)v.findViewById(R.id.btn_choose_pallette);
        chooseColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showColorDialog();
            }
        });




        return v;
    }

    private void showColorDialog(){
        DialogColor fragment = new DialogColor();
        fragment.setTargetFragment(this, REQUEST_WEIGHT);
        fragment.show(getFragmentManager(), fragment.getClass().getName());
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


    private void openImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        File folderToSave = new File(Environment.getExternalStorageDirectory() +
                File.separator + "WatermarkBack/");
        Uri uri = Uri.fromFile(folderToSave);
        photoPickerIntent.setDataAndType(uri, "image/*");
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
                        galleryBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                                selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageWatermark.setImageBitmap(galleryBitmap);
                    getPixelColor();
                }
                break;

            case REQUEST_WEIGHT:
                int color = imageReturnedIntent.getIntExtra(DialogColor.TAG_WEIGHT_SELECTED, -1);
                int pixelRed = Color.red(color);
                int pixelGreen = Color.green(color);
                int pixelBlue = Color.blue(color);
                Bitmap paletteBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                paletteBitmap.eraseColor(Color.argb(255,pixelRed,pixelGreen,pixelBlue));
                paletteImageView.setImageBitmap(paletteBitmap);
                break;
        }

    }



    class CutAsyncTask extends AsyncTask<Void, Void, Void>{

        private Bitmap src, dest;
        private int sensitivity = seekBar.getProgress();
        int pixel;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

            //containerRelativeLayout.setDrawingCacheEnabled(true);
            //containerRelativeLayout.buildDrawingCache();

            src = ((BitmapDrawable)imageWatermark.getDrawable()).getBitmap();
            dest = Bitmap.createBitmap(
                    src.getWidth(), src.getHeight(), src.getConfig());


            //Определение цвета фона автоматически
            //1.Получение цветов пивселей с разных сторон
            /*
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
*/


            Bitmap src = ((BitmapDrawable)paletteImageView.getDrawable()).getBitmap();
            int width = 1;
            int height = 1;
            pixel = src.getPixel(width,height);

            /*
            int pixel1, pixel2, pixel3,pixel4;
            pixel1=pixel2=pixel3=pixel4=pixel;
*/





            //2. Сравниваем цвета
            /*
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
*/




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