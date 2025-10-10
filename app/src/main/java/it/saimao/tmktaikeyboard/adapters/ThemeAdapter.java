package it.saimao.tmktaikeyboard.adapters;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.databinding.AdapterThemeBinding;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder> {

    private List<Theme> themes = new ArrayList<>();
    private final Theme.OnThemeClickListener listener;

    public ThemeAdapter(Theme.OnThemeClickListener listener) {
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
        if (theme.isSelected()) {
            holder.binding.cvTheme.setBackgroundResource(R.drawable.modern_selected_theme);
            // Add a subtle pulse animation for selected items
            holder.binding.cvTheme.animate()
                    .scaleX(1.03f)
                    .scaleY(1.03f)
                    .setDuration(200)
                    .start();
        } else {
            holder.binding.cvTheme.setBackground(null);
            holder.binding.cvTheme.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start();
        }
        
        // Add fade-in animation for items
        holder.binding.cvTheme.setAlpha(0f);
        holder.binding.cvTheme.animate()
                .alpha(1f)
                .setStartDelay(position * 50)
                .setDuration(300)
                .start();
        
        // Add animation for better visual feedback
        holder.binding.cvTheme.setOnClickListener(v -> {
            // Add a subtle scale animation when clicked
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 0.9f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 0.9f);
            scaleDownX.setDuration(150);
            scaleDownY.setDuration(150);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            scaleDownX.start();
            scaleDownY.start();
            
            v.postDelayed(() -> {
                ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f);
                ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f);
                scaleUpX.setDuration(150);
                scaleUpY.setDuration(150);
                scaleUpX.setInterpolator(new DecelerateInterpolator());
                scaleUpY.setInterpolator(new DecelerateInterpolator());
                scaleUpX.start();
                scaleUpY.start();
                
                listener.onThemeClicked(theme);
            }, 150);
        });
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
