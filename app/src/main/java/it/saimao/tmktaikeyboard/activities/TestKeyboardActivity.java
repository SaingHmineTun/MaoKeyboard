package it.saimao.tmktaikeyboard.activities;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.databinding.ActivityTestKeyboardBinding;
import it.saimao.tmktaikeyboard.utils.Utils;

public class TestKeyboardActivity extends AppCompatActivity {

    private ActivityTestKeyboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.initLanguage(this);
        binding = ActivityTestKeyboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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