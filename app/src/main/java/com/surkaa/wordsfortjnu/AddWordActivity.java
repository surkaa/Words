package com.surkaa.wordsfortjnu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.surkaa.wordsfortjnu.word.Word;
import com.surkaa.wordsfortjnu.word.WordRepository;

import java.util.Objects;

public class AddWordActivity extends AppCompatActivity {

    TextInputEditText english, meaning, number;
    Button submitBtn, addAnotherBtn, backBtn;
    WordRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        repository = new WordRepository(getApplication());

        findView();
        initTextInputs();
        initSubmitBtn();
        initAddAnotherBtn();
        backBtn.setOnClickListener(vil -> backHome());
    }

    private void backHome() {
        // 直接返回首页并清空页面堆栈
        Intent intent = new Intent(this, WordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        // 关闭软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(english.getWindowToken(), 0);
    }

    private void findView() {
        submitBtn = findViewById(R.id.submit_btn);
        addAnotherBtn = findViewById(R.id.add_again_btn);
        backBtn = findViewById(R.id.back_btn);
        english = findViewById(R.id.edit_english);
        meaning = findViewById(R.id.edit_meaning);
        number = findViewById(R.id.edit_num);
    }

    private void initTextInputs() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 仅当单词和意思都有内容时，才允许提交按钮可用
                Editable englishText = english.getText();
                Editable meaningText = meaning.getText();
                if (englishText == null || meaningText == null) {
                    return;
                }
                String englishStr = englishText.toString().trim();
                String meaningStr = meaningText.toString().trim();
                boolean enable = !englishStr.isEmpty() && !meaningStr.isEmpty();
                submitBtn.setEnabled(enable);
                addAnotherBtn.setEnabled(enable);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        english.requestFocus();
        english.addTextChangedListener(watcher);
        meaning.addTextChangedListener(watcher);
    }

    private void initSubmitBtn() {
        submitBtn.setEnabled(false);
        submitBtn.setOnClickListener(v -> {
            saveOneWord();
            backHome();
        });
    }

    private void initAddAnotherBtn() {
        addAnotherBtn.setEnabled(false);
        addAnotherBtn.setOnClickListener(v -> {
            saveOneWord();
            english.setText("");
            meaning.setText("");
            number.setText("");
            english.requestFocus();
        });
    }

    private void saveOneWord() {
        String englishStr = Objects.requireNonNull(english.getText()).toString().trim();
        String meaningStr = Objects.requireNonNull(meaning.getText()).toString().trim();
        String numberStr = Objects.requireNonNull(number.getText()).toString().trim();
        int numInt = 0;
        if (!numberStr.isEmpty()) {
            numInt = Integer.parseInt(numberStr);
        }
        Word word = new Word(englishStr, meaningStr, numInt);
        repository.insert(word);
    }
}