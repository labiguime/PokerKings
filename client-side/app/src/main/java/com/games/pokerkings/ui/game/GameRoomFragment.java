package com.games.pokerkings.ui.game;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.games.pokerkings.R;

import com.games.pokerkings.data.models.*;
import com.games.pokerkings.databinding.FragmentGameRoomBinding;
import com.games.pokerkings.utils.CardManipulation;
import com.games.pokerkings.utils.Constants;
import com.games.pokerkings.utils.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameRoomFragment extends Fragment {

    private GameRoomViewModel gameRoomViewModel;

    LinearLayout[] layoutPlayer = new LinearLayout[4];
    ImageView[][] playerCardImage = new ImageView[4][2];
    ImageView[] tableCardImage = new ImageView[5];
    ImageView[] userCard = new ImageView[2];


    public GameRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        gameRoomViewModel = new ViewModelProvider(this).get(GameRoomViewModel.class);

        FragmentGameRoomBinding binding = FragmentGameRoomBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(GameRoomFragment.this);
        binding.setGameRoomViewModel(gameRoomViewModel);

        layoutPlayer[0] = binding.getRoot().findViewById(R.id.layout_player_0);
        layoutPlayer[1] = binding.getRoot().findViewById(R.id.layout_player_1);
        layoutPlayer[2] = binding.getRoot().findViewById(R.id.layout_player_2);
        layoutPlayer[3] = binding.getRoot().findViewById(R.id.layout_player_3);

        playerCardImage[0][0] = binding.getRoot().findViewById(R.id.top_player_card_1);
        playerCardImage[0][1] = binding.getRoot().findViewById(R.id.top_player_card_2);

        playerCardImage[1][0] = binding.getRoot().findViewById(R.id.left_player_card_1);
        playerCardImage[1][1] = binding.getRoot().findViewById(R.id.left_player_card_2);

        playerCardImage[2][0] = binding.getRoot().findViewById(R.id.right_player_card_1);
        playerCardImage[2][1] = binding.getRoot().findViewById(R.id.right_player_card_2);

        userCard[0] = binding.getRoot().findViewById(R.id.user_card_1);
        userCard[1] = binding.getRoot().findViewById(R.id.user_card_2);

        tableCardImage[0] = binding.getRoot().findViewById(R.id.table_card_1);
        tableCardImage[1] = binding.getRoot().findViewById(R.id.table_card_2);
        tableCardImage[2] = binding.getRoot().findViewById(R.id.table_card_3);
        tableCardImage[3] = binding.getRoot().findViewById(R.id.table_card_4);
        tableCardImage[4] = binding.getRoot().findViewById(R.id.table_card_5);

        // Recover variables from previous fragment
        Bundle bundle = this.getArguments();

        // TODO: Implement steps to take if bundle transfer fails
        if(bundle == null) {
            return null;
        }
        gameRoomViewModel.setUserInterfaceForUser((User)bundle.getSerializable("user"));
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gameRoomViewModel.onReceiveRoomState().observe(getViewLifecycleOwner(), roomState -> {
            if(roomState.getError() != null) {
                showErrorMessage(roomState.getError());
            } else {
                if(roomState.getHasRoundEnded()) {
                    Animation triggerChangesAfterFromTop = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                    triggerChangesAfterFromTop.setStartOffset(2000);
                    triggerChangesAfterFromTop.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            tableCardImage[roomState.getGameStage()+2].setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            List<Integer> tableCards = Arrays.asList(roomState.getTableCard());
                            List<ImageView> imageList = Arrays.asList(tableCardImage[roomState.getGameStage()+2]);
                            CardManipulation.revealCards(getActivity(), getResources(), imageList, tableCards, 0);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    tableCardImage[roomState.getGameStage()+2].startAnimation(triggerChangesAfterFromTop);
                }
            }
        });

        gameRoomViewModel.onReceiveRoomResults().observe(getViewLifecycleOwner(), roomResults -> {
            if(roomResults.getError() != null) {
                showErrorMessage(roomResults.getError());
            } else {
                if(roomResults.getHasRoundEnded() == true && roomResults.getGameStage() == 3) { // We have to show the results progressively

                } else {

                    // Tell them who won
                    showErrorMessage(roomResults.getMessage());

                    /* Now we replace old things with new things */

                    // Get the cards that we want to fade
                    List<ImageView> cardsToFade = new ArrayList<>();

                    int stageCount = (roomResults.getGameStage()==3)?2:roomResults.getGameStage();
                    for(int i = 0; i < 3+stageCount; i++) {
                        cardsToFade.add(tableCardImage[i]);
                    }
                    cardsToFade.add(userCard[0]);
                    cardsToFade.add(userCard[1]);
                    for(int i = 0; i < 3; i++) {
                        if(layoutPlayer[i+1].getVisibility() == View.VISIBLE) {
                            playerCardImage[i][0].setVisibility(View.VISIBLE);
                            playerCardImage[i][1].setVisibility(View.VISIBLE);

                            cardsToFade.add(playerCardImage[i][0]);
                            cardsToFade.add(playerCardImage[i][1]);
                        }
                    }

                    Animation fade_out = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
                    for (ImageView image: cardsToFade) {
                        image.startAnimation(fade_out);
                    }

                    fade_out.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            for (ImageView image: cardsToFade) {
                                if(image.getVisibility() == View.VISIBLE) {
                                    int resId = getResources().getIdentifier("backside_old", "drawable", "com.example.lepti.pokerapp");
                                    image.setImageResource(resId);
                                    image.setVisibility(View.INVISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }
        });

        gameRoomViewModel.onReceiveAuthorizationToPlay().observe(getViewLifecycleOwner(), booleanResult -> {
            if(booleanResult instanceof Result.Error) {
                showErrorMessage(((Result.Error) booleanResult).getError());
            }
        });

        gameRoomViewModel.onReceiveReadyPlayerAuthorization().observe(getViewLifecycleOwner(), booleanResult -> {
            if(booleanResult instanceof Result.Error) {
                showErrorMessage(((Result.Error) booleanResult).getError());
            } else if(booleanResult instanceof Result.Success) {
                if(!((Result.Success<Boolean>) booleanResult).getData()) {
                    showErrorMessage(Constants.ERROR_UNKNOWN);
                }
            }
        });

        gameRoomViewModel.onReceiveInitialGameData().observe(getViewLifecycleOwner(), initialGameDataResult -> {
            if(initialGameDataResult.isDataValid() && initialGameDataResult.getError() == null) {
                Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                Animation triggerChangesAfterFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                userCard[0].startAnimation(triggerChangesAfterFadeIn);
                userCard[1].startAnimation(fadeIn);
                triggerChangesAfterFadeIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        List<Integer> cards = Arrays.asList(initialGameDataResult.getCard1(), initialGameDataResult.getCard2());
                        List<ImageView> imageList = Arrays.asList(userCard[0], userCard[1]);
                        CardManipulation.revealCards(getActivity(), getResources(), imageList, cards, 0);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                Animation fromTop = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                Animation triggerChangesAfterFromTop = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);

                fromTop.setStartOffset(2000);
                triggerChangesAfterFromTop.setStartOffset(2000);

                triggerChangesAfterFromTop.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        tableCardImage[0].setVisibility(View.VISIBLE);
                        tableCardImage[1].setVisibility(View.VISIBLE);
                        tableCardImage[2].setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        List<Integer> tableCards = Arrays.asList(initialGameDataResult.getTable1(), initialGameDataResult.getTable2(), initialGameDataResult.getTable3());
                        List<ImageView> imageList = Arrays.asList(tableCardImage[0], tableCardImage[1], tableCardImage[2]);
                        CardManipulation.revealCards(getActivity(), getResources(), imageList, tableCards, 0);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                tableCardImage[0].startAnimation(triggerChangesAfterFromTop);
                tableCardImage[1].startAnimation(fromTop);
                tableCardImage[2].startAnimation(fromTop);

                for(int i = 0; i < 3; i++) {
                    if(layoutPlayer[i+1].getVisibility() == View.VISIBLE) {
                        playerCardImage[i][0].setVisibility(View.VISIBLE);
                        playerCardImage[i][1].setVisibility(View.VISIBLE);

                        playerCardImage[i][0].startAnimation(fadeIn);
                        playerCardImage[i][1].startAnimation(fadeIn);
                    }
                }
            }
        });
    }

    public void showErrorMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }


}
