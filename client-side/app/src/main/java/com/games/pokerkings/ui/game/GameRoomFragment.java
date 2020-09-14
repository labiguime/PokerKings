package com.games.pokerkings.ui.game;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.games.pokerkings.R;

import com.games.pokerkings.data.DisconnectionType;
import com.games.pokerkings.data.models.*;
import com.games.pokerkings.databinding.FragmentGameRoomBinding;
import com.games.pokerkings.utils.CardManipulation;
import com.games.pokerkings.utils.Constants;
import com.games.pokerkings.utils.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.games.pokerkings.utils.CardManipulation.fadeOutAndIn;

public class GameRoomFragment extends Fragment {

    private GameRoomViewModel gameRoomViewModel;

    LinearLayout[] layoutPlayer = new LinearLayout[4];
    ImageView[][] playerCardImage = new ImageView[4][2];
    ImageView[] tableCardImage = new ImageView[5];
    ImageView[] userCard = new ImageView[2];
    EditText raiseText;


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

        raiseText = binding.getRoot().findViewById(R.id.raise_text);

        // Recover variables from previous fragment
        Bundle bundle = this.getArguments();

        // TODO: Implement steps to take if bundle transfer fails
        if(bundle == null) {
            return null;
        }
        gameRoomViewModel.setUserInterfaceForUser((User)bundle.getSerializable("user"));

        if(!(binding.getRoot() instanceof EditText)) {

            binding.getRoot().setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboardFrom(getContext(), binding.getRoot());
                    raiseText.clearFocus();
                    return false;
                }
            });
        }

        return binding.getRoot();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gameRoomViewModel.onReceiveRoomState().observe(getViewLifecycleOwner(), roomState -> {
            if(roomState.getError() != null) {
                showErrorMessage(roomState.getError());
            } else {
                if(roomState.getHasRoundEnded()) {
                    tableCardImage[roomState.getGameStage()+2].setVisibility(View.VISIBLE);
                    CardManipulation.fadeCardIn(tableCardImage[roomState.getGameStage()+2], 0).start();
                    Handler handler = new Handler();
                    Runnable runnable = () -> {
                        List<Integer> tableCards = Arrays.asList(roomState.getTableCard());
                        List<ImageView> imageList = Arrays.asList(tableCardImage[roomState.getGameStage()+2]);
                        CardManipulation.revealCards(getActivity(), getResources(), imageList, tableCards, 0);
                    };
                    handler.postDelayed(runnable, 1300);

                }
            }
        });

        gameRoomViewModel.onReceiveDisconnectEvent().observe(getViewLifecycleOwner(), disconnectionType -> {
            if(disconnectionType.getType() == 1) {
                showErrorMessage("A player has disconnected so the game has ended!");
            }
        });

        gameRoomViewModel.onReceiveRoomResults().observe(getViewLifecycleOwner(), roomResults -> {
            if(roomResults.getError() != null) {
                showErrorMessage(roomResults.getError());
            } else {

                if(roomResults.getHasRoundEnded() && roomResults.getGameStage() == 3) { // We have to show the results progressively

                    Integer me = roomResults.getMyIndex();
                    Integer nPlayers = roomResults.getnPlayers();

                    for(int i = 0; i < 4; i++) {
                        if(i == me) continue;
                        int id = CardManipulation.getLayoutForId(me, i, nPlayers);
                        if(layoutPlayer[id].getVisibility() == View.VISIBLE) {
                            List<Integer> userCards = Arrays.asList(roomResults.getAllCards().get(0+i*2), roomResults.getAllCards().get(1+i*2));
                            if(userCards.get(0) == -1 || userCards.get(1) == -1) continue;

                            List<ImageView> imageList = Arrays.asList(playerCardImage[id-1][0], playerCardImage[id-1][1]);
                            CardManipulation.revealCards(getActivity(), getResources(), imageList, userCards, 0);
                        }
                    }

                    Handler handler = new Handler();
                    Runnable runnable = () -> showErrorMessage(roomResults.getMessage());
                    Runnable runnable1 = () -> hideAllCards(roomResults.getGameStage());
                    Runnable runnable2 = () -> {

                        List<Integer> userCards = Arrays.asList(roomResults.getCard1(), roomResults.getCard2());
                        List<ImageView> imageList = Arrays.asList(userCard[0], userCard[1]);
                        CardManipulation.revealCards(getActivity(), getResources(), imageList, userCards, 0);

                        List<Integer> tableCards = Arrays.asList(roomResults.getTable1(), roomResults.getTable2(), roomResults.getTable3());
                        imageList = Arrays.asList(tableCardImage[0], tableCardImage[1], tableCardImage[2]);
                        CardManipulation.revealCards(getActivity(), getResources(), imageList, tableCards, 0);

                        gameRoomViewModel.triggerAfterRoomResultsChanges();
                    };
                    handler.postDelayed(runnable, 5000);
                    handler.postDelayed(runnable1, 10000);
                    handler.postDelayed(runnable2, 21000);

                } else {
                    // Tell them who won
                    showErrorMessage(roomResults.getMessage());

                    //
                    hideAllCards(roomResults.getGameStage());

                    Handler handler = new Handler();
                    Runnable runnable = () -> {

                        List<Integer> userCards = Arrays.asList(roomResults.getCard1(), roomResults.getCard2());
                        List<ImageView> imageList = Arrays.asList(userCard[0], userCard[1]);
                        CardManipulation.revealCards(getActivity(), getResources(), imageList, userCards, 0);

                        List<Integer> tableCards = Arrays.asList(roomResults.getTable1(), roomResults.getTable2(), roomResults.getTable3());
                        imageList = Arrays.asList(tableCardImage[0], tableCardImage[1], tableCardImage[2]);
                        CardManipulation.revealCards(getActivity(), getResources(), imageList, tableCards, 0);

                        gameRoomViewModel.triggerAfterRoomResultsChanges();
                    };
                    handler.postDelayed(runnable, 11000);
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

        gameRoomViewModel.getHasUserInterfaceLoaded().observe(getViewLifecycleOwner(), aBoolean -> {
            if(!aBoolean) {
                //gameRoomViewModel.reloadUserInterface();
            }
        });

        gameRoomViewModel.onReceivePreGamePlayerList().observe(getViewLifecycleOwner(), aBoolean -> {
            if(!aBoolean) {

            }
        });

        gameRoomViewModel.onReceiveInitialGameData().observe(getViewLifecycleOwner(), initialGameDataResult -> {
            if(initialGameDataResult.isDataValid() && initialGameDataResult.getError() == null) {
                List<Integer> userCards = Arrays.asList(initialGameDataResult.getCard1(), initialGameDataResult.getCard2());
                List<Integer> tableCards = Arrays.asList(initialGameDataResult.getTable1(), initialGameDataResult.getTable2(), initialGameDataResult.getTable3());
                showCards(userCards, tableCards);
            }
        });
    }

    public void showCards(List<Integer> userCards, List<Integer> tableCards) {
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

                List<ImageView> imageList = Arrays.asList(userCard[0], userCard[1]);
                CardManipulation.revealCards(getActivity(), getResources(), imageList, userCards, 0);
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

    public void showErrorMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    public void hideAllCards(Integer gameStage) {
        // Get the cards that we want to fade
        List<ImageView> shortFadeList = new ArrayList<>();
        List<ImageView> longFadeList = new ArrayList<>();

        if(gameStage > 1) {
            longFadeList.add(tableCardImage[3]);
        }

        if(gameStage > 2) {
            longFadeList.add(tableCardImage[4]);
        }

        shortFadeList.addAll(Arrays.asList(userCard).subList(0, 2));
        for(int i = 0; i < 3; i++) {
            if(layoutPlayer[i+1].getVisibility() == View.VISIBLE) {
                shortFadeList.add(playerCardImage[i][0]);
                shortFadeList.add(playerCardImage[i][1]);
            }
        }

        for (ImageView image: shortFadeList) {
            fadeOutAndIn(getResources(), image, 3000, 5000);
        }

        for (ImageView image: longFadeList) {
            CardManipulation.fadeCardOut(getResources(), image, 3000).start();
        }

        for (int i = 0; i < 3; i++) {
            fadeOutAndIn(getResources(), tableCardImage[i], 3000, 5000);
        }
    }


}
