package it.saimao.tmkkeyboard.maoconverter;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import it.saimao.tmkkeyboard.R;
import it.saimao.tmkkeyboard.utils.Utils;

public class MaoConverterService extends Service {

    ClipboardManager clipboard;
    String copiedString;
    boolean isRunning;

    private final Set<AlertDialog> alerts = new HashSet<>();

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

//                NotificationCompat.Builder builder = new NotificationCompat.Builder(MaoConverterService.this)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentTitle("Mao Converter")
//                        .setContentText("Click here to view the converted text!")
//                        .setChannelId("mao_id");
//                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
////                    NotificationChannel channel = new NotificationChannel("mao_id", "Mao ID", NotificationManager.IMPORTANCE_DEFAULT);
////                    channel.enableLights(true);
////                    channel.setVibrationPattern(new long[] { 100, 100, 100, 100, 100 });
////                    channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
////                    channel.setLightColor(Color.GREEN);
////                    manager.createNotificationChannel(channel);
////                }

//                Intent i = new Intent(MaoConverterService.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//                Bundle bundle = new Bundle();
//                bundle.putString("text", clipboard.getText().toString());
////                i.putExtra("text", clipboard.getText());
//                i.putExtras(bundle);
//                PendingIntent pi = PendingIntent.getActivity(MaoConverterService.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(pi);
//                manager.notify(0, builder.build());
//            ZawgyiDetector zawgyiDetector = new ZawgyiDetector(MaoConverterService.this);
            copiedString = clipboard.getText().toString();
//            if (zawgyiDetector.isZawgyiTai(copiedString) || zawgyiDetector.getZawgyiProbability(copiedString) > 0.8) {
//                if (isFromFB()) {
//                    displayAlert(MaoZgUniConverter.zg2uni(MaoZgUniConverter.uni2zg(copiedString)));
//                } else {
//                    displayAlert(MaoZgUniConverter.zg2uni(copiedString));
//                }
//            } else {
//                displayAlert(MaoZgUniConverter.uni2zg(copiedString));
//            }
            if (Utils.isEnabledConvertFromFb(MaoConverterService.this, "convertFromFb")) {
                displayAlert(MaoZgUniConverter.zg2uni(MaoZgUniConverter.uni2zg(copiedString)));
            } else {
                displayAlert(MaoZgUniConverter.zg2uni(copiedString));
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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
        // close any previous alerts
        for (Iterator<AlertDialog> i = alerts.iterator(); i.hasNext(); ) {
            AlertDialog dialog = i.next();
            try {
                dialog.cancel();
            } catch (Exception nop) {
            }
            i.remove();
        }

        // Mao Custom Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.mao_converter, null, false);
        builder.setView(dialogView);

        final AlertDialog alert = builder.create();
        alerts.add(alert);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else {
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }
        alert.show();

        final Button btnClose = alert.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
        final Button btnCopy = alert.findViewById(R.id.btnCopy);
        final TextView tvContent = alert.findViewById(R.id.tvContent);
        tvContent.setText(sms);
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.setStopCopyDialog(true);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", tvContent.getText());
                clipboard.setPrimaryClip(clip);
//                Toast.makeText(MaoConverterService.this, "Copied Successfully!", Toast.LENGTH_LONG).show();
                btnCopy.setBackground(ContextCompat.getDrawable(MaoConverterService.this, R.drawable.green_theme_keybackground));
            }
        });
        CheckBox cbFromFB = alert.findViewById(R.id.cbFromFB);
        cbFromFB.setChecked(Utils.isEnabledConvertFromFb(MaoConverterService.this, "convertFromFb"));
        cbFromFB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    String copiedString = clipboard.getText().toString();
                    tvContent.setText(MaoZgUniConverter.zg2uni(MaoZgUniConverter.uni2zg(copiedString)));
                } else {
                    tvContent.setText(MaoZgUniConverter.zg2uni(copiedString));
                }
                Utils.setEnabledConvertFromFb(MaoConverterService.this, "convertFromFb", b);
            }
        });
    }

}
