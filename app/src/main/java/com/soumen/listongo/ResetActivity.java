package com.soumen.listongo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.soumen.listongo.SettingActivity.SettingsUtil;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetActivity extends AppCompatActivity {
TextInputEditText edtPassword,edtConfPass;
MaterialButton btnResetDone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtConfPass=findViewById(R.id.edtConfPass);
        edtPassword=findViewById(R.id.edtPassword);
        btnResetDone=findViewById(R.id.resetDone);

        String email=getIntent().getStringExtra("email");

        btnResetDone.setOnClickListener(v->{
            String password=edtPassword.getText().toString();
            String confPass=edtConfPass.getText().toString();
            if (password != null&&!password.isEmpty()&&confPass != null&&!confPass.isEmpty()) {
                updatePass(email,password);
            }
            else {
                Toast.makeText(this, "Password"+password, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updatePass(String email,String password){
        ApiService apiService=ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> updatePass=apiService.restPass(email,password);
        updatePass.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent intent=new Intent(ResetActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Toast.makeText(ResetActivity.this, "Have an error.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}