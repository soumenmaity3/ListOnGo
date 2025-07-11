package com.soumen.listongo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.soumen.listongo.SettingActivity.SettingsUtil;

public class OptionActivity extends AppCompatActivity {
MaterialButton btnSignUp,btnSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_option);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        checkNotification();
        btnSignIn=findViewById(R.id.btn_sign_in);
        btnSignUp=findViewById(R.id.btn_sign_up);
        btnSignIn.setOnClickListener(v->{
            Intent intent=new Intent(OptionActivity.this,SignInActivity.class);
            startActivity(intent);
            finish();
        });
        btnSignUp.setOnClickListener(v->{
            Intent intent=new Intent(OptionActivity.this,SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void checkNotification() {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        boolean notificationEnabled = prefs.getBoolean("notification_enable", false);

        if (!notificationEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            1001);
                } else {
                    // Permission already granted – save state
                    prefs.edit().putBoolean("notification_enable", true).apply();
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Save preference when granted
            SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
            prefs.edit().putBoolean("notification_enable", true).apply();
        }
    }



}