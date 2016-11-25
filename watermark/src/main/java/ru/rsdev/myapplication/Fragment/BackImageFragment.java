package ru.rsdev.myapplication.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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


public class BackImageFragment extends Fragment {

    Button openBackImage, importBackImage;
    ImageView backImage;
    private static final int CHOOSE_BASE_PHOTO_OPEN = 1001;
    private static final int CHOOSE_BASE_PHOTO_IMPORT = 1002;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_back_image, null);

        openBackImage = (Button)v.findViewById(R.id.btn_open_back_image);
        backImage = (ImageView) v.findViewById(R.id.img_back_image);
        openBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageBack();
            }
        });

        importBackImage = (Button)v.findViewById(R.id.btn_import_back_image);
        importBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagetoImportBack();
            }
        });


        return v;
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
                        galleryBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                                selectedImageUri);
                    }  catch (IOException e) {
                        e.printStackTrace();
                    }
                    backImage.setImageBitmap(galleryBitmap);
                    WatermarkSettings.getInstance().setBackImageBitmap(galleryBitmap);
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
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                                selectedImageUri);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                    }catch (Exception e) // здесь необходим блок отслеживания реальных ошибок и исключений, общий Exception приведен в качестве примера
                    {
                        // return e.getMessage();
                    }

                    Toast.makeText(getActivity(), "Файл успешно импортирован", Toast.LENGTH_SHORT).show();
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


}
