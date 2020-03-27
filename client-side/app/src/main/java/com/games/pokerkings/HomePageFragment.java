package com.games.pokerkings;

import com.games.pokerkings.utils.SocketManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class HomePageFragment extends Fragment {

    EditText nicknameTextBox;
    ImageView joinGameButton;
    ImageView changeAvatarButton;
    ImageView homeAvatarPicture;

    Integer avatarId = 0;
    Map<String, Boolean> freeSpots = new HashMap<>();

    Socket mSocket;

    FirebaseDatabase database;
    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        database = FirebaseDatabase.getInstance();
        mSocket = SocketManager.getInstance();

        // Setup freeSpots
        DatabaseReference freeSpotsReference = database.getReference("game-1/free-spots");
        freeSpots.put("0", true);
        freeSpots.put("1", true);
        freeSpots.put("2", true);
        freeSpots.put("3", true);
        freeSpotsReference.setValue(freeSpots);

        freeSpots.clear();

        // Reset variables (for debugging only)
        FirebaseDatabase.getInstance().getReference("game-1/variables").child("readyUsers").setValue(0);
        FirebaseDatabase.getInstance().getReference("game-1/variables").child("playingUsers").setValue(0);

        // Load the views
        nicknameTextBox = view.findViewById(R.id.nickname_text_box);
        changeAvatarButton = view.findViewById(R.id.change_avatar_button);
        homeAvatarPicture = view.findViewById(R.id.home_avatar_picture);
        joinGameButton = view.findViewById(R.id.join_game_button);

        // Setup the listeners
        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onJoinGameButtonPressed();
            }
        });

        changeAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeAvatarButtonPressed();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void onJoinGameButtonPressed() {
        String nickname = nicknameTextBox.getText().toString();

        if(nickname.isEmpty()) {
            Toast.makeText(getActivity(),"You must choose a nickname!",Toast.LENGTH_SHORT).show();
        }
        else if(nickname.length() > 15) {
            Toast.makeText(getActivity(),"Your nickname must contain less than 15 characters!",Toast.LENGTH_SHORT).show();
        }
        else {
            joinGameButton.setClickable(false);
            joinGameButton.setVisibility(View.GONE);

            JSONObject joinObject = new JSONObject();
            try {
                joinObject.put("room", "Room#1");
                joinObject.put("name", nickname);
                joinObject.put("avatar", "avatar" + (avatarId+1));
            } catch( JSONException e ) {

            }

            mSocket.emit("room/join", joinObject);

            /*DatabaseReference myRef = database.getReference("game-1/free-spots");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    freeSpots.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        freeSpots.put(snapshot.getKey(), snapshot.getValue(Boolean.class));
                    }
                    joinGame(getFreeSpot());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    joinGame(-1);
                }
            });*/
        }

    }

    private void onChangeAvatarButtonPressed() {
        avatarId = ((avatarId+1)%6);
        String avatarFileName = "avatar" + (avatarId+1);
        int resID = getResources().getIdentifier(avatarFileName, "drawable", "com.games.pokerkings");
        homeAvatarPicture.setImageResource(resID);
    }

    private void joinGame(final int gameSpot) {
        if(gameSpot == -1) {
            Toast.makeText(getActivity(), "The room is full!", Toast.LENGTH_SHORT).show();
            joinGameButton.setClickable(true);
            return;
        }
        DatabaseReference reference = database.getReference("game-1/free-spots");
        reference.setValue(freeSpots);

        GameRoomFragment fragment = new GameRoomFragment();

        // Variables to pass

        String nickname = nicknameTextBox.getText().toString();
        Integer spot = gameSpot;

        // Put variables into bundle to pass them to the next fragment
        Bundle bundle = new Bundle();
        //bundle.putString("avatar", avatarFileName);
        bundle.putString("nickname", nickname);
        bundle.putInt("spot", spot);
        fragment.setArguments(bundle);

        // Move to the next fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private int getFreeSpot() {
        for (Map.Entry<String, Boolean> entry : freeSpots.entrySet()) {
            if(entry.getValue()) {
                entry.setValue(false);
                return Integer.parseInt(entry.getKey());
            }
        }
        return -1;
    }

}
