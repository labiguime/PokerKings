package com.games.pokerkings.utils;

import android.widget.EditText;
import android.widget.ImageView;
import androidx.databinding.BindingAdapter;

public class BindingAdapters {

    @BindingAdapter("variable")
    public static void setImage(ImageView imageView, Object variable) {
        if(variable != null) {
            imageView.setImageResource(imageView.getResources().getIdentifier(variable.toString(), "drawable", "com.games.pokerkings"));
        }
    }

}