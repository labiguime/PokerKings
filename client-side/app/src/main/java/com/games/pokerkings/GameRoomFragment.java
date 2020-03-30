package com.games.pokerkings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.games.pokerkings.classes.Game;
import com.games.pokerkings.classes.ReadyImplementation;
import com.games.pokerkings.classes.User;
import com.games.pokerkings.utils.SocketManager;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.TreeMap;

public class GameRoomFragment extends Fragment {

    LinearLayout totalBetLayout;
    LinearLayout currentBetLayout;
    LinearLayout tableCardsLayout;
    LinearLayout[] layoutPlayer = new LinearLayout[4];
    LinearLayout gameButtonsLayout;
    TextView userNicknameText;
    ImageView[] userCard = new ImageView[2];
    ConstraintLayout userAvatar;
    User user;
    ImageView readyButton;
    Boolean hasPlayerJustJoinedTheRoom = false;
    Game gameVariables;
    String avatar;
    String name;
    String spot;
    String room;
    Socket mSocket;
    HashMap<String, User> roomUsers;
    public GameRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_room, container, false);

        // Initialize layouts
        totalBetLayout = view.findViewById(R.id.total_bet_layout);
        currentBetLayout = view.findViewById(R.id.current_bet_layout);
        tableCardsLayout = view.findViewById(R.id.table_cards_layout);

        layoutPlayer[0] = view.findViewById(R.id.layout_player_0);
        layoutPlayer[1] = view.findViewById(R.id.layout_player_1);
        layoutPlayer[2] = view.findViewById(R.id.layout_player_2);
        layoutPlayer[3] = view.findViewById(R.id.layout_player_3);

        gameButtonsLayout = view.findViewById(R.id.game_buttons_layout);

        userCard[0] = view.findViewById(R.id.user_card_1);
        userCard[1] = view.findViewById(R.id.user_card_2);

        userNicknameText = view.findViewById(R.id.user_nickname_text);
        userAvatar = view.findViewById(R.id.user_avatar);

        readyButton = view.findViewById(R.id.ready_button);

        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReadyButtonPressed();
            }
        });

        // Initialize variables
        user = new User();
        gameVariables = new Game();
        mSocket = SocketManager.getInstance();

        // Recover variables from previous fragment
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            name = bundle.getString("name");
            avatar = bundle.getString("avatar");
            spot = bundle.getString("spot");
            room = bundle.getString("room");
            hasPlayerJustJoinedTheRoom = true;
        }

        // Setup UI
        setupNotReadyUiForPlayer();
        roomUsers = new HashMap<>();
        JSONObject object = new JSONObject();
        try {
            object.put("room_id", room);
        } catch(JSONException e) {

        }
        mSocket.emit("room/getPlayers", object);

        mSocket.on("getPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                getPlayers(args);
            }
        });

        return view;
    }

    private void setupNotReadyUiForPlayer() {
        totalBetLayout.setVisibility(View.INVISIBLE);
        currentBetLayout.setVisibility(View.INVISIBLE);
        tableCardsLayout.setVisibility(View.INVISIBLE);
        gameButtonsLayout.setVisibility(View.INVISIBLE);
        layoutPlayer[1].setVisibility(View.INVISIBLE);
        layoutPlayer[2].setVisibility(View.INVISIBLE);
        layoutPlayer[3].setVisibility(View.INVISIBLE);
        userCard[0].setVisibility(View.INVISIBLE);
        userCard[1].setVisibility(View.INVISIBLE);
        readyButton.setVisibility(View.VISIBLE);

        // Set user name and avatar picture
        int resID = getResources().getIdentifier(avatar+ "_notfolded", "drawable", "com.games.pokerkings");
        userAvatar.setBackgroundResource(resID);
        userNicknameText.setText(name);
    }

    private void getPlayers(Object... args) {
        JSONObject data = (JSONObject) args[0];
        try {
            JSONArray array = data.getJSONArray("players");
            for(int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("name");
                String avatar = obj.getString("avatar");
                String roomId = obj.getString("room_id");
                String spotId = obj.getString("spot_id");
                String id = obj.getString("_id");
                User u = new User(name, avatar, id, roomId, spotId);
                roomUsers.put(spotId, u);
            }

        } catch (JSONException e) {

        }
        updateUsersUi();

    }

    private void updateUsersUi() {
        TreeMap<String, User> map = new TreeMap<>(roomUsers);

        if(map.size() == 1) return;

        for(TreeMap.Entry<String,User> entry : map.entrySet()) {
            String key = entry.getKey();
            Log.d("DEBUG", "Id: "+key);
        }
    }
    private void onReadyButtonPressed() {
        Integer readyUsers = gameVariables.getReadyUsers()+1;
        Integer playingUsers = gameVariables.getPlayingUsers()+1;

        readyButton.setVisibility(View.INVISIBLE);
        ReadyImplementation.addReadyPlayer("game-1", readyUsers);
        if(ReadyImplementation.isGameReadyToStart(readyUsers, playingUsers)) {
            startGame();
        }
    }

    private void startGame() {

    }

}
