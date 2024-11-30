package it.saimao.tmktaikeyboard.activities;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.saimao.tmktaikeyboard.databinding.ActivityAboutUsBinding;
import it.saimao.tmktaikeyboard.utils.Utils;


public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.initLanguage(this);
        ActivityAboutUsBinding binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.lyEmail.setOnClickListener(v -> onItemClick(0));
        binding.lyFacebook.setOnClickListener(v -> onItemClick(1));
        binding.lyGithub.setOnClickListener(v -> onItemClick(2));
        binding.lyPlayStore.setOnClickListener(v -> onItemClick(3));
    }

    public void onItemClick(int i) {
        Intent intent;
        if (i == 1) {

            try {
                getPackageManager().getPackageInfo("com.facebook.katana", 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/529740996878692"));
            } catch (Exception e) {

                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/529740996878692"));
            }
            startActivity(intent);
        } else if (i == 0) {
            String to = "tmk.muse@gmail.com";
            intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Choose an Email client :"));
        } else if (i == 2) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://itsaimao.wordpress.com/")));
        } else if (i == 3) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=it.saimao.tmktaikeyboard")));
        }
    }
}
