package com.soumen.listongo;

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

public class SignUpActivity extends AppCompatActivity {
    TextInputEditText edtUserName, edtOtp, edtEmail, edtPassword, edtConfPassword;
    MaterialButton btnGetOtp, btnCreateAcc;
    MaterialTextView textSign;
    String storeOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtEmail = findViewById(R.id.signup_email_input);
        edtOtp = findViewById((R.id.otpInput));
        edtPassword = findViewById(R.id.signup_password_input);
        edtConfPassword = findViewById(R.id.confirm_password_input);
        edtUserName = findViewById(R.id.username_input);
        btnCreateAcc = findViewById(R.id.sign_up_button);
        btnGetOtp = findViewById(R.id.btnGetOtp);
        textSign = findViewById(R.id.text_signin);

        textSign.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        });


        btnGetOtp.setOnClickListener(v -> {
            String email = edtEmail.getText().toString();
            Log.d("OTPEmail", email);
            if (email == null || email.isEmpty()) {
                edtEmail.setError("Enter email first.");
                return;
            }
            generateOtp();
            sendOtp(storeOTP, email);
        });

        btnCreateAcc.setOnClickListener(v -> {
            String enteredOtp = edtOtp.getText() != null ? edtOtp.getText().toString().trim() : "";

            if (enteredOtp.isEmpty() || !enteredOtp.equals(storeOTP)) {
                edtOtp.setError("Enter Valid OTP");
                return;
            }

            if (!emailChecker() || !nameChecker() || !passwordChecker()) {
                return;
            }

            apiResponse();
        });

    }

    //call api
    public void apiResponse() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String username = edtUserName.getText().toString();
        SignUpUserModel signUpUserModel = new SignUpUserModel(username, email, password);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.signUpUser(signUpUserModel);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                    finish();
                } else {
                    try {
                        String errorMessage = response.errorBody().string();

                        if (errorMessage.contains("Username")) {
                            edtUserName.setError("This username is already taken");
                        } else if (errorMessage.contains("Email")) {
                            edtEmail.setError("This email is already registered");
                        } else {
                            Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(SignUpActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Toast.makeText(SignUpActivity.this, "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public boolean nameChecker() {
        String name = edtUserName.getText().toString();
        if (name.isEmpty()) {
            edtUserName.setError("Name can't be empty.");
            return false;
        } else {
            return true;
        }
    }

    public boolean emailChecker() {
        String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";

        if (email.isEmpty() || !isValidEmail(email)) {
            edtEmail.setError("Enter Valid Email");
            return false;
        } else {
            edtEmail.setError(null); // Clear error if valid
            return true;
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public boolean passwordChecker() {
        String password = edtPassword.getText().toString().trim();
        String confirm = edtConfPassword.getText().toString().trim();
        if (password.isEmpty()) {
            edtPassword.setError("Password cannot be empty.");
            return false;
        }
        if (confirm.isEmpty()) {
            edtConfPassword.setError("Please confirm your password.");
            return false;
        }
        if (password.length() < 8) {
            edtPassword.setError("Password must be at least 8 characters.");
            return false;
        }
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        if (!password.matches(passwordPattern)) {
            edtPassword.setError("Password must contain uppercase, number, and special character.");
            return false;
        }

        if (!password.equals(confirm)) {
            edtConfPassword.setError("Passwords do not match.");
            return false;
        }

        return true;
    }

    public void generateOtp() {
        int randomOtp = (int) (Math.random() * 900000) + 100000;
        storeOTP = String.valueOf(randomOtp);
    }


    private void sendOtp(String otp, String email) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> send = apiService.sendAndStoreOTP(otp, email);
        send.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(SignUpActivity.this, "Send and Store Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Toast.makeText(SignUpActivity.this, "Have an error", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private  void  checkOtp(String email){
//        ApiService apiService=ApiClient.getInstance().create(ApiService.class);
//        Call<ResponseBody> checkThis=apiService.checkOtp(email);
//        checkThis.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                boolean otpCheck=Boolean.parseBoolean(response.body().toString());
//                if (!otpCheck) {
//                    Call<ResponseBody> useThis=apiService.useOTP(email);
//                    useThis.enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            Toast.makeText(SignUpActivity.this, "Done", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
//                            Toast.makeText(SignUpActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }else {
//                    Toast.makeText(SignUpActivity.this, "OTP not matched.Try again", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
//                Toast.makeText(SignUpActivity.this, throwable.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//}
}