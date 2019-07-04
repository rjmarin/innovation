package com.example.lab.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.example.lab.db.models.Cart
import com.example.lab.db.models.Complaint

@Dao
interface CartDao {
    @Query("SELECT * FROM cart")
    fun getAll(): List<Cart>

    @Query("SELECT * FROM cart WHERE id LIKE :id")
    fun getCart(id: Int): Cart

    @Query("SELECT * FROM cart WHERE userEmail LIKE :userEmail")
    fun getUserCart(userEmail: String): List<Cart>

    @Query("SELECT * FROM cart WHERE userEmail NOT LIKE :userEmail")
    fun getNonCurrentUserCart(userEmail: String): List<Cart>

    @Insert
    fun insertAll(vararg cart: Cart)
}