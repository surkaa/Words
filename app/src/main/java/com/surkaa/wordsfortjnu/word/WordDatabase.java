package com.surkaa.wordsfortjnu.word;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Word.class}, version = 1, exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {
    public abstract WordDao getWordDao();
    private static volatile WordDatabase INSTANCE;
    public synchronized static WordDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (WordDatabase.class) {
                INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(), WordDatabase.class, "word_database")
                        .build();
            }
        }
        return INSTANCE;
    }
}
