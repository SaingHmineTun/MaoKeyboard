package it.saimao.tmktaikeyboard.activities;

import static it.saimao.tmktaikeyboard.utils.Constants.FONT_CONVERTER;
import static it.saimao.tmktaikeyboard.utils.Constants.SHAN_TRANSLIT;
import static it.saimao.tmktaikeyboard.utils.Constants.TAILE_CONVERTER;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import androidx.appcompat.app.AppCompatActivity;

import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.databinding.ActivityEnableConvertersBinding;
import it.saimao.tmktaikeyboard.utils.PrefManager;

public class EnableConvertersActivity extends AppCompatActivity {

    private ActivityEnableConvertersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEnableConvertersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUi();
    }

    private void initUi() {

        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);

        /* TMK Font Converter */
        SpannableString fontConverter = new SpannableString(getString(R.string.enable_font_converter_desc));
        fontConverter.setSpan(bss, fontConverter.toString().indexOf("ENTER"), fontConverter.toString().indexOf("ENTER") + 5, SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
        binding.tvFontConverter.setText(fontConverter);
        binding.btFontConverter.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=it.saimao.tmkfontconverter"))));
        binding.cbFontConverter.setChecked(PrefManager.isEnabledLanguage(this, FONT_CONVERTER));
        binding.cbFontConverter.setOnCheckedChangeListener((compoundButton, b) -> PrefManager.setEnabledLanguage(this, FONT_CONVERTER, b));

        /* TMK Tai Le Converter */
        SpannableString taiLeConverter = new SpannableString(getString(R.string.enable_taile_converter_desc));
        taiLeConverter.setSpan(bss, taiLeConverter.toString().indexOf("123"), taiLeConverter.toString().indexOf("123") + 3, SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
        binding.tvTaileConverter.setText(taiLeConverter);
        binding.btTaileConverter.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=it.saimao.tmktaileconverter"))));
        binding.cbTaileConverter.setChecked(PrefManager.isEnabledLanguage(this, TAILE_CONVERTER));
        binding.cbTaileConverter.setOnCheckedChangeListener((compoundButton, b) -> PrefManager.setEnabledLanguage(this, TAILE_CONVERTER, b));


        /* Shan Translit */
        SpannableString shanTranslit = new SpannableString("This app can help you convert Tai to English and vice versa. If enabled, long press the SMILE key will trigger this converter.");
        shanTranslit.setSpan(bss, shanTranslit.toString().indexOf("SMILE"), shanTranslit.toString().indexOf("SMILE") + 5, SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
        binding.tvShanTranslit.setText(shanTranslit);
        binding.btShanTranslit.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=it.saimao.shantranslit"))));
        binding.cbShanTranslit.setChecked(PrefManager.isEnabledLanguage(this, SHAN_TRANSLIT));
        binding.cbShanTranslit.setOnCheckedChangeListener((compoundButton, b) -> PrefManager.setEnabledLanguage(this, SHAN_TRANSLIT, b));


    }
}