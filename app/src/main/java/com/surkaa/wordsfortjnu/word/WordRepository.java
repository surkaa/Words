package com.surkaa.wordsfortjnu.word;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * @author SurKaa
 */
public class WordRepository {
    private final LiveData<List<Word>> list;
    private final WordDao wordDao;

    public WordRepository(Application contest) {
        WordDatabase database = WordDatabase.getDatabase(contest.getApplicationContext());
        wordDao = database.getWordDao();
        list = wordDao.getAll();
    }

    //region 表操作
    public void insert(Word... words) {
        new InsertAsyncTask(wordDao).execute(words);
    }

    public void update(Word... words) {
        new UpdateAsyncTask(wordDao).execute(words);
    }

    public void delete(Word... words) {
        new DeleteAsyncTask(wordDao).execute(words);
    }

    public void clear() {
        new ClearAsyncTask(wordDao).execute();
    }

    public LiveData<List<Word>> getAll() {
        return list;
    }

    public LiveData<List<Word>> getWordsWithPattern(String pattern) {
        // %表示任意多个字符 ps: hello
        char[] chars = pattern.toCharArray();
        StringBuilder str = new StringBuilder();
        str.append('%');
        for (char ch : chars) {
            str.append(ch).append('%');
        }
        // ps: %h%e%l%l%o%
        return wordDao.getWordsWithPattern(str.toString());
    }
    //endregion

    //region AsyncTask
    static class InsertAsyncTask extends AsyncTask<Word, Void, Void> {
        private final WordDao wordDao;

        public InsertAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.insert(words);
            return null;
        }
    }

    static class UpdateAsyncTask extends AsyncTask<Word, Void, Void> {
        private final WordDao wordDao;

        public UpdateAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.update(words);
            return null;
        }
    }

    static class DeleteAsyncTask extends AsyncTask<Word, Void, Void> {
        private final WordDao wordDao;

        public DeleteAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.delete(words);
            return null;
        }
    }

    static class ClearAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WordDao wordDao;

        public ClearAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wordDao.clear();
            return null;
        }
    }
    //endregion
}

