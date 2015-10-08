package com.twitter.api.util;

import android.content.ContentValues;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import java.util.Set;

/**
 * Some Utilities
 *
 * @author David Castillo Fuentes <dcaf82@gmail.com>
 *
 */
public class OkHttpUtil {
    public static RequestBody buildRequestBody(ContentValues values) {
        FormEncodingBuilder builder = new FormEncodingBuilder();

        if (values == null || values.size() == 0) {
            return builder.build();
        }

        Set<String> ketSet = values.keySet();

        for (String param : ketSet) {
            builder.add(param, values.get(param).toString());
        }

        return builder.build();
    }
}
