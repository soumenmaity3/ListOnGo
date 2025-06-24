package com.soumen.listongo.ForCart;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartManager {

    private static final String PREF_NAME = "cart_pref";
    private static final String CART_KEY = "cart_items";

    private static CartManager instance;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private HashMap<String, CartItem> cartItems;

    private CartManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        cartItems = new HashMap<>();
        loadCart();
    }

    public static CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context.getApplicationContext());
        }
        return instance;
    }

    private void saveCart() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(cartItems);
        editor.putString(CART_KEY, json);
        editor.apply();
    }

    private void loadCart() {
        String json = sharedPreferences.getString(CART_KEY, "");
        if (!json.isEmpty()) {
            Type type = new TypeToken<HashMap<String, CartItem>>() {}.getType();
            cartItems = gson.fromJson(json, type);
        }
    }

    public void addToCart(CartItem item) {
        if (cartItems.containsKey(item.getId())) {
            CartItem existing = cartItems.get(item.getId());
            existing.setQuantity(existing.getQuantity() + 1);
        } else {
            cartItems.put(item.getId(), item);
        }
        saveCart();
    }

    public void decreaseQuantity(String productId) {
        if (cartItems.containsKey(productId)) {
            CartItem item = cartItems.get(productId);
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
            } else {
                cartItems.remove(productId);
            }
            saveCart();
        }
    }

    public void removeFromCart(String productId) {
        cartItems.remove(productId);
        saveCart();
    }

    public List<CartItem> getCartList() {
        return new ArrayList<>(cartItems.values());
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems.values()) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
        saveCart();
    }

    public int getItemQuantity(String productId) {
        return cartItems.containsKey(productId) ? cartItems.get(productId).getQuantity() : 0;
    }
}
