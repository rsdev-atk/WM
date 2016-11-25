package ru.rsdev.myapplication.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.isseiaoki.simplecropview.CropImageView;

import ru.rsdev.myapplication.R;
import ru.rsdev.myapplication.Utils.WatermarkSettings;

public class CropFragment extends Fragment {

    private ModificationWatermarkFragment modificationWatermarkFragment;
    private FragmentTransaction fTrans;



    private com.isseiaoki.simplecropview.CropImageView mCropView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crop, null);

        mCropView = (CropImageView) v.findViewById(R.id.cropImageView);

        Bitmap bitmap = WatermarkSettings.getInstance().getWatermarkBitmap();

        mCropView.setImageBitmap(bitmap);

        Button saveButtonCrop = (Button)v.findViewById(R.id.btn_save);
        saveButtonCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap cropBitmap = mCropView.getCroppedBitmap();
                WatermarkSettings.getInstance().setWatermarkBitmap(cropBitmap);


                modificationWatermarkFragment = new ModificationWatermarkFragment();
                fTrans = getActivity().getSupportFragmentManager().beginTransaction();
     //           fTrans.replace(R.id.fragment_container, modificationWatermarkFragment);
                fTrans.commit();


            }
        });


        return v;
    }
}
