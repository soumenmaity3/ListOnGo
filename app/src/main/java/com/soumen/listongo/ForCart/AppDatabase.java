package com.soumen.listongo.ForCart;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CartModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CartDao cartDao();
}

