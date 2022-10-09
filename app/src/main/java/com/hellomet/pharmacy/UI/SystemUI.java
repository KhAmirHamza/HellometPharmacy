package com.hellomet.pharmacy.UI;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

public class SystemUI {
    Activity activity;

    public SystemUI(Activity activity) {
        this.activity = activity;

    }

    public void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = activity.getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(

            //View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //light statuus bar

                    //| View.SYSTEM_UI_FLAG_IMMERSIVE
                     View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    //| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                   // | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    //| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar

            );
        }
    }
}
