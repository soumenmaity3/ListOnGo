package com.soumen.listongo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    TextInputEditText edtEmail,edtPassword;
    MaterialButton btnSignIn;
    MaterialTextView txtSignUp,txtForgoPass;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtEmail=findViewById(R.id.email_input);
        edtPassword=findViewById(R.id.password_input);
        btnSignIn=findViewById(R.id.sign_in_button);
        txtSignUp=findViewById(R.id.sign_up_link);
        txtForgoPass=findViewById(R.id.for_got_pass);


        btnSignIn.setOnClickListener(v->{
           apiResponse();
        });

        txtSignUp.setOnClickListener(v->{
            Intent intent=new Intent(this, SignUpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        txtForgoPass.setOnClickListener(v->{
            Intent intent=new Intent(this,ForgotPassword.class);
            startActivity(intent);
        });

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
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            intent.putExtra("UserId", userId);
                            intent.putExtra("userName", username);  // Will now be valid
                            intent.putExtra("email", email);
                            startActivity(intent);
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

}