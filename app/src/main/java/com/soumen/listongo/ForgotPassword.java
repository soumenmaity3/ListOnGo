package com.soumen.listongo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioOldPassword, radioOtp;
    private LinearLayout layoutOldPassword, layoutOtp;
    private MaterialButton btnForgot, btnGetOtp;
    TextInputEditText editTextEmail, editTextOtp,editTextPassword;
    String storeOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        // Apply padding for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        radioGroup = findViewById(R.id.radioGroup);
        radioOldPassword = findViewById(R.id.radio_old_password);
        radioOtp = findViewById(R.id.radio_otp);
        layoutOldPassword = findViewById(R.id.layout_old_password);
        layoutOtp = findViewById(R.id.layout_otp);
        btnForgot = findViewById(R.id.btnResetPas);
        btnGetOtp = findViewById(R.id.btnGetOtp);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextOtp = findViewById(R.id.editTextOtp);
        editTextPassword=findViewById(R.id.editTextOldPassword);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_old_password) {
                layoutOldPassword.setVisibility(View.VISIBLE);
                layoutOtp.setVisibility(View.GONE);
                editTextOtp.setEnabled(false);
                editTextPassword.setEnabled(true);
            } else if (checkedId == R.id.radio_otp) {
                layoutOldPassword.setVisibility(View.GONE);
                layoutOtp.setVisibility(View.VISIBLE);
                editTextOtp.setEnabled(true);
                editTextPassword.setEnabled(false);
            }
        });
        radioOtp.setChecked(true);
        btnGetOtp.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            Log.d("OTPEmail", email);
            if (email == null || email.isEmpty()) {
                editTextEmail.setError("Enter email first.");
                return;
            }
            generateOtp();
            sendAndStore(email, storeOTP);
        });
        btnForgot.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String otp = editTextOtp.getText().toString();
            checkOtp(email, otp);
        });
    }

    public void generateOtp() {
        int randomOtp = (int) (Math.random() * 900000) + 100000;
        storeOTP = String.valueOf(randomOtp);
    }

    public void sendAndStore(String email, String otp) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> sendStore = apiService.sendAndStoreOTP(otp, email);
        sendStore.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(ForgotPassword.this, "Send Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Toast.makeText(ForgotPassword.this, "Have an error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkOtp(String email, String otp) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> check = apiService.checkOtp(email, otp);

        check.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String result = response.body().string(); // Read response safely
                        boolean isMatched = Boolean.parseBoolean(result);

                        if (isMatched) {
                            // OTP matched, proceed to useOTP
                            Call<ResponseBody> useThis = apiService.useOTP(email);
                            useThis.enqueue(new Callback<ResponseBody>() {
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
                            // OTP did not match
                            Toast.makeText(ForgotPassword.this, "OTP not matched", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ForgotPassword.this, "Error reading response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPassword.this, "Failed response from checkOtp", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ForgotPassword.this, "Error from checkOtp", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
