package fr.pnpc.project.server.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by stephen on 19/05/17.
 */
public class GsonSingleton {

    private static Gson gson;

    public final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static Gson getInstance() {
        if (gson == null) {
            return new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setDateFormat(DATE_FORMAT)
                    .setPrettyPrinting()
                    .create();
        }
        if (gson == null)
            throw new AssertionError("The gson instance should noy be null");
        return gson;
    }
}
