package com.games.pokerkings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GameRoomFragment extends Fragment {

    LinearLayout totalBetLayout;
    LinearLayout currentBetLayout;
    LinearLayout tableCardsLayout;
    LinearLayout[] layoutPlayer = new LinearLayout[4];
    LinearLayout gameButtonsLayout;
    EditText userNicknameText;
    ImageView[] userCard = new ImageView[2];
    ImageView userAvatar;

    public GameRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_room, container, false);

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

        setupNotReadyUiForPlayer();

        String nickname = "None";
        String avatarPicture = "avatar1";

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            nickname = bundle.getString("nickname");
            avatarPicture = bundle.getString("avatar");
        }

        int resID = getResources().getIdentifier(avatarPicture+ "_notfolded", "drawable", "com.example.lepti.pokerapp");
        userAvatar.setBackgroundResource(resID);
        userNicknameText.setText(nickname);

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
    }

}
