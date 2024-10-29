package com.ats.tulukeyboard.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ats.tulukeyboard.R;
import com.ats.tulukeyboard.databinding.ActivityAboutBinding;
import com.ats.tulukeyboard.utils.Utils;


public class AboutActivity extends AppCompatActivity {


    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUi();
    }

    private void initUi() {
//        Utils.justify(binding.tvAbout);
    }
}
