package com.games.pokerkings.ui.home;

import com.games.pokerkings.databinding.FragmentHomePageBinding;
import com.games.pokerkings.data.models.User;
import com.games.pokerkings.ui.game.GameRoomFragment;
import com.games.pokerkings.R;
import com.games.pokerkings.utils.Result;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class HomePageFragment extends Fragment {

    private HomePageViewModel homePageViewModel;

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

    private void launchGameRoomFragment(User u) {
        GameRoomFragment fragment = new GameRoomFragment();
        Bundle bundle = new Bundle();

        // Put User class into bundle to pass it to the next fragment
        bundle.putSerializable("user", u);
        fragment.setArguments(bundle);

        // Move to the next fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
