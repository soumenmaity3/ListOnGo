package com.soumen.listongo;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecurePrefsHelper {
    private static final String FILE_NAME = "secure_prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME="user_name";

    private final SharedPreferences sharedPreferences;

    public SecurePrefsHelper(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("SecurePrefsHelper init error", e);
        }
    }

    public void saveCredentials(String email, String password, Long userId,String userName) {
        sharedPreferences.edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .putLong(KEY_USER_ID, userId)
                .putString(KEY_USERNAME,userName)
                .apply();
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, null);
    }

    public Long getUserId() {
        if (!sharedPreferences.contains(KEY_USER_ID)) return null;
        return sharedPreferences.getLong(KEY_USER_ID, -1);
    }

   public String getUsername(){
       return sharedPreferences.getString(KEY_USERNAME, null);
   }


    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
