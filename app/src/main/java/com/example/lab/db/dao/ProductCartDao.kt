package com.example.lab.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.example.lab.db.models.Complaint
import com.example.lab.db.models.ProductCart

@Dao
interface ProductCartDao {
    @Query("SELECT * FROM productCart")
    fun getAll(): List<ProductCart>

    @Query("SELECT * FROM productCart WHERE id LIKE :id")
    fun getProductCart(id: Int): ProductCart


    @Insert
    fun insertAll(vararg productCart: ProductCart)
}