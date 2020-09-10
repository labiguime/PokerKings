package com.games.pokerkings.utils;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class ListManipulation {

    public static <T> void append(MutableLiveData<List<T>> original, T s) {
        List<T> copy = original.getValue();
        if(copy == null) {
            copy = new ArrayList<>();
        }
        copy.add(s);
        original.setValue(copy);
    }

    public static <T> void set(MutableLiveData<List<T>> original, Integer index, T s, Boolean isRemote) {
        List<T> copy = original.getValue();
        assert copy != null;
        if(copy.get(index) != null) {
            copy.set(index, s);
            if(isRemote) {
                original.postValue(copy);
            } else {
                original.setValue(copy);
            }

        }
    }
}