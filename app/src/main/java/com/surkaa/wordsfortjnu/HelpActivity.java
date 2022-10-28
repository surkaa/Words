package com.surkaa.wordsfortjnu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.surkaa.wordsfortjnu.word.WordRepository;
import com.surkaa.wordsfortjnu.word.myDefaultWords;

public class HelpActivity extends AppCompatActivity {

    WordRepository repository;
    Button backBtn, addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        repository = new WordRepository(getApplication());

        findView();
        setListener();
    }

    private void setListener() {
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, WordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        addBtn.setOnClickListener(v -> {
//            repository.clear();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("添加数据");
            builder.setMessage("请问您需要添加的是四级词汇还是六级词汇?");
            builder.setPositiveButton("四级词汇", (dialog, which) ->
                    new myDefaultWords(HelpActivity.this, repository).userSelectImport(true)
            );
            builder.setNegativeButton("六级词汇", (dialog, which) ->
                    new myDefaultWords(HelpActivity.this, repository).userSelectImport(false)
            );
            builder.create().show();
        });
    }

    private void findView() {
        backBtn = findViewById(R.id.help_back_btn);
        addBtn = findViewById(R.id.help_add_btn);
    }
}