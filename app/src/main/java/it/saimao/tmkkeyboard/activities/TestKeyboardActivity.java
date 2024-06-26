package it.saimao.tmkkeyboard.activities;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import it.saimao.tmkkeyboard.R;
import it.saimao.tmkkeyboard.databinding.ActivityTestKeyboardBinding;

public class TestKeyboardActivity extends AppCompatActivity {

    private ActivityTestKeyboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    editText.getText().clear();
                }

            }
        });
    }
}