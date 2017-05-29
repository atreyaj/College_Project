package com.example.soumyadeeppal.collegeproject;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Soumyadeep Pal on 13-05-2017.
 */

public class PackageView extends LinearLayout implements Checkable {
        View v;
        TextView tv;
        ImageView p1;
        Button b;

        CheckBox testCheckBox;

        public PackageView(Context context)
        {
            super(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            v = inflater.inflate(R.layout.user_element, this, true);
            tv = (TextView) v.findViewById(R.id.username);
            p1 = (ImageView) v.findViewById(R.id.photo);
            b = (Button) v.findViewById(R.id.invite);

            testCheckBox=(CheckBox)findViewById(R.id.checkbox);


            // I don't have checkbox in my layout, but if I had:
            // testCheckBox = (CheckBox) v.findViewById(R.id.checkBoxId);
        }

    public void setPackage(Package pack)
    {
        // my custom method where I set package id, date, and time
    }

    private Boolean checked = false;

    @Override
    public boolean isChecked()
    {
        return checked;
        // if I had checkbox in my layout I could
        // return testCheckBox.checked();
    }

    @Override
    public void setChecked(boolean checked)
    {
        this.checked = checked;

        // since I choose not to have check box in my layout, I change background color
        // according to checked state
        if(isChecked()) {
            getRootView().setBackgroundColor(Color.BLUE);
            testCheckBox.setChecked(true);
        }
        else {
            getRootView().setBackgroundColor(Color.WHITE);
        }
        // if I had checkbox in my layout I could
        // testCheckBox.setChecked(checked);
    }

    @Override
    public void toggle()
    {
        checked = !checked;
        // if I had checkbox in my layout I could
        // return testCheckBox.toggle();
    }

}
