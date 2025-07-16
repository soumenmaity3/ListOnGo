package com.soumen.listongo.ForAdmin.ReqForMakeAdmin;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReqForAdminActivity extends AppCompatActivity {
    private static final int PLAN_REQUEST_CODE = 1001;
    TextInputEditText planEditText;
    private String storeOTP, coinValue;

    //     choosePlane;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_req_for_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvAgreementText = findViewById(R.id.tvAgreementText);
        String fullText = "I agree to the Terms & Conditions and Privacy Policy";
        SpannableString spannable = new SpannableString(fullText);

        // Terms & Conditions span
        ClickableSpan termsSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // TODO: Open Terms & Conditions Activity or WebPage
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(ContextCompat.getColor(ReqForAdminActivity.this, R.color.link_color));
                ds.setUnderlineText(false);
            }
        };

        // Privacy Policy span
        ClickableSpan privacySpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // TODO: Open Privacy Policy Activity or WebPage
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(ContextCompat.getColor(ReqForAdminActivity.this, R.color.link_color));
                ds.setUnderlineText(false);
            }
        };

// Apply spans
        spannable.setSpan(termsSpan, 14, 33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(privacySpan, 38, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// Set to TextView
        tvAgreementText.setText(spannable);
        tvAgreementText.setMovementMethod(LinkMovementMethod.getInstance());
        tvAgreementText.setHighlightColor(Color.TRANSPARENT);

        String email = getIntent().getStringExtra("email");

        MaterialButton btnAdmin = findViewById(R.id.submitButton);
        MaterialButton choosePlane = findViewById(R.id.choosePlanButton);
        MaterialCheckBox checkBox = findViewById(R.id.termsCheckbox);
        MaterialTextView sendOTP = findViewById(R.id.sendOtpText);
        TextInputEditText reasonEdit = findViewById(R.id.reasonEditText);
        TextInputEditText emailInput = findViewById(R.id.emailEditText);
        TextInputEditText otpInput = findViewById(R.id.otpEditText);
        planEditText = findViewById(R.id.planEditText);


        emailInput.setText(email);
        emailInput.setEnabled(false);

        choosePlane.setOnClickListener(v -> {
            Intent intent = new Intent(ReqForAdminActivity.this, PlaneListActivity.class);
            startActivityForResult(intent, PLAN_REQUEST_CODE);
        });


        sendOTP.setOnClickListener(v -> {
            generateOtp();
            sendAndStore(email, storeOTP);
            new CountDownTimer(30000, 1000) {
                @Override
                public void onTick(long l) {
                    sendOTP.setText("Wait: " + l / 1000 + " s");
                    sendOTP.setEnabled(false);
                }

                @Override
                public void onFinish() {
                    sendOTP.setText("Send OTP");
                    sendOTP.setEnabled(true);
                }
            }.start();
        });
        btnAdmin.setOnClickListener(v -> {
            if (!checkBox.isChecked()) {
                Toast.makeText(this, "Read Terms & Conditions and Privacy Policy carefully", Toast.LENGTH_SHORT).show();
                return;
            }
            if (otpInput.getText().toString().isEmpty()) {
                Toast.makeText(this, "Enter otp", Toast.LENGTH_SHORT).show();
            }
            String otp = otpInput.getText().toString();
            String reason = reasonEdit.getText().toString();
            checkOtp(email, otp, reason);
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
                Toast.makeText(ReqForAdminActivity.this, "Send Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Toast.makeText(ReqForAdminActivity.this, "Have an error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkOtp(String email, String otp, String reason) {
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
                                    Intent intent=new Intent(ReqForAdminActivity.this, PayForAdminActivity.class);
                                    intent.putExtra("email",email);
                                    intent.putExtra("reason",reason);
                                    intent.putExtra("coin_value",coinValue.trim());
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(ReqForAdminActivity.this, "Error from useOTP", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // OTP did not match
                            Toast.makeText(ReqForAdminActivity.this, "OTP not matched", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ReqForAdminActivity.this, "Error reading response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReqForAdminActivity.this, "Failed response from checkOtp", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ReqForAdminActivity.this, "Error from checkOtp", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAN_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String selectedPlan = data.getStringExtra("selected_plan");
            coinValue = data.getStringExtra("coin_value");
            planEditText.setText(selectedPlan + " for " + coinValue + " coin");
        }
    }
}