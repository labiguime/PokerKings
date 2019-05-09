package com.games.pokerkings.classes;

import com.google.firebase.database.FirebaseDatabase;

public class ReadyImplementation {

    public static void addReadyPlayer(String roomName, Integer readyUsers) {
        FirebaseDatabase.getInstance().getReference(roomName+"/variables").child("readyUsers").setValue(readyUsers); // no error or exception checks
        return;
    }

    public static boolean isGameReadyToStart(Integer readyUsers, Integer playingUsers) {
        if(readyUsers == playingUsers && readyUsers > 1) return true;
        return false;
    }
}
