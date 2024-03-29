package com.surkaa.wordsfortjnu.word;

import static com.surkaa.wordsfortjnu.R.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.surkaa.wordsfortjnu.R;

import java.util.List;

public class WordAdapter extends ListAdapter<Word, WordAdapter.WordHolder> {

    private final WordRepository wordRepository;
    private List<Word> list;
    private final SharedPreferences shp;
    private final Context context;
    final static String HTTPS_YouDao = "https://m.youdao.com/dict?le=eng&q=";
    final static String HTTPS_NiuJin = "https://www.oxfordlearnersdictionaries.com/definition/english/";

    public WordAdapter(WordRepository wordRepository, Context c) {
        super(new DiffUtil.ItemCallback<Word>() {
            @Override
            public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.wordRepository = wordRepository;
        this.context = c;
        shp = context.getSharedPreferences(context.getString(string.shp), Context.MODE_PRIVATE);
    }

    public void setList(List<Word> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public WordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout.item_word, parent, false);
        final WordHolder holder = new WordHolder(view);

        holder.comeInConstraintLayout.setOnClickListener(v -> {
            if (!holder.aSwitch.isChecked()) {
                Uri uri = Uri.parse(getHttps() + holder.english.getText());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Word word;
            try {
                word = list.get(holder.getAdapterPosition());
            } catch (Exception e) {
                return;
            }
            word.setClose(isChecked);
            wordRepository.update(word);
            holder.imageView.setSelected(isChecked);
            holder.chip.setVisibility(isChecked ? View.INVISIBLE : View.VISIBLE);
            holder.meaning.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            holder.count.setVisibility(isChecked ? View.INVISIBLE : View.VISIBLE);
        });

        holder.chip.setOnClickListener(v -> {
            Word word;
            try {
                word = list.get(holder.getAdapterPosition());
            } catch (Exception e) {
                return;
            }
            word.addCount();
            notifyItemChanged(holder.getAdapterPosition());
            holder.aSwitch.setChecked(true);
        });
        return holder;
    }

    @SuppressLint("NonConstantResourceId")
    private String getHttps() {
        int defaultId = shp.getInt(context.getString(string.shp_editor_defaultUseHttps), 0);
        switch (defaultId) {
            case R.id.radioButton_youdao:
                return HTTPS_YouDao;
            case R.id.radioButton_niujin:
                return HTTPS_NiuJin;
        }
        return HTTPS_NiuJin;
    }

    @Override
    public void onBindViewHolder(@NonNull WordHolder holder, int position) {
        Word word;
        try {
            word = list.get(position);
        } catch (IndexOutOfBoundsException e) {
            return;
        }

        boolean isOffDefault = shp.getBoolean(context.getString(string.shp_editor_defaultCloseWord), false);
        if (isOffDefault) {
            word.setClose(true);
        }

        holder.english.setText(word.getEnglish());
        holder.meaning.setText(word.getMeaning());
        holder.cardView.setBackground(getDrawable(holder.itemView.getContext(), word.getCount()));
        holder.count.setText(word.getCount() > 12 ? "A" : String.valueOf(word.getCount()));
        holder.num.setText(word.getNumInExamination() > 0 ? String.valueOf(word.getNumInExamination()) : "");
        holder.aSwitch.setChecked(word.isClose());
    }

    public Drawable getDrawable(Context context, int count) {
        // 根据用户记忆次数返回背景颜色
        int[] range = {0, 2, 5, 8};
        if (count <= range[0]) {
            return AppCompatResources.getDrawable(context, drawable.shape_white);
        } else if (count <= range[1]) {
            return AppCompatResources.getDrawable(context, drawable.shape_blue);
        } else if (count <= range[2]) {
            return AppCompatResources.getDrawable(context, drawable.shape_green);
        } else if (count <= range[3]) {
            return AppCompatResources.getDrawable(context, drawable.shape_yellow);
        } else {
            return AppCompatResources.getDrawable(context, drawable.shape_purple);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull WordHolder holder) {
        super.onViewAttachedToWindow(holder);
        // 手动刷新卡片序号(序号是界面上的，不是数据库中的)
        holder.id.setText(String.valueOf(holder.getAdapterPosition() + 1));
    }

    public static class WordHolder extends RecyclerView.ViewHolder {
        Chip chip;
        SwitchCompat aSwitch;
        CardView cardView;
        ImageView imageView;
        ConstraintLayout comeInConstraintLayout;
        public TextView id;
        TextView english;
        TextView meaning;
        TextView num;
        TextView count;

        public WordHolder(@NonNull View itemView) {
            super(itemView);
            // 获取item的控件
            num = itemView.findViewById(R.id.num);
            id = itemView.findViewById(R.id.id_no);
            chip = itemView.findViewById(R.id.chip);
            count = itemView.findViewById(R.id.count);
            aSwitch = itemView.findViewById(R.id.switch1);
            english = itemView.findViewById(R.id.english);
            meaning = itemView.findViewById(R.id.meaning);
            cardView = itemView.findViewById(R.id.card_view);
            imageView = itemView.findViewById(R.id.image_view_come);
            comeInConstraintLayout = itemView.findViewById(R.id.constraint_layout_for_web);
        }
    }
}
