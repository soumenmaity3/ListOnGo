package com.soumen.listongo;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.soumen.listongo.SettingActivity.SettingsUtil;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.biometric.BiometricPrompt;


public class SignInActivity extends AppCompatActivity {
    TextInputEditText edtEmail, edtPassword;
    MaterialButton btnSignIn, btnFingerPrint;
    MaterialTextView txtSignUp, txtForgoPass;
    MaterialCheckBox checkBoxReminder;
    ProgressBar signProgress;

    String savedEmail, savedPassword, savedUsername;
    Long savedUserId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        // Insets etc...
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI bindings
        edtEmail = findViewById(R.id.email_input);
        edtPassword = findViewById(R.id.password_input);
        btnSignIn = findViewById(R.id.sign_in_button);
        txtSignUp = findViewById(R.id.sign_up_link);
        txtForgoPass = findViewById(R.id.for_got_pass);
        btnFingerPrint = findViewById(R.id.sign_in_finger_button);
        checkBoxReminder = findViewById(R.id.checkSaveme);
        signProgress = findViewById(R.id.signProgress);

        // Check biometric setting and trigger automatically
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        boolean biometricEnabled = prefs.getBoolean("biometric_enabled", false);
        boolean rememberMe = prefs.getBoolean("remember_me", false);
        checkBoxReminder.setChecked(rememberMe);

        SecurePrefsHelper prefsHelper = new SecurePrefsHelper(this);
        savedEmail = prefsHelper.getEmail();
        savedPassword = prefsHelper.getPassword();
        savedUserId = prefsHelper.getUserId();
        savedUsername = prefsHelper.getUsername();
        if (biometricEnabled) {
            if (prefsHelper.getEmail() != null && prefsHelper.getPassword() != null && prefsHelper.getUserId() != null) {
                BiometricData();
            }
        }
        if (rememberMe) {
            edtEmail.setText(savedEmail);
            edtPassword.setText(savedPassword);
        }
        // Manual button click handlers
        btnSignIn.setOnClickListener(v ->{
            signProgress.setVisibility(VISIBLE);
            apiResponse();});
        btnFingerPrint.setOnClickListener(v ->BiometricData());
        txtSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });
        txtForgoPass.setOnClickListener(v -> startActivity(new Intent(this, ForgotPassword.class)));
    }


    public void userNameById(Long userId, UsernameCallback callback) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> userNameF = apiService.fetchUsername(userId);
        userNameF.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String fetchedUsername = response.body().string();
                        Log.d("username", "Fetched username: " + fetchedUsername);
                        callback.onUsernameFetched(fetchedUsername);
                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onUsernameFetched(null);
                    }
                } else {
                    Log.e("username", "Empty or failed response");
                    callback.onUsernameFetched(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Toast.makeText(SignInActivity.this, "NetWork Error", Toast.LENGTH_SHORT).show();
                callback.onUsernameFetched(null);
            }
        });
    }


    //api call
    public void apiResponse() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        LogInUserModel logInUserModel = new LogInUserModel(email, password);

        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.loginUser(logInUserModel);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String result;
                    try {
                        result = response.body().string().trim();
                        Long userId = Long.parseLong(result);
                        userNameById(userId, username -> {
                            SecurePrefsHelper securePrefsHelper = new SecurePrefsHelper(SignInActivity.this);
                            securePrefsHelper.saveCredentials(email, password, userId, username);

                            SharedPreferences.Editor editor = getSharedPreferences("app_settings", MODE_PRIVATE).edit();
                            editor.putBoolean("remember_me", checkBoxReminder.isChecked());
                            editor.apply();

                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            intent.putExtra("UserId", userId);
                            intent.putExtra("userName", username);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            signProgress.setVisibility(GONE);
                            finish();
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Toast.makeText(SignInActivity.this, "Login failed. Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SignInActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    public void BiometricData() {
        signProgress.setVisibility(VISIBLE);

        if (savedEmail == null || savedPassword == null || savedUserId == null) {
            Toast.makeText(this, "No saved credentials found", Toast.LENGTH_SHORT).show();
            return;
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(SignInActivity.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);

                        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
                        LogInUserModel model = new LogInUserModel(savedEmail, savedPassword);

                        apiService.loginUser(model).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    try {
                                        String result = response.body().string().trim();
                                        Long userId = Long.parseLong(result);

                                        userNameById(userId, username -> {
                                            signProgress.setVisibility(GONE);
                                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                            intent.putExtra("email", savedEmail);
                                            intent.putExtra("UserId", userId);
                                            intent.putExtra("userName", username);
                                            startActivity(intent);
                                            finish();
                                        });

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        signProgress.setVisibility(GONE);
                                        Toast.makeText(SignInActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    SecurePrefsHelper securePrefsHelper = new SecurePrefsHelper(SignInActivity.this);
                                    securePrefsHelper.clear();
                                    signProgress.setVisibility(GONE);
                                    Toast.makeText(SignInActivity.this, "Credentials invalid. Please login manually.", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(SignInActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                signProgress.setVisibility(GONE);
                            }
                        });
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        Toast.makeText(SignInActivity.this, "Auth error: " + errString, Toast.LENGTH_SHORT).show();
                        signProgress.setVisibility(GONE);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        Toast.makeText(SignInActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        signProgress.setVisibility(GONE);
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login with Fingerprint")
                .setSubtitle("Authenticate using biometrics")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

}