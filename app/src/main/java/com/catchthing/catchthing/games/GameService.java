package com.catchthing.catchthing.games;

import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by ButuzovVU on 28.01.2018.
 */

class GameService {

    private static final int SIZE_CIRCLES = 75;

    RelativeLayout.LayoutParams calculateRandomParams(RelativeLayout relativeLayout, Random random) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(SIZE_CIRCLES, SIZE_CIRCLES);
        System.out.println("Relative height " + relativeLayout.getHeight() + " width " + relativeLayout.getWidth());

        int left = random.nextInt(relativeLayout.getWidth());
        int top = random.nextInt(relativeLayout.getHeight());

        if (left > SIZE_CIRCLES) {
            if (left < relativeLayout.getWidth() - SIZE_CIRCLES) {
                System.out.println("norm game_left");
                layoutParams.leftMargin = left;
            } else {
                System.out.println("game_left " + left + " > l gr");
                layoutParams.leftMargin = left - (SIZE_CIRCLES - (relativeLayout.getWidth() - left - 1));
            }
        } else {
            System.out.println("l " + left + " < size");
            layoutParams.leftMargin += (SIZE_CIRCLES + 1);
        }
        if (top > SIZE_CIRCLES) {
            if (top < relativeLayout.getHeight() - SIZE_CIRCLES) {
                System.out.println("norm top");
                layoutParams.topMargin = top;
            } else {
                System.out.println("t " + top + " > t gr");
                layoutParams.topMargin = top - (SIZE_CIRCLES - (relativeLayout.getHeight() - top - 1));
            }
        } else {
            System.out.println("t " + top + " < size");
            layoutParams.topMargin += (SIZE_CIRCLES + 1);
        }
        System.out.println("rand l " + left + " rand t " + top);
        System.out.println("lp l " + layoutParams.leftMargin + " lp t " + layoutParams.topMargin);
        return layoutParams;
    }
}
