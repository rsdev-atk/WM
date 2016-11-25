package ru.rsdev.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import ru.rsdev.myapplication.R;
import ru.rsdev.myapplication.Model.Icon;

public class IconAdapter extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    ArrayList<Icon> objects;

    public IconAdapter(Context context, ArrayList<Icon> icons) {
        this.context = context;
        this.objects = icons;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_icon, parent, false);
        }

        Icon p = getIcon(position);

        ((TextView) view.findViewById(R.id.id_icon)).setText(String.valueOf(p.id));
        ((TextView) view.findViewById(R.id.name_icon)).setText(p.name);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.cbBox_icon);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getIcon((Integer) compoundButton.getTag()).chek = b;
            }
        });

        checkBox.setTag(position);
        checkBox.setChecked(p.chek);


        return view;
    }

    Icon getIcon(int position) {
        return ((Icon) getItem(position));
    }

    public ArrayList<Icon> getChek() {
        ArrayList<Icon> box = new ArrayList<Icon>();
        for (Icon icon : objects) {
            if (icon.chek)
                box.add(icon);
        }
        return box;
    }

}