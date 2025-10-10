package it.saimao.tmktaikeyboard.adapters;

import static it.saimao.tmktaikeyboard.utils.Constants.LANGUAGES;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.maokeyboard.MaoKeyboard;
import it.saimao.tmktaikeyboard.utils.KeyboardPreviewView;
import it.saimao.tmktaikeyboard.utils.PrefManager;

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

        private final TextView tvLanguage;
        private final KeyboardPreviewView kpv;
        private final com.google.android.material.card.MaterialCardView cvTheme;
        private String languageKey;

        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLanguage = itemView.findViewById(R.id.tv_language);
            kpv = itemView.findViewById(R.id.kpv_language);
            cvTheme = itemView.findViewById(R.id.cv_theme);
            
            // Add click listener to the whole item
            itemView.setOnClickListener(v -> {
                if (languageKey != null) {
                    boolean isChecked = !PrefManager.isEnabledLanguage(context, languageKey);
                    PrefManager.setEnabledLanguage(context, languageKey, isChecked);
                    
                    // Update UI to reflect selection state
                    updateSelectionState(languageKey, isChecked);
                    
                    listener.onLanguageChecked(languageKey, isChecked);
                }
            });
        }

        public void setData(String key) {
            languageKey = key;
            tvLanguage.setText(getStringResourceByName(key));
            
            // Update selection state
            updateSelectionState(key, PrefManager.isEnabledLanguage(context, key));
            
            // Set keyboard preview
            setKeyboardPreview(key);
        }
        
        private void updateSelectionState(String key, boolean isSelected) {
            if (isSelected) {
                cvTheme.setBackgroundResource(R.drawable.modern_selected_theme);
                // Add a subtle scale effect for selected items
                cvTheme.animate()
                        .scaleX(1.03f)
                        .scaleY(1.03f)
                        .setDuration(200)
                        .start();
            } else {
                cvTheme.setBackground(null);
                cvTheme.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200)
                        .start();
            }
        }

        private void setKeyboardPreview(String key) {
            // Create a keyboard based on the language
            MaoKeyboard keyboard = null;
            
            switch (key) {
                case "en_GB":
                    keyboard = new MaoKeyboard(context, R.xml.english1, "en_GB");
                    break;
                case "mm_MM":
                    keyboard = new MaoKeyboard(context, R.xml.burma1, "mm_MM");
                    break;
                case "shn_MM":
                    keyboard = new MaoKeyboard(context, R.xml.tai1, "shn_MM");
                    break;
                case "taile_MM":
                    keyboard = new MaoKeyboard(context, R.xml.taile_normal, "taile_MM");
                    break;
                case "th_TH":
                    keyboard = new MaoKeyboard(context, R.xml.th_qwerty, "th_TH");
                    break;
                case "khamti_MM":
                    keyboard = new MaoKeyboard(context, R.xml.tai_khamti_qwerty, "khamti_MM");
                    break;
                case "tham_TH":
                    keyboard = new MaoKeyboard(context, R.xml.tai_tham_qwerty, "tham_TH");
                    break;
                case "tai_lue_TH":
                    keyboard = new MaoKeyboard(context, R.xml.tai_lue_qwerty, "tai_lue_TH");
                    break;
                case "tai_dam_VN":
                    keyboard = new MaoKeyboard(context, R.xml.tai_dam_qwerty, "tai_dam_VN");
                    break;
                case "tai_IN":
                    keyboard = new MaoKeyboard(context, R.xml.tai_ahom_normal, "tai_IN");
                    break;
                default:
                    // For other languages, we'll use a default keyboard or hide the preview
                    // You can add more cases for other languages as needed
                    kpv.setVisibility(View.GONE);
                    return;
            }
            
            // Set the keyboard and use dark theme (index 0)
            kpv.setKeyboard(keyboard);
            kpv.setThemeIndex(0); // Dark theme
            kpv.setVisibility(View.VISIBLE);
        }
    }
}