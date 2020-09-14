package com.games.pokerkings.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.games.pokerkings.R;

import java.util.List;

public class CardManipulation {
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static String returnSuitName(int card) {
        String suitName = "";
        int suit = Math.floorDiv(card, 13);
        switch(suit) {
            case 0: {
                suitName = "clubs";
                break;
            }
            case 1: {
                suitName = "diamonds";
                break;
            }
            case 2: {
                suitName = "spades";
                break;
            }
            case 3: {
                suitName = "hearts";
                break;
            }
        }
        return suitName;
    }

    private static String returnCardRank(int card) {
        String cardRank;
        int rank = (card)%13;
        if(rank == 0) {
            cardRank = "ace";
        }
        else if(rank > 0 && rank < 10) {
            cardRank = "c" + Integer.toString((rank+1));
        }
        else if(rank == 10) {
            cardRank = "jack";
        }
        else if(rank == 11) {
            cardRank =  "queen";
        }
        else {
            cardRank = "king";
        }
        return cardRank;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String returnCardName(int card) {
        String suitName = returnSuitName(card);
        String cardRank = returnCardRank(card);
        String cardName = cardRank + "_of_" + suitName;
        return cardName;
    }

    public static void revealCards(Context context, Resources resources, List<ImageView> images, List<Integer> cards, Integer currentCard) {
        AnimatorSet set = new AnimatorSet();
        Animator animator1 = AnimatorInflater.loadAnimator(context,
                R.animator.flip_out);
        Animator animator2 = AnimatorInflater.loadAnimator(context,
                R.animator.flip_in);
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onAnimationEnd(Animator animation) {
                String cardName = CardManipulation.returnCardName(cards.get(currentCard));
                images.get(currentCard).setImageResource(resources.getIdentifier(cardName, "drawable", "com.games.pokerkings"));
                if(currentCard+1 < images.size()) {
                    revealCards(context, resources, images, cards, currentCard+1);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        set.playSequentially(animator1, animator2);
        set.setTarget(images.get(currentCard));
        set.start();
    }

    public static ObjectAnimator fadeCardOut(Resources resources, ImageView view, int startDelay) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        Drawable drawable = resources.getDrawable(R.drawable.backside_old);
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setImageDrawable(drawable);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        fadeOut.setStartDelay(startDelay);
        fadeOut.setDuration(2000);
        return fadeOut;
    }

    public static void fadeOutAndIn(Resources resource, ImageView image, int fadeOutDelay, int fadeInDelay) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator fadeOut = CardManipulation.fadeCardOut(resource, image, fadeOutDelay);
        ObjectAnimator fadeIn = CardManipulation.fadeCardIn(image, fadeInDelay);
        animatorSet.playSequentially(fadeOut, fadeIn);
        animatorSet.start();
    }


    public static ObjectAnimator fadeCardIn(ImageView view, int startDelay) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setStartDelay(startDelay);
        return fadeIn;
    }
}
