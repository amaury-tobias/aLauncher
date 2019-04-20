package me.amaurytq.alauncher.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {

    private static DatabaseClient mInstance;
    private AppItemDatabase appItemDatabase;

    private DatabaseClient(Context context) {
        appItemDatabase = Room.databaseBuilder(context, AppItemDatabase.class, "AppItemDB").build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(context);
        }
        return mInstance;
    }

    public AppItemDatabase getAppItemDatabase() {
        return appItemDatabase;
    }
}
