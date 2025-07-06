package com.soumen.listongo;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
}