package it.saimao.tmktaikeyboard.maoconverter;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.databinding.DialogPopupConverterBinding;
import it.saimao.tmktaikeyboard.utils.Utils;

public class PopupConverterService extends Service {

    private ClipboardManager clipboard;
    private String copiedString;
    boolean isRunning;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = false;
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }

    private final ClipboardManager.OnPrimaryClipChangedListener listener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {

            if (Utils.isStopCopyDialog()) {
                Utils.setStopCopyDialog(false);
                return;
            }
            copiedString = clipboard.getText().toString();
            if (Utils.isEnabledConvertFromFb(PopupConverterService.this, "convertFromFb")) {
                displayAlert(MaoZgUniConverter.zg2uni(MaoZgUniConverter.uni2zg(copiedString)));
            } else {
                displayAlert(MaoZgUniConverter.zg2uni(copiedString));
            }
        }
    };


    private AlertDialog alertDialog;
    private DialogPopupConverterBinding binding;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // Mao Custom Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        binding = DialogPopupConverterBinding.inflate(inflater);
        builder.setView(binding.getRoot()).setCancelable(false);
        alertDialog = builder.create();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else {
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }

        binding.btnClose.setOnClickListener(view -> alertDialog.dismiss());
        binding.btnCopy.setOnClickListener(view -> {
            Utils.setStopCopyDialog(true);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", binding.tvContent.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copy success!", Toast.LENGTH_SHORT).show();
        });
        binding.cbFromFB.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                String copiedString = clipboard.getText().toString();
                binding.tvContent.setText(MaoZgUniConverter.zg2uni(MaoZgUniConverter.uni2zg(copiedString)));
            } else {
                binding.tvContent.setText(MaoZgUniConverter.zg2uni(copiedString));
            }
            Utils.setEnabledConvertFromFb(PopupConverterService.this, "convertFromFb", b);
        });

        if (!isRunning) {
            isRunning = true;
            clipboard.addPrimaryClipChangedListener(listener);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        clipboard.removePrimaryClipChangedListener(listener);
        super.onDestroy();
    }

    private void displayAlert(String sms) {
        binding.tvContent.setText(sms);
        binding.cbFromFB.setChecked(Utils.isEnabledConvertFromFb(PopupConverterService.this, "convertFromFb"));
        alertDialog.show();

    }

}
