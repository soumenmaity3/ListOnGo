package com.soumen.listongo;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.soumen.listongo.SettingActivity.SettingsUtil;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    private MaterialButtonToggleGroup toggleGroup;
    private ViewFlipper passwordSwitcher;
    private MaterialButton btnForgot, btnGetOtp;
    private TextInputEditText editTextEmail, editTextOtp, editTextPassword;
    private TextView txtLogin;

    private String storeOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        toggleGroup = findViewById(R.id.toggleGroup);
        passwordSwitcher = findViewById(R.id.passwordSwitcher);
        btnForgot = findViewById(R.id.btnResetPas);
        btnGetOtp = findViewById(R.id.btnGetOtp);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextOtp = findViewById(R.id.editTextOtp);
        editTextPassword = findViewById(R.id.editTextOldPassword);
        txtLogin = findViewById(R.id.txtLogin);

        // Default selection = OTP
        toggleGroup.check(R.id.btnOtp);
        passwordSwitcher.setDisplayedChild(1);

        // Toggle between Old Password / OTP
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnPassword) {
                    passwordSwitcher.setDisplayedChild(0); // Show Old Password
                } else if (checkedId == R.id.btnOtp) {
                    passwordSwitcher.setDisplayedChild(1); // Show OTP
                }
            }
        });

        // Get OTP
        btnGetOtp.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            if (email.isEmpty()) {
                editTextEmail.setError("Enter email first.");
                return;
            }
            generateOtp();
            sendAndStore(email, storeOTP);

            // Disable button for 15s
            btnGetOtp.setEnabled(false);
            new CountDownTimer(15000, 1000) {
                @Override
                public void onTick(long l) {
                    btnGetOtp.setText("Wait: " + l / 1000 + "s");
                }

                @Override
                public void onFinish() {
                    btnGetOtp.setText("Get OTP");
                    btnGetOtp.setEnabled(true);
                }
            }.start();
        });

        // Reset Password
        btnForgot.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();

            if (toggleGroup.getCheckedButtonId() == R.id.btnPassword) {
                // Old password path
                String oldPassword = editTextPassword.getText().toString().trim();
                if (oldPassword.isEmpty()) {
                    editTextPassword.setError("Enter old password");
                    return;
                }
                // TODO: Call your API to verify old password
                Toast.makeText(this, "Proceed with old password flow", Toast.LENGTH_SHORT).show();

            } else {
                // OTP path
                String otp = editTextOtp.getText().toString().trim();
                if (otp.isEmpty()) {
                    editTextOtp.setError("Enter OTP");
                    return;
                }
                checkOtp(email, otp);
            }
        });

        // Back to Login
        txtLogin.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPassword.this, SignInActivity.class));
            finish();
        });
    }

    private void generateOtp() {
        int randomOtp = (int) (Math.random() * 900000) + 100000;
        storeOTP = String.valueOf(randomOtp);
    }

    private void sendAndStore(String email, String otp) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> sendStore = apiService.sendAndStoreOTP(otp, email);
        sendStore.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(ForgotPassword.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Toast.makeText(ForgotPassword.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkOtp(String email, String otp) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> check = apiService.checkOtp(email, otp);

        check.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String result = response.body().string();
                        boolean isMatched = Boolean.parseBoolean(result);

                        if (isMatched) {
                            // OTP matched → call useOTP API
                            apiService.useOTP(email).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Intent intent = new Intent(ForgotPassword.this, ResetActivity.class);
                                    intent.putExtra("email", email);
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(ForgotPassword.this, "Error from useOTP", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(ForgotPassword.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Toast.makeText(ForgotPassword.this, "Error reading response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPassword.this, "Failed to check OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ForgotPassword.this, "Error from checkOtp", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
