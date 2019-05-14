package com.games.pokerkings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.games.pokerkings.classes.Game;
import com.games.pokerkings.classes.ReadyImplementation;
import com.games.pokerkings.classes.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    Game gameVariables;
    FirebaseDatabase database;
    ValueEventListener gameVariablesListener;
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
        database = FirebaseDatabase.getInstance();
        user = new User();
        gameVariables = new Game();

        // Recover variables from previous fragment
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user.setNickname(bundle.getString("nickname"));
            user.setAvatar(bundle.getString("avatar"));
        }

        // Setup listeners
        gameVariablesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                onGameVariablesChanged(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        database.getReference("game-1/variables").addValueEventListener(gameVariablesListener);

        // Setup UI
        setupNotReadyUiForPlayer();

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
        int resID = getResources().getIdentifier(user.getAvatar()+ "_notfolded", "drawable", "com.games.pokerkings");
        userAvatar.setBackgroundResource(resID);
        userNicknameText.setText(user.getNickname());
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

    private void onGameVariablesChanged(@NonNull DataSnapshot dataSnapshot) {
        gameVariables = dataSnapshot.getValue(Game.class);
    }

    private void startGame() {

    }

}
