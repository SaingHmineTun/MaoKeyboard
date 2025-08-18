package it.saimao.tmktaikeyboard.activities;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.databinding.ActivityTestKeyboardBinding;
import it.saimao.tmktaikeyboard.utils.Utils;

public class TestKeyboardActivity extends AppCompatActivity {

    private ActivityTestKeyboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Utils.initLanguage(this);
        binding = ActivityTestKeyboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initListener();
    }

    private void initListener() {
        binding.btExit.setOnClickListener(v -> finish());
        binding.btClear.setOnClickListener(v -> {
            for (int i = 0; i < binding.llTest.getChildCount(); i++) {
                if (binding.llTest.getChildAt(i) instanceof TextInputLayout layout) {
                    EditText editText = layout.getEditText();
                    if (editText != null) {
                        editText.getText().clear();
                    }
                }

            }
        });
    }
}