package it.saimao.tmktaikeyboard.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
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
import it.saimao.tmktaikeyboard.maokeyboard.MaoKeyboard;
import it.saimao.tmktaikeyboard.utils.PrefManager;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder> {

    private List<Theme> themes = new ArrayList<>();
    private final Theme.OnThemeClickListener listener;
    private Context context;

    public ThemeAdapter(Theme.OnThemeClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = AdapterThemeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        this.context = parent.getContext();
        return new ThemeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeViewHolder holder, int position) {
        Theme theme = themes.get(position);

        // Set appropriate background based on theme
        setThemeBackground(holder, position);

        // Create a keyboard preview
        MaoKeyboard keyboard = new MaoKeyboard(context, R.xml.english1);
        holder.binding.kpvTheme.setKeyboard(keyboard);
        holder.binding.kpvTheme.setThemeIndex(position);
// Hide the image view since we're using the keyboard preview
        holder.binding.ivTheme.setVisibility(View.GONE);
        holder.binding.kpvTheme.setVisibility(View.VISIBLE);

        // For TMK theme (position 9), show the custom background image if available
        if (position == 9) {
            String backgroundImageUri = PrefManager.getCustomBackgroundUri(context);
            if (backgroundImageUri != null && !backgroundImageUri.isEmpty()) {
                try {
                    holder.binding.ivThemeBackground.setImageURI(Uri.parse(backgroundImageUri));
                    holder.binding.ivThemeBackground.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    holder.binding.ivThemeBackground.setVisibility(View.GONE);
                }
            } else {
                holder.binding.ivThemeBackground.setVisibility(View.VISIBLE);
                holder.binding.ivThemeBackground.setImageResource(R.drawable.bg_custom_default);
            }
        } else {
            holder.binding.ivThemeBackground.setVisibility(View.GONE);
        }

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

    private void setThemeBackground(ThemeViewHolder holder, int position) {
        switch (position) {
            case 0: // Dark theme
                holder.binding.flThemeItem.setBackgroundColor(context.getResources().getColor(R.color.key_dark_glow));
                holder.binding.kpvTheme.setBackgroundColor(context.getResources().getColor(R.color.key_dark_glow));
                break;
            case 1: // Green theme
                holder.binding.flThemeItem.setBackgroundColor(context.getResources().getColor(R.color.key_success_glow));
                holder.binding.kpvTheme.setBackgroundColor(context.getResources().getColor(R.color.key_success_glow));
                break;
            case 2: // Blue theme
                holder.binding.kpvTheme.setBackgroundResource(R.drawable.enhanced_blue_theme_keybackground);
                break;
            case 3: // Sunset theme
                holder.binding.kpvTheme.setBackgroundResource(R.drawable.enhanced_sunset_theme_keybackground);
                break;
            case 4: // Gold theme
                holder.binding.kpvTheme.setBackgroundResource(R.drawable.enhanced_gold_theme_keybackground);
                break;
            case 5: // Pink theme
                holder.binding.kpvTheme.setBackgroundResource(R.drawable.enhanced_pink_theme_keybackground);
                break;
            case 6: // Violet theme
                holder.binding.kpvTheme.setBackgroundResource(R.drawable.enhanced_violet_theme_keybackground);
                break;
            case 7: // Scarlet theme
                holder.binding.kpvTheme.setBackgroundResource(R.drawable.enhanced_scarlet_theme_keybackground);
                break;
            case 8: // Neon theme
                holder.binding.kpvTheme.setBackgroundResource(R.drawable.enhanced_neon_theme_keybackground);
                break;
            case 9: // Custom theme
                holder.binding.kpvTheme.setBackgroundResource(R.drawable.enhanced_custom_theme_keybackground);
                break;
            default:
                holder.binding.kpvTheme.setBackgroundResource(R.drawable.modern_theme_background);
                break;
        }
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