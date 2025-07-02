package com.soumen.listongo.ForCart;

import android.content.Context;

import androidx.room.Room;

public class AppDatabaseClient {
    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "cart_items").build();
        }
        return instance;
    }
}

