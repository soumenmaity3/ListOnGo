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

import com.google.android.material.card.MaterialCardView;
import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.MainActivity;
import com.soumen.listongo.R;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayForAdminActivity extends AppCompatActivity {
    String email, reason, coinValue;
    MaterialCardView upiOption,cardOption,netBankingOption;
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
        isAdminReq=getIntent().getBooleanExtra("isAdminReq",false);

        upiOption=findViewById(R.id.upiOption);
        cardOption=findViewById(R.id.cardOption);
        netBankingOption=findViewById(R.id.netBankingOption);
        tvPrice=findViewById(R.id.tvPrice);

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

        dialog=new Dialog(this);
        dialog.setContentView(R.layout.dialog_processing_payment);

        upiOption.setOnClickListener(v->buyCoin());
        netBankingOption.setOnClickListener(v->buyCoin());
        cardOption.setOnClickListener(v->buyCoin());


    }


    public void buyCoin() {
        dialog.show();

        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> buy = apiService.buyCredit(email, Integer.parseInt(coinValue));

        buy.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(PayForAdminActivity.this, "Payment Successful!", Toast.LENGTH_SHORT).show();
                    if (isAdminReq) {
                        reqForAdmin(email,reason);
                    }
                    finish();
                } else {
                    Toast.makeText(PayForAdminActivity.this, "Payment failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                dialog.dismiss();
                Toast.makeText(PayForAdminActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
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
                    congEmail();
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

    public void congEmail() {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> congEmail = apiService.congEmail(email);
        congEmail.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {

            }
        });
    }
}