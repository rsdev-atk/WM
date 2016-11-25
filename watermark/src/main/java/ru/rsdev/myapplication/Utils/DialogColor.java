package ru.rsdev.myapplication.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.larswerkman.holocolorpicker.ColorPicker;

import ru.rsdev.myapplication.R;

public class DialogColor extends DialogFragment {

    private ColorPicker mColorPicker;
    private int color;

    onSomeEventListener someEventListener;

    public interface onSomeEventListener {
        void someEvent(int s);

    }



    public static final String TAG_WEIGHT_SELECTED = "weight";
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_color, null);

        mColorPicker = (ColorPicker) view.findViewById(R.id.picker_color);
        mColorPicker.setOldCenterColor(Color.WHITE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        color = mColorPicker.getColor();
                        //хак
                        //if(color<0) color = (-1)*color;


                        someEventListener.someEvent(color);

                        //отправляем результат обратно
                        /*
                        Intent intent = new Intent();
                        intent.putExtra(TAG_WEIGHT_SELECTED, color);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        */
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            someEventListener = (onSomeEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }




}
