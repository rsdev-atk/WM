package ru.rsdev.myapplication.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import ru.rsdev.myapplication.R;

public class DialogIconName extends DialogFragment implements DialogInterface.OnClickListener{

    private String iconName;
    View view;
    EditText editText;
    String name;

    public interface onSomeEventListener {
        public void someEvent(String s);
    }

    onSomeEventListener someEventListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_icon_name, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        editText = (EditText) view.findViewById(R.id.icon_name);

        try{


        name = getArguments().getString("name",null);
        if(name != null) {
            editText.setText(name);
        }
    }catch (Exception e){

        }

        return(builder.setTitle("Введите название").setView(view)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create());

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

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

        //editText = (EditText) getDialog().findViewById(R.id.icon_name);
        iconName = editText.getText().toString();
        someEventListener.someEvent(iconName);



    }




}
