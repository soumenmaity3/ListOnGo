package com.soumen.listongo;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ForgotPassword extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioOldPassword, radioOtp;
    private LinearLayout layoutOldPassword, layoutOtp;

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

        // Handle toggle
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_old_password) {
                layoutOldPassword.setVisibility(View.VISIBLE);
                layoutOtp.setVisibility(View.GONE);
            } else if (checkedId == R.id.radio_otp) {
                layoutOldPassword.setVisibility(View.GONE);
                layoutOtp.setVisibility(View.VISIBLE);
            }
        });

        // Optional: default selection
        radioOtp.setChecked(true);
    }
}
