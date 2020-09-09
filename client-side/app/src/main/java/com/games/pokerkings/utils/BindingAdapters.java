package com.games.pokerkings.utils;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.BindingAdapter;

public class BindingAdapters {

    @BindingAdapter("variable")
    public static void setImage(ImageView imageView, Object variable) {
        if(variable != null) {
            imageView.setImageResource(imageView.getResources().getIdentifier(variable.toString(), "drawable", "com.games.pokerkings"));
        }
    }

    @BindingAdapter("text_attribute")
    public static void setText(TextView textView, Object text) {
        if(text != null) {
            textView.setText(text.toString());
        }
    }

    @BindingAdapter({"avatar_attribute", "avatar_type"})
    public static void setAvatar(ConstraintLayout imageView, Object avatar, Object type) {
        if(avatar != null && type != null) {
            imageView.setBackgroundResource(imageView.getResources().getIdentifier(avatar.toString()+type.toString(), "drawable", "com.games.pokerkings"));
        }
    }

}
