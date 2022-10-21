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
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class WordActivity extends AppCompatActivity {

    FloatingActionButton addBtn;
    LottieAnimationView emptyView;
    ImageView clearBtn, topBtn, bottomBtn;

    RecyclerView recyclerView;
    WordRepository repository;
    WordAdapter adapter;
    LiveData<List<Word>> filteredList;
    Observer<List<Word>> observer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);

        // 找到控件
        findView();

        // 仓库及适配器
        repository = new WordRepository(getApplication());
        adapter = new WordAdapter(repository);

        // 设置监听器
        initViewListener();
        // RecyclerView的初始化
        initRecyclerView();
        // 数据filteredList的初始化
        initLiveDataList();

        // 默认数据的导入: 仅在安装后第一次运行时导入
        addDefaultWords();

    }

    //<editor-fold desc="找到界面控件并设置监听事件">
    private void findView() {
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_state_animation);
        addBtn = findViewById(R.id.add_word);
        clearBtn = findViewById(R.id.img_btn_clear);
        topBtn = findViewById(R.id.btn_top);
        bottomBtn = findViewById(R.id.btn_bottom);
    }

    private void initViewListener() {
        initClearBtn();
        initAddBtn();
        initTopBtn();
        initBottomBtn();
    }

    private void initClearBtn() {
        clearBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(WordActivity.this);
            builder.setTitle("确认清除所有单词吗？");
            builder.setPositiveButton("确认", (dialog, which) -> repository.clear());
            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.create().show();
        });
    }

    private void initAddBtn() {
        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddWordActivity.class);
            startActivity(intent);
        });
    }

    private void initTopBtn() {
        topBtn.setOnClickListener(v -> recyclerView.smoothScrollToPosition(0));
    }

    private void initBottomBtn() {
        bottomBtn.setOnClickListener(v -> recyclerView.smoothScrollToPosition(Math.max(adapter.getItemCount() - 1, 0)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 在顶部设置出仅有一个搜索item的菜单
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        // 设置搜索框的长度避免label被压缩
        int width = getResources().getDisplayMetrics().widthPixels;
        searchView.setMaxWidth((int) (width * 0.75));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // 去掉前后空格
                String pattern = s.trim();
                // 改变过程中移除观察者 以防错乱
                filteredList.removeObservers(WordActivity.this);
                filteredList = repository.getWordsWithPattern(pattern);
                // 重新设置观察者
                filteredList.observe(WordActivity.this, observer);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    //</editor-fold>

    //<editor-fold desc="默认数据的导入">
    private void addDefaultWords() {
        SharedPreferences shp = getSharedPreferences("defaultData", Context.MODE_PRIVATE);
        if (shp.getBoolean("defaultFlag", true)) {
            SharedPreferences.Editor editor = shp.edit();
            editor.putBoolean("defaultFlag", false);
            try {
                insertDefaultData();
            } catch (IOException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Default data insert failed");
                builder.setPositiveButton("OK", null);
                builder.show();
            }
            editor.apply();
        }
    }

    private void insertDefaultData() throws IOException {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("默认数据");
        builder.setMessage("检测到当前列表为空\n请问您是否需要导入的默认数据?");
        builder.setPositiveButton("我要四级词汇!", (dialog, which) -> {
            try {
                insertCet4();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        builder.setNegativeButton("我要六级词汇!", (dialog, which) -> {
            try {
                insertCet6();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        builder.create().show();
    }

    private void insertCet4() throws IOException {
        BufferedReader englishReader = new BufferedReader(new InputStreamReader(getAssets().open("cet4/english4.txt")));
        BufferedReader meaningReader = new BufferedReader(new InputStreamReader(getAssets().open("cet4/meaning4.txt")));
        BufferedReader numbersReader = new BufferedReader(new InputStreamReader(getAssets().open("cet4/numbers4.txt")));
        String english, meaning, numbers;
        while ((english = englishReader.readLine()) != null
                && (meaning = meaningReader.readLine()) != null
                && (numbers = numbersReader.readLine()) != null) {
            Word word = new Word(english, meaning, Integer.parseInt(numbers));
            repository.insert(word);
        }
        Toast.makeText(this, "四级词汇添加成功", Toast.LENGTH_SHORT).show();
    }

    private void insertCet6() throws IOException {
        BufferedReader englishReader = new BufferedReader(new InputStreamReader(getAssets().open("cet6/english6.txt")));
        BufferedReader meaningReader = new BufferedReader(new InputStreamReader(getAssets().open("cet6/meaning6.txt")));
        BufferedReader numbersReader = new BufferedReader(new InputStreamReader(getAssets().open("cet6/numbers6.txt")));
        String english, meaning, numbers;
        while ((english = englishReader.readLine()) != null
                && (meaning = meaningReader.readLine()) != null
                && (numbers = numbersReader.readLine()) != null) {
            Word word = new Word(english, meaning, Integer.parseInt(numbers));
            repository.insert(word);
        }
        Toast.makeText(this, "六级词汇添加成功", Toast.LENGTH_SHORT).show();
    }
    //</editor-fold>

    private void initRecyclerView() {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 用于序列号的修改, 移动, 删除等操作时用到,并配合适配器的onViewAttachedToWindow()方法
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

        // 优化recyclerView的功能
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // 用于支持长按拖动
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int indexFrom = viewHolder.getAdapterPosition();
                int indexTo = target.getAdapterPosition();
                Word wordFrom = Objects.requireNonNull(filteredList.getValue()).get(indexFrom);
                Word wordTo = filteredList.getValue().get(indexTo);
                int idTemp = wordFrom.getId();
                wordFrom.setId(wordTo.getId());
                wordTo.setId(idTemp);
                repository.update(wordFrom, wordTo);
                adapter.notifyItemMoved(indexFrom, indexTo);
                return false;
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                // 第一个卡片禁止拖动, 以防bug
                if (viewHolder.getAdapterPosition() == 0) {
                    return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                }
                return super.getMovementFlags(recyclerView, viewHolder);
            }


            // 设置item的滑动删除
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                 int direction) {
                final Word wordToDelete = Objects.requireNonNull(filteredList.getValue()).get(viewHolder.getAdapterPosition());
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
            if (len != 0) {
                // 暂停并隐藏
                emptyView.pauseAnimation();
                emptyView.setVisibility(View.INVISIBLE);
            } else {
                // 只播放一遍
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setRepeatCount(1);
            }

            adapter.setList(words);
            // TODO 考虑是否需要判断!=, 而是直接submitList
            if (len != adapter.getItemCount()) {
                adapter.submitList(words);
            }
        };
        filteredList = repository.getAll();
        filteredList.observe(this, observer);
    }
}