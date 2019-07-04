package com.example.lab.db.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

@Entity(tableName = "cart", foreignKeys = [ForeignKey(entity = User::class,
    parentColumns = arrayOf("email"),
    childColumns = arrayOf("userEmail"),
    onDelete = ForeignKey.CASCADE
)])

data class Cart(
    @NonNull @ColumnInfo(name = "name") val name: String,
    @NonNull @ColumnInfo(name = "userEmail") val userEmail: String,
    @NonNull @ColumnInfo(name = "code") val code: Double,
    @NonNull @ColumnInfo(name = "active") val active: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
