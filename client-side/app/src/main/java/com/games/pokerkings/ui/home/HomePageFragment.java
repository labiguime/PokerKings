package com.games.pokerkings.ui.home;

import com.games.pokerkings.databinding.FragmentHomePageBinding;
import com.games.pokerkings.models.User;
import com.games.pokerkings.ui.game.GameRoomFragment;
import com.games.pokerkings.R;
import com.games.pokerkings.utils.SocketManager;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class HomePageFragment extends Fragment {

    EditText nicknameTextBox;
    ImageView joinGameButton;
    //ImageView changeAvatarButton;
    ImageView homeAvatarPicture;

    Integer avatarId = 0;

    Socket mSocket;
    private HomePageViewModel homePageViewModel;

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        FragmentHomePageBinding binding = FragmentHomePageBinding.inflate(inflater, container, false);
        homePageViewModel = new ViewModelProvider(this, new HomePageViewModelFactory()).get(HomePageViewModel.class);

        binding.setHomePageViewModel(homePageViewModel);

        mSocket = SocketManager.getInstance();

        // Load the views
        nicknameTextBox = view.findViewById(R.id.nickname_text_box);
        //changeAvatarButton = view.findViewById(R.id.change_avatar_button);
        homeAvatarPicture = view.findViewById(R.id.home_avatar_picture);
        joinGameButton = view.findViewById(R.id.join_game_button);

        // Setup the listeners
        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onJoinGameButtonPressed();
                //homePageViewModel.joinGame();
            }
        });

        /*changeAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePageViewModel.changeAvatar();
            }
        });*/

        /*nicknameTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                homePageViewModel.usernameTextChanged();
            }
        });*/

        homePageViewModel.getAvatar().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                int resID = getResources().getIdentifier(s, "drawable", "com.games.pokerkings");
                homeAvatarPicture.setImageResource(resID);
            }
        });

        return binding.getRoot();
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
            //changeAvatarButton.setClickable(false);
            nicknameTextBox.setEnabled(false);

            JSONObject joinObject = new JSONObject();
            try {
                joinObject.put("room", "Room#1");
                joinObject.put("name", nickname);
                joinObject.put("avatar", "avatar" + (avatarId+1));
            } catch( JSONException e ) {

            }
            mSocket.emit("room/POST:join", joinObject);
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
        String room;
        try {
            success = data.getBoolean("success");
            message = data.getString("message");
            spot = data.getString("spot");
            room = data.getString("room");
        } catch (JSONException e) {
            return;
        }

        if(!success) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            joinGameButton.setVisibility(View.VISIBLE);
            //changeAvatarButton.setClickable(true);
            nicknameTextBox.setEnabled(true);
            joinGameButton.setClickable(true);
        } else {
            GameRoomFragment fragment = new GameRoomFragment();
            Bundle bundle = new Bundle();

            String name = nicknameTextBox.getText().toString();
            String avatarFileName = "avatar"+(avatarId+1);

            // Put variables into bundle to pass them to the next fragment
            bundle.putString("avatar", avatarFileName);
            bundle.putString("name", name);
            bundle.putString("spot", spot);
            bundle.putString("room", room);
            fragment.setArguments(bundle);

            // Move to the next fragment
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_placeholder, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

}