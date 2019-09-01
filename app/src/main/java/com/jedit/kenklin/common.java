package com.jedit.kenklin;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.jedit.kenklin.models.User_Class;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class common {

    public static final String STATE_ACTIVE = "Active";
    public static final String STATE_PENDING = "Pending";
    public static final String STATE_COMPLETED = "Completed";
    public static final String STATE_CANCELLED = "Cancelled";

    public static String time_to_date(String timeMillis){

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(Long.parseLong(timeMillis));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        return  simpleDateFormat.format(calendar.getTime());
    }

    public static User_Class userBundle(Bundle bundle){
        User_Class user = null;

        if (bundle != null && FirebaseAuth.getInstance().getCurrentUser() != null){
            user = new User_Class(
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    bundle.getString(User_Class.FIRSTNAME),
                    bundle.getString(User_Class.LASTNAME),
                    bundle.getString(User_Class.MOBILE_NUMBER));
        }
        return user;
    }

    public static Snackbar Mysnackbar(View parent_view, String message, int lenght) {

        final Snackbar snackbar = Snackbar.make(parent_view, message, lenght);
        snackbar.setActionTextColor(parent_view.getContext().getResources().getColor(R.color.colorPrimary));
        snackbar.setAction("Close", v -> snackbar.dismiss());

        return snackbar;
    }

}
