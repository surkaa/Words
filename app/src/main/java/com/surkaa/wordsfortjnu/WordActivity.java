package com.surkaa.wordsfortjnu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.surkaa.wordsfortjnu.WordAdapter.WordHolder;
import com.surkaa.wordsfortjnu.word.Word;
import com.surkaa.wordsfortjnu.word.WordRepository;

import java.util.List;

public class WordActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WordRepository repository;
    WordAdapter adapter;
    LiveData<List<Word>> filteredList;
    LottieAnimationView emptyView;
    FloatingActionButton addBtn;
    Observer<List<Word>> observer;
    ImageView clearBtn;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);

        findView();

        repository = new WordRepository(getApplication());
        adapter = new WordAdapter(repository);

        initClearBtn();
        initSwitch();
        initRecyclerView();
        initLiveDataList();
        initAddBtn();

        addDefaultWords();
    }

    private void addDefaultWords() {
        SharedPreferences shp = getSharedPreferences("defaultData", Context.MODE_PRIVATE);
        if (shp.getBoolean("defaultFlag", true)) {
            SharedPreferences.Editor editor = shp.edit();
            editor.putBoolean("defaultFlag", false);
            repository.insert(
                    new Word("people", "n. 人, 人民, 民族, 平民vt. 使住满人, 居住于", 549),
                    new Word("say", "vt. 说, 讲, 念,说明, 指明vi. 说, 讲n. 意见, 发言权", 412),
                    new Word("make", "vt. 制造, 安排,创造, 构成, 使得,产生, 造成, 整理,布置, 引起,到达,进行vi. 开始, 前进,增大, 被制造, 被处理n. 制造, 构造, 性情", 396),
                    new Word("take", "vt. 拿, 取, 占有, 理解,领会, 行使, 引起, 采取,接受, 容纳, 济贫vt. 采访,视察,播送n. 接人, 绳子vi. 接人, 环绕, 乡下", 189),
                    new Word("check", "已阅", 0)
            );
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 在顶部设置出仅有一个搜索item的菜单
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
//        int width = getResources().getDisplayMetrics().widthPixels;
//        searchView.setMaxWidth((int) (width * 0.75));

        // 设置固定长度 800像素
        searchView.setMaxWidth(800);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String pattern = s.trim();
                filteredList.removeObservers(WordActivity.this);
                filteredList = repository.getWordsWithPattern(pattern);
                filteredList.observe(WordActivity.this, observer);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void clearAllWords() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WordActivity.this);
        builder.setTitle("确认清除所有单词吗？");
        builder.setPositiveButton("确认", (dialog, which) -> repository.clear());
        builder.setNegativeButton("取消", (dialog, which) -> {
        });
        builder.create().show();
    }

    private void findView() {
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_state_animation);
        addBtn = findViewById(R.id.add_word);
        clearBtn = findViewById(R.id.img_btn_clear);
        switch1 = findViewById(R.id.switch1);
    }

    private void initClearBtn() {
        clearBtn.setOnClickListener(view -> clearAllWords());
    }

    private void initSwitch() {
        switch1.setChecked(false);
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                repository.close();
            } else {
                repository.open();
            }
            adapter.submitList(filteredList.getValue());
        });
    }

    private void initRecyclerView() {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                super.onAnimationFinished(viewHolder);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (manager != null) {
                    int first = manager.findFirstVisibleItemPosition();
                    int last = manager.findLastVisibleItemPosition();
                    for (int i = first; i <= last; i++) {
                        WordHolder holder = (WordHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if (holder != null) {
                            holder.id.setText(String.valueOf(i + 1));
                        }
                    }
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                Word wordFrom = filteredList.getValue().get(viewHolder.getAdapterPosition());
                Word wordTo = filteredList.getValue().get(target.getAdapterPosition());
                int idTemp = wordFrom.getId();
                wordFrom.setId(wordTo.getId());
                wordTo.setId(idTemp);
                repository.update(wordFrom, wordTo);
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final Word wordToDelete = filteredList.getValue().get(viewHolder.getAdapterPosition());
                repository.delete(wordToDelete);
                Snackbar.make(findViewById(R.id.word_layout), "删除了一个词汇", Snackbar.LENGTH_SHORT)
                        .setAction("撤销", v -> repository.insert(wordToDelete))
                        .show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void initLiveDataList() {
        observer = words -> {
            int len = words.size();
            emptyView.setVisibility(len == 0 ? View.VISIBLE : View.GONE);
            adapter.setList(words);
            if (len != adapter.getItemCount()) {
                recyclerView.smoothScrollBy(0, -230);
                adapter.submitList(words);
            }
        };
        filteredList = repository.getAll();
        filteredList.observe(this, observer);
    }

    private void initAddBtn() {
        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddWordActivity.class);
            startActivity(intent);
        });
    }
}