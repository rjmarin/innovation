package com.example.lab.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.example.lab.db.models.Complaint
import com.example.lab.db.models.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    fun getAll(): List<Product>

    @Query("SELECT DISTINCT name FROM product")
    fun getNamesProducts(): List<String>

    @Query("SELECT * FROM product WHERE id LIKE :id")
    fun getProduct(id: Int): Product


    @Query("SELECT  * FROM   product WHERE name LIKE :name LIMIT 1")
    fun getDistProduct(name:String): List<Product>

    @Insert
    fun insertAll(vararg product: Product)
}
