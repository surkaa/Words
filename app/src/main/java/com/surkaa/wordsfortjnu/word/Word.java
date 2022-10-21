package com.surkaa.wordsfortjnu.word;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Word {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private final String english;
    private final String meaning;

    private boolean isClose;
    private int count;
    private final int numInExamination;

    public Word(String english, String meaning, int numInExamination) {
        this.count = (int) (Math.random() * 15);
        this.isClose = Math.random() > 0.5;
        this.english = english;
        this.meaning = meaning;
        this.numInExamination = numInExamination;
    }

    @NonNull
    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", english='" + english + '\'' +
                ", meaning='" + meaning + '\'' +
                ", isClose=" + isClose +
                ", count=" + count +
                ", numInExamination=" + numInExamination +
                "}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Word)) return false;
        Word word = (Word) o;
        return isClose == word.isClose && count == word.count
                && numInExamination == word.numInExamination
                && Objects.equals(english, word.english)
                && Objects.equals(meaning, word.meaning);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, english, meaning, isClose, count, numInExamination);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnglish() {
        return english;
    }

    public String getMeaning() {
        return meaning;
    }

    public boolean isClose() {
        return isClose;
    }

    public void setClose(boolean close) {
        isClose = close;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addCount() {
        ++count;
    }

    public int getNumInExamination() {
        return numInExamination;
    }

}
