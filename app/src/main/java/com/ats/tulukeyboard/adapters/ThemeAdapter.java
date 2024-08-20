package com.ats.tulukeyboard.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ats.tulukeyboard.R;
import com.ats.tulukeyboard.databinding.AdapterThemeBinding;

import java.util.ArrayList;
import java.util.List;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder> {

    private List<Theme> themes = new ArrayList<>();
    private final com.ats.tulukeyboard.adapters.OnThemeClickListener listener;

    public ThemeAdapter(OnThemeClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = AdapterThemeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ThemeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeViewHolder holder, int position) {
        Theme theme = themes.get(position);
        holder.binding.ivTheme.setImageResource(theme.getResource());
        holder.binding.tvTheme.setText(theme.getName());
        if (theme.isSelected())
            holder.binding.cvTheme.setBackgroundResource(R.drawable.bg_selected);
        else
            holder.binding.cvTheme.setBackground(null);
        holder.binding.cvTheme.setOnClickListener(v -> listener.onThemeClicked(theme));
    }

    public void setThemes(List<Theme> themes) {
        this.themes = themes;
        this.notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return themes.size();
    }

    class ThemeViewHolder extends RecyclerView.ViewHolder {

        AdapterThemeBinding binding;

        ThemeViewHolder(AdapterThemeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
