package com.soumen.listongo;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.soumen.listongo.ForAdmin.AdminActivity;
import com.soumen.listongo.Fragment.AddItemFragment;
import com.soumen.listongo.Fragment.AllListFragment;
import com.soumen.listongo.Fragment.ItemFragment;
import com.soumen.listongo.Fragment.ProfileFragment;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textUser=findViewById(R.id.username_text);
        email=getIntent().getStringExtra("email");
        userId=getIntent().getLongExtra("UserId",1);
        userName=getIntent().getStringExtra("userName");

        textUser.setText(userName);
        bottomNavigationView=findViewById(R.id.navItem);
        frameLayout=findViewById(R.id.nav_host_fragment);
        loadFragment(new AllListFragment(),true);
        toolbar=findViewById(R.id.topAppBar);



        toolbar.setOnMenuItemClickListener(v->{
            if (v.getItemId() == R.id.admin) {

                ApiService apiService=ApiClient.getInstance().create(ApiService.class);
                Call<ResponseBody>isAdmin=apiService.isAdmin(email);
                isAdmin.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String result= null;
                        try {
                            result = response.body().string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        isAdmin2=Boolean.parseBoolean(result.trim());
                        if (isAdmin2){
                            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                            startActivity(intent);
                        }else {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("SORRY")
                                    .setMessage("You are not an admin")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {

                    }
                });

            }
            else {
                Toast.makeText(this, "This is settings", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId=item.getItemId();

                if (itemId==R.id.list){
                    loadFragment(new AllListFragment(),false);
                    toolbar.setVisibility(VISIBLE);
                } else if (itemId==R.id.item) {
                    loadFragment(new ItemFragment(),false);
                    toolbar.setVisibility(GONE);
                } else if (itemId==R.id.addItem) {
                    loadFragment(new AddItemFragment(),false);
                    toolbar.setVisibility(GONE);
                }else {
                    loadFragment(new ProfileFragment(),false);
                    toolbar.setVisibility(GONE);
                }

                return true;
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


    public void loadFragment(Fragment fragment,boolean isList){
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        Bundle bundle=new Bundle();
        bundle.putString("email",email);
        bundle.putLong("UserId",userId);
        bundle.putString("UserName",userName);
        fragment.setArguments(bundle);
        if (isList){

            transaction.add(R.id.nav_host_fragment,fragment);
        }else {
            transaction.replace(R.id.nav_host_fragment, fragment);
        }
        transaction.commit();
    }
}