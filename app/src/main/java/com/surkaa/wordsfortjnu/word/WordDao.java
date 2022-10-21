package com.surkaa.wordsfortjnu.word;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WordDao {
    @Insert
    void insert(Word... words);

    @Update
    void update(Word... words);

    @Delete
    void delete(Word... words);

    @Query("DELETE FROM word")
    void clear();

    @Query("SELECT * FROM word ORDER BY id DESC")
    LiveData<List<Word>> getAll();

    @Query("SELECT * FROM word WHERE english LIKE :pattern OR meaning LIKE :pattern ORDER BY id DESC")
    LiveData<List<Word>> getWordsWithPattern(String pattern);
}
