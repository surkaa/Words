package com.surkaa.wordsfortjnu.word;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class myDefaultWords {
    static final int notRequiredTop = 25;
    static final int notRequiredBottom = 0;
    Context context;
    WordRepository repository;

    public myDefaultWords(Context context, WordRepository repository) {
        this.context = context;
        this.repository = repository;
    }

    public void userFirstRun() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("默认数据");
        builder.setMessage("检测到当前列表为空\n请问您是否需要导入的默认数据?");
        builder.setPositiveButton("我要四级词汇!", (dialog, which) -> userSelectImport(true));
        builder.setNegativeButton("我要六级词汇!", (dialog, which) -> userSelectImport(false));
        builder.create().show();
    }

    public void userSelectImport(boolean isCET4) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("导入数量");
        builder.setMessage("检测到您对某些单词的记忆十分牢固\n是否需要去掉这部分单词(去掉常用单词)");
        builder.setPositiveButton("我不需要!", (dialog, which) -> insertDefaultWord(isCET4, notRequiredTop, notRequiredBottom));
        builder.setNegativeButton("不, 我得留着", (dialog, which) -> insertDefaultWord(isCET4, Integer.MAX_VALUE, Integer.MIN_VALUE));
        builder.create().show();
    }

    public void insertDefaultWord(boolean isCET4, int maxNumber, int minNumber) {
        try {
            if (isCET4) {
                insertDefaultWordsCET4(maxNumber, minNumber);
            } else {
                insertDefaultWordsCET6(maxNumber, minNumber);
            }
        } catch (IOException e) {
            showError();
        }
    }

    public void insertDefaultWordsCET4(int maxNumber, int minNumber) throws IOException {
        BufferedReader englishReader = new BufferedReader(new InputStreamReader(context.getAssets().open("cet4/english4.txt")));
        BufferedReader meaningReader = new BufferedReader(new InputStreamReader(context.getAssets().open("cet4/meaning4.txt")));
        BufferedReader numbersReader = new BufferedReader(new InputStreamReader(context.getAssets().open("cet4/numbers4.txt")));
        String english, meaning, numbers;
        while ((english = englishReader.readLine()) != null
                && (meaning = meaningReader.readLine()) != null
                && (numbers = numbersReader.readLine()) != null) {
            int number = Integer.parseInt(numbers);
            if (number <= maxNumber && number >= minNumber) {
                Word word = new Word(english, meaning, number);
                repository.insert(word);
            }
        }
        Toast.makeText(context, "四级词汇添加成功", Toast.LENGTH_SHORT).show();
    }

    public void insertDefaultWordsCET6(int maxNumber, int minNumber) throws IOException {
        BufferedReader englishReader = new BufferedReader(new InputStreamReader(context.getAssets().open("cet6/english6.txt")));
        BufferedReader meaningReader = new BufferedReader(new InputStreamReader(context.getAssets().open("cet6/meaning6.txt")));
        BufferedReader numbersReader = new BufferedReader(new InputStreamReader(context.getAssets().open("cet6/numbers6.txt")));
        String english, meaning, numbers;
        while ((english = englishReader.readLine()) != null
                && (meaning = meaningReader.readLine()) != null
                && (numbers = numbersReader.readLine()) != null) {
            int number = Integer.parseInt(numbers);
            if (number <= maxNumber && number >= minNumber) {
                Word word = new Word(english, meaning, number);
                repository.insert(word);
            }
        }
        Toast.makeText(context, "六级词汇添加成功", Toast.LENGTH_SHORT).show();
    }

    public void showError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("错误");
        builder.setMessage("导入默认数据失败");
        builder.setPositiveButton("好叭", null);
        builder.show();
    }

}
