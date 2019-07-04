package com.example.lab.db.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull


@Entity(tableName = "product")

data class Product(
    @PrimaryKey @NonNull @ColumnInfo(name = "id") val id: Int,
    @NonNull @ColumnInfo(name = "name") val name: String,
    @NonNull @ColumnInfo(name = "price") val price: Int,
    @NonNull @ColumnInfo(name = "weight") val weight: Float
)
