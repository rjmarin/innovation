package com.example.lab.db.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import android.location.Location
import android.support.annotation.NonNull
import com.google.android.gms.tasks.Task

@Entity(tableName = "complaints", foreignKeys = [ForeignKey(entity = User::class,
                                                parentColumns = arrayOf("email"),
                                                childColumns = arrayOf("userEmail"),
                                                onDelete = CASCADE)])

data class Complaint(
    @NonNull @ColumnInfo(name = "name") val name: String,
    @NonNull @ColumnInfo(name = "date") val date: String,
    @NonNull @ColumnInfo(name = "complaint") val complaint: String,
    @NonNull @ColumnInfo(name = "userEmail") val userEmail: String,
    @ColumnInfo(name = "photoUri") val photoUri: String?,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double
) {
    @PrimaryKey(autoGenerate=true) var id: Int = 0
}