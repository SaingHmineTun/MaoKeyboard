package it.saimao.tmktaikeyboard.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.adapters.Theme;
import it.saimao.tmktaikeyboard.adapters.ThemeAdapter;
import it.saimao.tmktaikeyboard.databinding.ActivityChooseThemeBinding;
import it.saimao.tmktaikeyboard.utils.PrefManager;
import it.saimao.tmktaikeyboard.utils.Utils;

public class ChooseThemeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_IMAGE = 1001;
    private static final int CUSTOM_THEME_INDEX = 9; // Based on the theme list position

    private ActivityChooseThemeBinding binding;
    private ThemeAdapter themeAdapter;
    private List<Theme> themes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Utils.initLanguage(this);
        binding = ActivityChooseThemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void initUi() {
        themes = List.of(
                new Theme(getString(R.string.dark)),
                new Theme(getString(R.string.green)),
                new Theme(getString(R.string.blue)),
                new Theme(getString(R.string.sunset)),
                new Theme(getString(R.string.gold)),
                new Theme(getString(R.string.pink)),
                new Theme(getString(R.string.violet)),
                new Theme(getString(R.string.scarlet)),
                new Theme("Neon"),
                new Theme(getString(R.string.custom))
        );
        themeAdapter = new ThemeAdapter(theme -> {
            var selected = 0;
            for (int i = 0; i < themes.size(); i++) {
                if (theme.equals(themes.get(i))) {
                    selected = i;
                    break;
                }
            }

            // Check if Custom theme is selected
            if (selected == CUSTOM_THEME_INDEX) {
                // Open Custom Theme Manager Activity
                Intent intent = new Intent(ChooseThemeActivity.this, CustomThemeManagerActivity.class);
                startActivity(intent);
            } else {
                PrefManager.setKeyboardTheme(this, selected);
                refreshThemes();
                Utils.setThemeChanged(true);
            }
        });
        binding.rvThemes.setAdapter(themeAdapter);
        binding.rvThemes.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        refreshThemes();
    }

    private void requestImageSelection() {
// Check for permission first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                openImagePicker();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                openImagePicker();
            }
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openImagePicker();
                } else {
                    Toast.makeText(this, "Permission denied. Cannot select custom background.", Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        // Save the image URI to preferences
                        getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        PrefManager.saveStringValue(this, "custom_background_uri", imageUri.toString());
                        PrefManager.setKeyboardTheme(this, CUSTOM_THEME_INDEX);
                        refreshThemes();
                        Utils.setThemeChanged(true);
                        Toast.makeText(this, "Custom background selected!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        imagePickerLauncher.launch(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshThemes();
    }

    private void refreshThemes() {
        ArrayList<Theme> themes = new ArrayList<>(this.themes);
        int selected = PrefManager.getKeyboardTheme(this);
        Theme selectedTheme = themes.get(selected);
        Theme newTheme = new Theme(selectedTheme);
        newTheme.setSelected(true);
        themes.set(selected, newTheme);
        themeAdapter.setThemes(themes);
    }
}