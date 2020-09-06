package com.games.pokerkings.ui.game;

import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.games.pokerkings.R;
import com.games.pokerkings.models.Game;
import com.games.pokerkings.models.User;
import com.games.pokerkings.utils.SocketManager;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

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
    LinearLayout readyBox;

    TextView totalBetText;
    TextView currentBetText;
    TextView raiseText;

    TextView[] playerNameText = new TextView[3];
    ConstraintLayout[] playerAvatarImage = new ConstraintLayout[3];
    ImageView[][] playerCardImage = new ImageView[4][2];
    LinearLayout gameButtonsLayout;
    TextView userNicknameText;
    TextView readyMessage;
    ImageView[] userCard = new ImageView[2];
    ConstraintLayout userAvatar;
    ImageView readyButton;
    Boolean hasPlayerJustJoinedTheRoom = false;
    Boolean isPlayerReady = false;
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
        readyBox = view.findViewById(R.id.ready_box);

        layoutPlayer[0] = view.findViewById(R.id.layout_player_0);
        layoutPlayer[1] = view.findViewById(R.id.layout_player_1);
        layoutPlayer[2] = view.findViewById(R.id.layout_player_2);
        layoutPlayer[3] = view.findViewById(R.id.layout_player_3);

        playerNameText[0] = view.findViewById(R.id.top_player_nickname_text);
        playerNameText[1] = view.findViewById(R.id.left_player_nickname_text);
        playerNameText[2] = view.findViewById(R.id.right_player_nickname_text);

        playerAvatarImage[0] = view.findViewById(R.id.top_player_avatar);
        playerAvatarImage[1] = view.findViewById(R.id.left_player_avatar);
        playerAvatarImage[2] = view.findViewById(R.id.right_player_avatar);

        playerCardImage[0][0] = view.findViewById(R.id.top_player_card_1);
        playerCardImage[0][1] = view.findViewById(R.id.top_player_card_2);

        playerCardImage[1][0] = view.findViewById(R.id.left_player_card_1);
        playerCardImage[1][1] = view.findViewById(R.id.left_player_card_2);

        playerCardImage[2][0] = view.findViewById(R.id.right_player_card_1);
        playerCardImage[2][1] = view.findViewById(R.id.right_player_card_2);

        gameButtonsLayout = view.findViewById(R.id.game_buttons_layout);

        userCard[0] = view.findViewById(R.id.user_card_1);
        userCard[1] = view.findViewById(R.id.user_card_2);

        userNicknameText = view.findViewById(R.id.user_nickname_text);
        userAvatar = view.findViewById(R.id.user_avatar);

        readyButton = view.findViewById(R.id.ready_button);
        readyMessage = view.findViewById(R.id.readyMessageTextView);
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReadyButtonPressed();
            }
        });

        totalBetText = view.findViewById(R.id.total_bet_text);
        currentBetText = view.findViewById(R.id.current_bet_text);
        raiseText = view.findViewById(R.id.raise_text);

        // Initialize variables
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
        mSocket.emit("room/GET:players", object);

        mSocket.on("getPlayers", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                getPlayers(args);
            }
        });

        mSocket.on("getReady", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                getReady(args);
            }
        });

        mSocket.on("getCompleteRoomData", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                initializeRoomData(args);
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


    private void setupPlayableUiForPlayer() {

        // Remove the ready button and its text from the constrained layout
        readyBox.setVisibility(View.INVISIBLE);

        // Setup the right side panel
        raiseText.setText("0");
        totalBetText.setText("$0");
        currentBetText.setText("$0");
        currentBetText.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark_default));
        totalBetLayout.setVisibility(View.VISIBLE);
        currentBetLayout.setVisibility(View.VISIBLE);

        Animation fade_in = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);

        userCard[0].setVisibility(View.VISIBLE);
        userCard[1].setVisibility(View.VISIBLE);

        userCard[0].startAnimation(fade_in);
        userCard[1].startAnimation(fade_in);

        for(int i = 0; i < 3; i++) {
            if(layoutPlayer[i+1].getVisibility() == View.VISIBLE) {
                playerCardImage[i][0].setVisibility(View.VISIBLE);
                playerCardImage[i][1].setVisibility(View.VISIBLE);

                playerCardImage[i][0].startAnimation(fade_in);
                playerCardImage[i][1].startAnimation(fade_in);
            }
        }
        //animateUserCard();
        //displayAllButtons();
    }



    private void getPlayers(Object... args) {
        JSONObject data = (JSONObject) args[0];
        try {
            JSONArray array = data.getJSONArray("players");
            for(int i = 0; i < array.length(); i++) {
                Log.d("DEBUG", "Starting iteration...");
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("name");
                String avatar = obj.getString("avatar");
                String roomId = obj.getString("room_id");
                String spotId = obj.getString("spot_id");
                Boolean ready = obj.getBoolean("ready");
                String id = obj.getString("_id");
                Log.d("DEBUG", "name is: "+ name);
                User u = new User(name, avatar, id, roomId, spotId, ready);
                roomUsers.put(spotId, u);
            }

        } catch (JSONException e) {

        }
        updateUsersUi();

    }
    private void initializeRoomData(final Object... args) {
        Log.d("DEBUG", "This room is initialized");
        return;

    }
    private void getReady(final Object... args) {
        if(!isPlayerReady) {
            Log.d("DEBUG", "Player not ready");
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject data = (JSONObject) args[0];
                try {
                    Boolean success = data.getBoolean("success");
                    Boolean gameIsStarting = data.getBoolean("gameIsStarting");
                    String message = data.getString("message");
                    Log.d("DEBUG", "Message is: "+ message);
                    if(!success) {
                        // restore ready button
                        return;
                    }
                    readyMessage.setText(message);
                    if(gameIsStarting) {
                        setupPlayableUiForPlayer();
                        return;
                    }
                } catch (JSONException e) {

                }
            }
        });
    }

    private void setupNotReadyUiFor(String recipient, final String playerName, final String playerAvatar) {
        final int index;
        if (recipient.equals("top")) {
            index = 0;
        } else if (recipient.equals("left")) {
            index = 1;
        } else {
            index = 2;
        }
        Log.d("DEBUG", "Working on index: "+ index);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layoutPlayer[index + 1].setVisibility(View.VISIBLE);
                playerCardImage[index][0].setVisibility(View.INVISIBLE);
                playerCardImage[index][1].setVisibility(View.INVISIBLE);
                Log.d("DEBUG", "Set visibility!");
                // Set user name and avatar picture
                int resID = getResources().getIdentifier(playerAvatar + "_notfolded", "drawable", "com.games.pokerkings");
                Log.d("DEBUG", "Resource found!");
                playerAvatarImage[index].setBackgroundResource(resID);
                Log.d("DEBUG", "Background set!");
                playerNameText[index].setText(playerName);
            }
        });
    }

    private String getLayoutForId(int playerIndex, int id, int size) {
        if(size == 2) return "top";
        int newId = (id+(size-playerIndex))%size;
        if(newId == 1) {
            return "left";
        } else if(newId == 2) {
            return "top";
        } else {
            return "right";
        }
    }
    private void updateUsersUi() {
        Log.d("DEBUG", "Taking care of the UI...");
        TreeMap<String, User> map = new TreeMap<>(roomUsers);
        int size = map.size();
        if(size == 1) return;
        int index = -1,
            playerIndex = -1;

        for(TreeMap.Entry<String,User> entry : map.entrySet()) {
            playerIndex++;
            Log.d("DEBUG", "Retrieving id...");
            if (entry.getKey().equals(spot)) {
                Log.d("DEBUG", "ID found!");
                break;
            }
        }

        Log.d("DEBUG", "Setting it up...");
        for(TreeMap.Entry<String,User> entry : map.entrySet()) {
            index++;
            Log.d("DEBUG", "Item getting worked!");
            if (entry.getKey().equals(spot)) {
                Log.d("DEBUG", "Main has gone away!");
                continue;
            }

            User u = entry.getValue();
            Log.d("DEBUG", "Working on name: "+u.getName()+" with avatar name: "+u.getAvatar());
            String position = getLayoutForId(playerIndex, index, size);
            Log.d("DEBUG", "Position recovered!");
            setupNotReadyUiFor(position, u.getName(), u.getAvatar());
            Log.d("DEBUG", "Finished working on name: "+u.getName());
        }
    }
    private void onReadyButtonPressed() {
        Integer readyUsers = gameVariables.getReadyUsers()+1;
        Integer playingUsers = gameVariables.getPlayingUsers()+1;
        isPlayerReady = true;
        readyButton.setVisibility(View.INVISIBLE);
        JSONObject object = new JSONObject();
        try {
            object.put("room_id", room);
            object.put("name", name);
        } catch(JSONException e) {
        }
        mSocket.emit("room/POST:ready", object);
        /*ReadyImplementation.addReadyPlayer("game-1", readyUsers);
        if(ReadyImplementation.isGameReadyToStart(readyUsers, playingUsers)) {
            startGame();
        }*/
    }

    private void startGame() {

    }

}