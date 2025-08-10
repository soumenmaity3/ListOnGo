package com.soumen.listongo;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.soumen.listongo.ForAdmin.AdminActivity;
import com.soumen.listongo.Fragment.AddItem.AddItemFragment;
import com.soumen.listongo.Fragment.AllListF.AllListFragment;
import com.soumen.listongo.Fragment.ItemLi.ItemFragment;
import com.soumen.listongo.Fragment.ProfileFragment;
import com.soumen.listongo.SettingActivity.SettingsActivity;
import com.soumen.listongo.SettingActivity.SettingsUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

public class MainActivity extends AppCompatActivity {
    TextView textUser;
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    MaterialToolbar toolbar;
    Long userId;
    String email;
    String userName;
    boolean isAdmin2;
    private long backPressedTime;
    private Toast backToast;
    int credit;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textUser = findViewById(R.id.username_text);
        email = getIntent().getStringExtra("email");
        userId = getIntent().getLongExtra("UserId", 1);
        userName = getIntent().getStringExtra("userName");

        textUser.setText(userName);
        bottomNavigationView = findViewById(R.id.navItem);
        frameLayout = findViewById(R.id.nav_host_fragment);
        loadFragment(new AllListFragment(), true);
        toolbar = findViewById(R.id.topAppBar);

        isAdmin();

        toolbar.setOnMenuItemClickListener(v -> {
            if (v.getItemId() == R.id.admin) {
                if (isAdmin2) {
                    Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                    intent.putExtra("UserId", userId);
                    intent.putExtra("email",email);
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("SORRY")
                            .setMessage("You are not an admin")
                            .setPositiveButton("Ok", null)
                            .show();
                }


            } else if (v.getItemId()==R.id.setting){
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("userName", userName);
                intent.putExtra("UserId", userId);
                intent.putExtra("userEmail", email);
                startActivity(intent);
            }else {
                showTimePickerDialog();
            }
            return true;
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.list) {
                    loadFragment(new AllListFragment(), false);
                    refreshUI();
                    toolbar.setVisibility(VISIBLE);
                } else if (itemId == R.id.item) {
                    loadFragment(new ItemFragment(), false);
                    toolbar.setVisibility(GONE);
                } else if (itemId == R.id.addItem) {
                    loadFragment(new AddItemFragment(), false);
                    toolbar.setVisibility(GONE);
                } else {
                    loadFragment(new ProfileFragment(), false);
                    toolbar.setVisibility(GONE);
                }

                return true;
            }
        });

    }

    public void isAdmin() {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> isAdmin = apiService.isAdmin(email);
        isAdmin.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String result = null;
                try {
                    result = response.body().string();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                isAdmin2 = Boolean.parseBoolean(result.trim());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            if (backToast != null) backToast.cancel();
            super.onBackPressed(); // Exit activity
        } else {
            backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
            backPressedTime = System.currentTimeMillis();
        }
    }


    public void loadFragment(Fragment fragment, boolean isList) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putLong("UserId", userId);
        bundle.putString("UserName", userName);
        bundle.putBoolean("isAdmin", isAdmin2);
        bundle.putInt("credit",credit);
        fragment.setArguments(bundle);
        if (isList) {
            transaction.add(R.id.nav_host_fragment, fragment);
        } else {
            transaction.replace(R.id.nav_host_fragment, fragment);
        }
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void refreshUI() {
        TextView creditText = findViewById(R.id.cradit_coin);
        getUserCredit(creditText);
    }

    private void getUserCredit(TextView creditText) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> getCoin = apiService.getCoin(email);

        getCoin.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String coinStr = response.body().string().trim();
                        int coin = Integer.parseInt(coinStr);
                        credit = coin;
                        creditText.setText(String.valueOf(credit));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    private void showTimePickerDialog() {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H) // or CLOCK_24H
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Reminder Time")
                .build();

        picker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");

        picker.addOnPositiveButtonClickListener(view -> {
            int hour = picker.getHour();
            int minute = picker.getMinute();

            // Call your scheduling function here
            scheduleReminder(hour, minute);
        });
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleReminder(int hour, int minute) {
        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= now.getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        long delay = calendar.getTimeInMillis() - now.getTimeInMillis();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(this).enqueue(workRequest);

        Toast.makeText(this, "Reminder set via WorkManager", Toast.LENGTH_SHORT).show();
    }


}