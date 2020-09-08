package com.games.pokerkings.ui.home;

import com.games.pokerkings.databinding.FragmentHomePageBinding;
import com.games.pokerkings.models.User;
import com.games.pokerkings.ui.game.GameRoomFragment;
import com.games.pokerkings.R;
import com.games.pokerkings.utils.Result;
import com.games.pokerkings.utils.SocketManager;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    private HomePageViewModel homePageViewModel;
    Integer avatarId = 0;

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        homePageViewModel = new ViewModelProvider(this, new HomePageViewModelFactory()).get(HomePageViewModel.class);

        FragmentHomePageBinding binding = FragmentHomePageBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(HomePageFragment.this);
        binding.setHomePageViewModel(homePageViewModel);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        observeName();
        observeOnJoinGame();
    }

    public void observeName() {
        homePageViewModel.getName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                homePageViewModel.setName(s);
            }
        });
    }

    public void observeOnJoinGame() {
        homePageViewModel.getOnJoinGame().observe(getViewLifecycleOwner(), new Observer<Result<User>>() {
            @Override
            public void onChanged(Result<User> userResult) {
                if(userResult instanceof Result.Error) {
                    homePageViewModel.setHasPlayerPressedJoin();
                    Log.d("TESTT", "Progress");
                    showErrorMessage(getString(((Result.Error) userResult).getError()));
                } else if(userResult instanceof Result.Success) {
                    homePageViewModel.setHasPlayerPressedJoin();
                    launchGameRoomFragment(((Result.Success<User>)userResult).getData());
                }
            }
        });
    }

    public void showErrorMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void showProgressBar() {
        homePageViewModel.setProgressBar
    }
    private void onJoinGameButtonPressed() {
        String nickname = nicknameTextBox.getText().toString();

        if(nickname.isEmpty()) {
            Toast.makeText(getActivity(),"You must choose a nickname!",Toast.LENGTH_SHORT).show();
        }
        else if(nickname.length() > 15) {
            Toast.makeText(getActivity(),"Your nickname must contain less than 15 characters!", Toast.LENGTH_SHORT).show();
        }
        else {
            //joinGameButton.setClickable(false);
            //joinGameButton.setVisibility(View.GONE);
            //changeAvatarButton.setClickable(false);

            JSONObject joinObject = new JSONObject();
            try {
                joinObject.put("room", "Room#1");
                joinObject.put("name", nickname);
                joinObject.put("avatar", "avatar" + (avatarId+1));
            } catch( JSONException e ) {

            }
        }
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
            //joinGameButton.setVisibility(View.VISIBLE);
            //changeAvatarButton.setClickable(true);
            nicknameTextBox.setEnabled(true);
            //joinGameButton.setClickable(true);
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
