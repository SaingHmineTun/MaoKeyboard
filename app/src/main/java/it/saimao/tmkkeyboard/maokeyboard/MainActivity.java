package it.saimao.tmkkeyboard.maokeyboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import it.saimao.tmkkeyboard.R;

public class MainActivity extends AppCompatActivity {
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_main);
    }
}