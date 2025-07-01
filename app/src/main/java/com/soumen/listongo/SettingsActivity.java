package com.soumen.listongo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.soumen.listongo.SettingActivity.AboutActivity;
import com.soumen.listongo.SettingActivity.ContactSupportActivity;
import com.soumen.listongo.SettingActivity.FeedbackActivity;
import com.soumen.listongo.SettingActivity.PrivacyPolicyActivity;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.biometric.BiometricPrompt;
import androidx.biometric.BiometricManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingsActivity extends AppCompatActivity {
    private static final int REQUEST_CODE=1000;
    MaterialButton btnContact,btnPrivacy,btnAbout,btnFeedback,btnDelete,btnLogout;
    @SuppressLint({"MissingInflatedId","WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Apply system insets (your existing code)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextInputEditText userName = findViewById(R.id.edit_username);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        MaterialTextView text_version = findViewById(R.id.text_version);


        btnAbout=findViewById(R.id.btn_about);
        btnContact=findViewById(R.id.btn_support);
        btnPrivacy=findViewById(R.id.btn_privacy);
        btnFeedback=findViewById(R.id.btn_feedback);

        btnContact.setOnClickListener(v->startActivity(new Intent(this, ContactSupportActivity.class)));
        btnPrivacy.setOnClickListener(v->startActivity(new Intent(this, PrivacyPolicyActivity.class)));
        btnFeedback.setOnClickListener(v->startActivity(new Intent(this, FeedbackActivity.class)));
        btnAbout.setOnClickListener(v->startActivity(new Intent(this, AboutActivity.class)));

        btnDelete=findViewById(R.id.btn_delete_account);
        btnLogout=findViewById(R.id.btn_logout);



        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        long versionCode = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            versionCode = packageInfo.getLongVersionCode();
        }

        text_version.setText("App Version: "+ packageInfo.versionName+"." + versionCode);

        Intent intent2 = getIntent();
        String name = intent2.getStringExtra("userName");
        String email = intent2.getStringExtra("userEmail");
        Long userId = intent2.getLongExtra("UserId", 1);

        MaterialAutoCompleteTextView spinnerTheme = findViewById(R.id.spinner_theme);


        String[] themeOptions = getResources().getStringArray(R.array.theme_options);

        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, themeOptions);

        spinnerTheme.setAdapter(themeAdapter);


        userName.setText(name);
        userName.setEnabled(false);

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);

        spinnerTheme.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTheme = parent.getItemAtPosition(position).toString();
            prefs.edit().putString("theme", selectedTheme.toLowerCase()).apply();
            spinnerTheme.setText(selectedTheme, false);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("email", email);
            intent.putExtra("UserId", userId);
            intent.putExtra("userName", name);
            startActivity(intent);
            finish();
        });


        MaterialSwitch biometricSwitch = findViewById(R.id.switch_biometric);
        SharedPreferences prefs2 = getSharedPreferences("app_settings", MODE_PRIVATE);
        boolean biometricEnabled = prefs2.getBoolean("biometric_enabled", false);
        biometricSwitch.setChecked(biometricEnabled);

        biometricSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                BiometricManager biometricManager = BiometricManager.from(this);
                switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                    case BiometricManager.BIOMETRIC_SUCCESS:
                        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                                .setTitle("Enable Biometric Login")
                                .setSubtitle("Authenticate using your fingerprint")
                                .setNegativeButtonText("Cancel")
                                .build();

                        BiometricPrompt biometricPrompt = new BiometricPrompt(
                                this,
                                ContextCompat.getMainExecutor(this),
                                new BiometricPrompt.AuthenticationCallback() {
                                    @Override
                                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                                        super.onAuthenticationSucceeded(result);
                                        prefs2.edit().putBoolean("biometric_enabled", true).apply();
                                        Toast.makeText(getApplicationContext(), "Biometric enabled", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onAuthenticationFailed() {
                                        super.onAuthenticationFailed();
                                        Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                                        biometricSwitch.setChecked(false);
                                    }

                                    @Override
                                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                                        super.onAuthenticationError(errorCode, errString);
                                        Toast.makeText(getApplicationContext(), "Error: " + errString, Toast.LENGTH_SHORT).show();
                                        biometricSwitch.setChecked(false);
                                    }
                                });

                        biometricPrompt.authenticate(promptInfo);
                        break;
                    default:
                        Toast.makeText(this, "Biometric not available or not enrolled", Toast.LENGTH_SHORT).show();
                        biometricSwitch.setChecked(false);
                }

            } else {
                prefs2.edit().putBoolean("biometric_enabled", false).apply();
            }
        });



        MaterialSwitch notification = findViewById(R.id.switch_notifications);
        SharedPreferences prefs3 = getSharedPreferences("app_settings", MODE_PRIVATE);

// Load and set saved switch state
        boolean notificationEnabled = prefs3.getBoolean("notification_enable", false);
        notification.setChecked(notificationEnabled);

        notification.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                REQUEST_CODE);
                    } else {
                        enableNotifications();
                    }
                } else {
                    enableNotifications();
                }
            } else {
                prefs3.edit().putBoolean("notification_enable", false).apply();
                Toast.makeText(this, "Push notifications disabled", Toast.LENGTH_SHORT).show();
            }
        });


        btnLogout.setOnClickListener(v->startActivity(new Intent(SettingsActivity.this, OptionActivity.class)));

        btnDelete.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.delete_account_dailog);

            MaterialButton btnYes = dialog.findViewById(R.id.delete_button);
            MaterialButton btnNo = dialog.findViewById(R.id.cancel_button);

            btnYes.setOnClickListener(vi -> {
                dialog.dismiss();

                Dialog dialog1 = new Dialog(this);
                dialog1.setContentView(R.layout.enter_password);

                MaterialButton btnSubmit = dialog1.findViewById(R.id.submitButton);
                TextInputEditText edtPass = dialog1.findViewById(R.id.enter_password);

                btnSubmit.setOnClickListener(view -> {
                    String password = edtPass.getText() != null ? edtPass.getText().toString().trim() : "";

                    if (password.isEmpty()) {
                        edtPass.setError("Enter Password");
                        return;
                    }

                    deleteAccount(userId, email, password);
                    dialog1.dismiss();
                });

                dialog1.show();
            });

            btnNo.setOnClickListener(vie -> {
                dialog.dismiss();
            });

            dialog.show();
        });


    }

    private void enableNotifications() {
        SharedPreferences prefs3 = getSharedPreferences("app_settings", MODE_PRIVATE);
        prefs3.edit().putBoolean("notification_enable", true).apply();
        pushNotify();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableNotifications();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void pushNotify() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }

            String token = task.getResult();
            if (token != null) {
                Log.d("FCM_Token", "Token: " + token);
            } else {
                Log.w("FCM_Token", "Token is null");
            }
        });

        Toast.makeText(this, "Push notifications enabled", Toast.LENGTH_SHORT).show();
    }

    public void deleteAccount(Long userId, String email, String password) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> deleteCall = apiService.deleteAccount(userId, email, password);

        deleteCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Account deleted successfully
                    Toast.makeText(SettingsActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SettingsActivity.this, SignUpActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle error response
                    Toast.makeText(SettingsActivity.this, "Failed to delete account: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
