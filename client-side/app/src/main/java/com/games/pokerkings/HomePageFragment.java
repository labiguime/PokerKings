package com.games.pokerkings;

import com.games.pokerkings.utils.SocketManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
            changeAvatarButton.setClickable(false);
            nicknameTextBox.setEnabled(false);

            JSONObject joinObject = new JSONObject();
            try {
                joinObject.put("room", "Room#1");
                joinObject.put("name", nickname);
                joinObject.put("avatar", "avatar" + (avatarId+1));
            } catch( JSONException e ) {

            }

            mSocket.emit("room/join", joinObject);
            mSocket.on("joinRoom", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    joinRoom(args);
                }
            });
        }

    }

    private void onChangeAvatarButtonPressed() {
        avatarId = ((avatarId+1)%6);
        String avatarFileName = "avatar" + (avatarId+1);
        int resID = getResources().getIdentifier(avatarFileName, "drawable", "com.games.pokerkings");
        homeAvatarPicture.setImageResource(resID);
    }

    private void joinRoom(Object... args) {
        JSONObject data = (JSONObject) args[0];
        Boolean success;
        String message;
        String spot;
        try {
            success = data.getBoolean("success");
            message = data.getString("message");
            spot = data.getString("spot");
        } catch (JSONException e) {
            return;
        }

        if(!success) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            joinGameButton.setVisibility(View.VISIBLE);
            changeAvatarButton.setClickable(true);
            nicknameTextBox.setEnabled(true);
            joinGameButton.setClickable(true);
        } else {
            GameRoomFragment fragment = new GameRoomFragment();
            Bundle bundle = new Bundle();

            String nickname = nicknameTextBox.getText().toString();
            String avatarFileName = Integer.toString(avatarId+1);

            // Put variables into bundle to pass them to the next fragment
            bundle.putString("avatar", avatarFileName);
            bundle.putString("nickname", nickname);
            bundle.putString("spot", spot);
            fragment.setArguments(bundle);

            // Move to the next fragment
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_placeholder, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

}
