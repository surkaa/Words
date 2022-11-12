package com.surkaa.wordsfortjnu.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.surkaa.wordsfortjnu.R;
import com.surkaa.wordsfortjnu.word.WordRepository;
import com.surkaa.wordsfortjnu.word.myDefaultWords;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences shp;
    WordRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        repository = new WordRepository(getApplication());

        shp = getSharedPreferences(getString(R.string.shp), MODE_PRIVATE);

        initView();
    }

    private void initView() {
        initBackBtn();
        initAddBtn();
        initSwitch();
    }

    private void initBackBtn() {
        Button backBtn = findViewById(R.id.settings_back_btn);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, WordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void initAddBtn() {
        Button addBtn = findViewById(R.id.settings_add_btn);
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
    }

    private void initSwitch() {
        SwitchCompat switchCompat = findViewById(R.id.settings_default_off);
        switchCompat.setChecked(shp.getBoolean(getString(R.string.shp_editor_defaultCloseWord), false));
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = shp.edit();
            editor.putBoolean(getString(R.string.shp_editor_defaultCloseWord), isChecked);
            editor.apply();
        });
    }
}