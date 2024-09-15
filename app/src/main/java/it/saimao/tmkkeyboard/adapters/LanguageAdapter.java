package it.saimao.tmkkeyboard.adapters;

import static it.saimao.tmkkeyboard.utils.Constants.LANGUAGES;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.saimao.tmkkeyboard.R;
import it.saimao.tmkkeyboard.utils.PrefManager;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {
    private List<String> languages;
    private Context context;
    private OnLanguageCheckedListener listener;

    public LanguageAdapter(Context context, OnLanguageCheckedListener listener) {
        languages = List.of(
                "en_GB",
                "mm_MM",
                "shn_MM",
                "taile_MM",
                "th_TH",
                "khamti_MM",
                "tham_TH",
                "tai_lue_TH",
                "tai_dam_VN",
                "tai_IN"
        );
        this.context = context;
        this.listener = listener;
    }

    public void refresh() {
        this.notifyDataSetChanged();
    }

    public interface OnLanguageCheckedListener {
        void onLanguageChecked(String key, boolean isChecked);
    }


    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(parent.getContext());
        var view = inflater.inflate(R.layout.adapter_language, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        var key = languages.get(position);
        holder.setData(key);

    }

    private String getStringResourceByName(String string) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(string, "string", packageName);
        return context.getString(resId);
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    class LanguageViewHolder extends RecyclerView.ViewHolder {

        private final CheckBox cb;
        private final ImageView iv;

        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            cb = itemView.findViewById(R.id.cb);
            iv = itemView.findViewById(R.id.iv);
        }

        public void setData(String key) {
            cb.setText(getStringResourceByName(key));
            cb.setChecked(PrefManager.isEnabledLanguage(context, key));
            iv.setBackgroundResource(getKeyboardImageFromKey(key));
            if (key.equals("en_GB")) cb.setEnabled(false);
            else {
                cb.setOnCheckedChangeListener((compoundButton, b) -> {
                    listener.onLanguageChecked(key, b);
                });
            }
        }


        public int getKeyboardImageFromKey(String key) {
            if (key.equals(LANGUAGES[0])) return R.drawable.kb_english;
            else if (key.equals(LANGUAGES[1])) return R.drawable.kb_burma;
            else if (key.equals(LANGUAGES[2])) return R.drawable.kb_tai;
            else if (key.equals(LANGUAGES[3])) return R.drawable.kb_taile;
            else if (key.equals(LANGUAGES[4])) return R.drawable.kb_thai;
            else if (key.equals(LANGUAGES[5])) return R.drawable.kb_taikhamti;
            else if (key.equals(LANGUAGES[6])) return R.drawable.kb_taitham;
            else if (key.equals(LANGUAGES[7])) return R.drawable.kb_tailue;
            else if (key.equals(LANGUAGES[8])) return R.drawable.kb_taidam;
            else return R.drawable.kb_ahom;
        }

    }
}
