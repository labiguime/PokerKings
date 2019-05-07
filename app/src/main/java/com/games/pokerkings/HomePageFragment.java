package com.games.pokerkings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

public class HomePageFragment extends Fragment {

    EditText nicknameTextBox;
    ImageView joinGameButton;
    ImageView changeAvatarButton;
    ImageView homeAvatarPicture;

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        nicknameTextBox = view.findViewById(R.id.nickname_text_box);
        changeAvatarButton = view.findViewById(R.id.change_avatar_button);
        homeAvatarPicture = view.findViewById(R.id.home_avatar_picture);
        joinGameButton = view.findViewById(R.id.join_game_button);

        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameRoomFragment fragment = new GameRoomFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_placeholder, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
