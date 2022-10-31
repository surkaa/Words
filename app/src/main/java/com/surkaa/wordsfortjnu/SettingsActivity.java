package com.surkaa.wordsfortjnu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.surkaa.wordsfortjnu.word.WordRepository;
import com.surkaa.wordsfortjnu.word.myDefaultWords;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences shp;
    SwitchCompat switchCompat;
    Button backBtn, addBtn;
    WordRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        repository = new WordRepository(getApplication());

        shp = getSharedPreferences("default_settings", MODE_PRIVATE);

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
                    new myDefaultWords(SettingsActivity.this, repository).userSelectImport(true)
            );
            builder.setNegativeButton("六级词汇", (dialog, which) ->
                    new myDefaultWords(SettingsActivity.this, repository).userSelectImport(false)
            );
            builder.create().show();
        });
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = shp.edit();
            editor.putBoolean("is_off_word", isChecked);
            editor.apply();
        });
    }

    private void findView() {
        switchCompat = findViewById(R.id.settings_default_off);
        switchCompat.setChecked(shp.getBoolean("is_off_word", false));
        backBtn = findViewById(R.id.settings_back_btn);
        addBtn = findViewById(R.id.settings_add_btn);
    }
}