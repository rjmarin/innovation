package com.example.lab.db.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull


@Entity(tableName = "productCart", foreignKeys = [ForeignKey(entity = Cart::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("cartId"),
    onDelete = ForeignKey.CASCADE),ForeignKey(entity = Product::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("productId"),
    onDelete = ForeignKey.CASCADE)
])

data class ProductCart(
    @NonNull @ColumnInfo(name = "cartId") val name: Int,
    @NonNull @ColumnInfo(name = "productId") val userEmail: Int

) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}