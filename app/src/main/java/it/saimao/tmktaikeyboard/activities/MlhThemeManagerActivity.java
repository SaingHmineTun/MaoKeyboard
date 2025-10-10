package it.saimao.tmktaikeyboard.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.databinding.ActivityMlhThemeManagerBinding;
import it.saimao.tmktaikeyboard.utils.PrefManager;
import it.saimao.tmktaikeyboard.utils.Utils;

public class MlhThemeManagerActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_IMAGE = 1001;
    private static final int MLH_THEME_INDEX = 9; // Based on the theme list position in ChooseThemeActivity

    private ActivityMlhThemeManagerBinding binding;
    private ImageView ivCurrentBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Utils.initLanguage(this);
        binding = ActivityMlhThemeManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadCurrentBackground();
        setupClickListeners();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void initViews() {
        ivCurrentBackground = binding.ivCurrentBackground;
    }

    private void loadCurrentBackground() {
        String backgroundImageUri = PrefManager.getMlhBackgroundUri(this);
        if (backgroundImageUri != null && !backgroundImageUri.isEmpty()) {
            Uri uri = Uri.parse(backgroundImageUri);
            ivCurrentBackground.setImageURI(uri);
        } else {
            ivCurrentBackground.setImageResource(R.drawable.bg_mlh);
        }
    }

    private void setupClickListeners() {
        binding.btnSelectBackground.setOnClickListener(v -> requestImageSelection());
        binding.btnSetKeyboardBackground.setOnClickListener(v -> setKeyboardBackground());
        binding.btnClearBackground.setOnClickListener(v -> clearBackgroundImage());
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
                    Toast.makeText(this, R.string.permission_denied_cannot_select_background, Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        // Save the image URI to preferences
                        getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        PrefManager.saveStringValue(this, "mlh_background_uri", imageUri.toString());
                        PrefManager.setKeyboardTheme(this, MLH_THEME_INDEX);
                        ivCurrentBackground.setImageURI(imageUri);
                        Utils.setThemeChanged(true);
                        Toast.makeText(this, R.string.custom_background_selected, Toast.LENGTH_SHORT).show();
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

    private void setKeyboardBackground() {
        String backgroundImageUri = PrefManager.getMlhBackgroundUri(this);
        if (backgroundImageUri != null && !backgroundImageUri.isEmpty()) {
            PrefManager.setKeyboardTheme(this, MLH_THEME_INDEX);
            Utils.setThemeChanged(true);
            Toast.makeText(this, R.string.keyboard_background_set, Toast.LENGTH_SHORT).show();
        } else {
            // Instead of complaining, use the default bg_mlh.jpg resource
            PrefManager.setKeyboardTheme(this, MLH_THEME_INDEX);
            Utils.setThemeChanged(true);
            Toast.makeText(this, R.string.keyboard_background_set, Toast.LENGTH_SHORT).show();
        }
    }

    private void clearBackgroundImage() {
        PrefManager.saveStringValue(this, "mlh_background_uri", "");
        ivCurrentBackground.setBackgroundResource(R.drawable.bg_mlh);
        PrefManager.setKeyboardTheme(this, 0); // Set to default theme
        Utils.setThemeChanged(true);
        Toast.makeText(this, R.string.background_cleared_default_restored, Toast.LENGTH_SHORT).show();
    }
}