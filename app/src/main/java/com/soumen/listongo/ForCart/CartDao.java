package com.soumen.listongo.ForCart;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CartModel cartModel);

    @Query("DELETE FROM cart_items WHERE id=:id")
    void delete(Long id);

    @Query("SELECT * FROM cart_items")
    LiveData<List<CartModel>> getAll();

    @Update
    void updateItem(CartModel item);

    @Query("SELECT * FROM cart_items WHERE id = :id LIMIT 1")
    CartModel getItemById(Long id);

    @Query("UPDATE cart_items SET quantity = quantity + 1 WHERE id = :id")
    void increaseQuantityByOne(Long id);

    @Query("UPDATE cart_items SET quantity = quantity - 1 WHERE id = :id")
    void decreaseQuantityByOne(Long id);


    @Query("DELETE FROM cart_items")
    void Delete();

    @Query("SELECT quantity FROM cart_items WHERE id = :id")
    int getQuantityById(Long id);

    @Query("DELETE FROM cart_items WHERE id = :id")
    void deleteById(Long id);
//    @Query("SELECT SUM(price * quantity) FROM cart_items")
//    LiveData<Double> getTotalCartPrice();

}
