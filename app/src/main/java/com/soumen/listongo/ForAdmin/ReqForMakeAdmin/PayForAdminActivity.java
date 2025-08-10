package com.soumen.listongo.ForAdmin.ReqForMakeAdmin;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.MainActivity;
import com.soumen.listongo.R;
import com.soumen.listongo.SettingActivity.SettingsActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayForAdminActivity extends AppCompatActivity {
    String email, reason, coinValue;
    MaterialCardView upiOption, cardOption, netBankingOption;
    TextView tvPrice;
    Dialog dialog;
    boolean isAdminReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pay_for_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = getIntent().getStringExtra("email");
        reason = getIntent().getStringExtra("reason");
        coinValue = getIntent().getStringExtra("coin_value");
        isAdminReq = getIntent().getBooleanExtra("isAdminReq", false);

        upiOption = findViewById(R.id.upiOption);
        cardOption = findViewById(R.id.cardOption);
        netBankingOption = findViewById(R.id.netBankingOption);
        tvPrice = findViewById(R.id.tvPrice);

        findViewById(R.id.otherOption).setOnClickListener(v -> {
            String[] options = {
                    "Paytm Wallet",
                    "PhonePe Wallet",
                    "Amazon Pay",
                    "QR Code Payment",
                    "Buy Now, Pay Later (Simpl, LazyPay)",
                    "EMI / ZestMoney",
                    "Crypto (Bitcoin, USDT)",
                    "Cash / Manual Transfer"
            };

            if (coinValue != null) {
                tvPrice.setText(coinValue);
            }

            new AlertDialog.Builder(this)
                    .setTitle("Other Payment Options")
                    .setItems(options, (dialog, which) -> {
                        String selected = options[which];
                        buyCoin();
                        Toast.makeText(this, "Selected: " + selected, Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_processing_payment);

        upiOption.setOnClickListener(v -> buyCoin());
        netBankingOption.setOnClickListener(v -> buyCoin());
        cardOption.setOnClickListener(v -> buyCoin());


    }


    private void buyCoin() {
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);

        final int creditValue;
        try {
            creditValue = Integer.parseInt(coinValue);
        } catch (NumberFormatException e) {
            dialog.dismiss();
            Toast.makeText(this, "Invalid credit amount", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Integer> call = apiService.buyCredit(email, creditValue);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                dialog.dismiss();

                if (response.isSuccessful()) {
                    Integer body = response.body();
                    if (body != null && body > 0) {
                        // Success — server returned a positive integer (the credit you added)
                        Toast.makeText(PayForAdminActivity.this, "Payment Successful! Added: " + body, Toast.LENGTH_SHORT).show();

                        if (isAdminReq) {
                            reqForAdmin(email, reason);
                        }

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("SHOW_DIALOG", true);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                        return;
                    } else {
                        // HTTP 200 but body is 0 or null — treat as failure
                        Toast.makeText(PayForAdminActivity.this, "Server returned no credit. Try again.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Non-2xx HTTP response: inspect error body for debugging
                    String err = "Server error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            err += " - " + response.errorBody().string();
                        }
                    } catch (IOException ignore) {}
                    Toast.makeText(PayForAdminActivity.this, err, Toast.LENGTH_LONG).show();
                }

                // On any failure branch above, return failure result
                Intent resultIntent = new Intent();
                resultIntent.putExtra("SHOW_DIALOG", false);
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                dialog.dismiss();
                // Network error, timeout, or conversion error
                Toast.makeText(PayForAdminActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("SHOW_DIALOG", false);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }



    public void reqForAdmin(String email, String reason) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("reason", reason);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        apiService.reqForAdmin(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
//                    congEmail();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("SHOW_DIALOG", true);
                    setResult(RESULT_OK, resultIntent);
                    Toast.makeText(PayForAdminActivity.this, "User promoted to admin", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(PayForAdminActivity.this, "Promotion failed: user not found or already admin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PayForAdminActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

//    public void congEmail() {
//        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
//        Call<ResponseBody> congEmail = apiService.congEmail(email);
//        congEmail.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
//
//            }
//        });
//    }
}