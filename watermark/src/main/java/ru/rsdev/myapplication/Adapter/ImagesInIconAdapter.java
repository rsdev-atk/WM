package ru.rsdev.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import ru.rsdev.myapplication.Activity.CropActivity;
import ru.rsdev.myapplication.Activity.ModificationImageActivity;
import ru.rsdev.myapplication.Model.Images;
import ru.rsdev.myapplication.R;

public class ImagesInIconAdapter extends BaseAdapter {

    Context context;
    LayoutInflater lInflater;
    ArrayList<Images> objects;

    public ImagesInIconAdapter(Context context, ArrayList<Images> images) {
        this.context = context;
        this.objects = images;
        lInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_images_in_icon, parent, false);
        }

        final Images i = getImages(position);

        ((TextView) view.findViewById(R.id.id_image)).setText(String.valueOf(i.id));
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                    Uri.parse(i.uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ((ImageView) view.findViewById(R.id.img_images)).setImageBitmap(bitmap);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.cbBox_image);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getImages((Integer) compoundButton.getTag()).chek = b;
            }
        });

        checkBox.setTag(position);
        checkBox.setChecked(i.chek);

        final Button cropButton = (Button)view.findViewById(R.id.btn_images_crop);
        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Вызов Crop изображения
                Intent intent = new Intent(context, CropActivity.class);
                //Передаем ссылку на изображение
                String str = String.valueOf(objects.get(position).uri);
                int id_image = objects.get(position).id;
                intent.putExtra("uri", str);
                intent.putExtra("id", id_image);
                context.startActivity(intent);

            }
        });

        final ImageView imageView = (ImageView)view.findViewById(R.id.img_images);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ModificationImageActivity.class);
                String str = String.valueOf(objects.get(position).uri);
                int id_image = objects.get(position).id;
                intent.putExtra("uri", str);
                intent.putExtra("id", id_image);
                context.startActivity(intent);
            }
        });



        return view;
    }

    Images getImages(int position) {
        return ((Images) getItem(position));
    }

    public ArrayList<Images> getChek() {
        ArrayList<Images> box = new ArrayList<>();
        for (Images images : objects) {
            if (images.chek)
                box.add(images);
        }
        return box;
    }


}
