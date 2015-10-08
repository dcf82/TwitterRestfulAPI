package com.tae.twitter.common;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;

/**
 * Utilities
 *
 * @author David Castillo Fuentes <dcaf82@gmail.com>
 *
 */
public class Utility {
    private static final String LOG = Utility.class.getName();

    /**
     * Parses JSON String and transforms it to the desired type
     * @param json : Json String
     * @param type : Type of Object to generate
     */
    public static <T> T parseJSON(String json, Class<T> type) {
        T r = null;

        try {
            r = new Gson().fromJson(json, type);
        } catch (Exception e) {
            Log.i(LOG, "error while parsing :: " + e);
        }

        return r;
    }

    public static void hideKeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
