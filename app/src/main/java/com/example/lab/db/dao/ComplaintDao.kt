package com.example.lab.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.example.lab.db.models.Complaint

@Dao
interface ComplaintDao {
    @Query("SELECT * FROM complaints")
    fun getAll(): List<Complaint>

    @Query("SELECT * FROM complaints WHERE id LIKE :id")
    fun getComplaint(id: Int): Complaint

    @Query("SELECT * FROM complaints WHERE userEmail LIKE :userEmail")
    fun getUserComplaints(userEmail: String): List<Complaint>

    @Query("SELECT * FROM complaints WHERE userEmail NOT LIKE :userEmail")
    fun getNonCurrentUserComplaints(userEmail: String): List<Complaint>

    @Insert
    fun insertAll(vararg complaint: Complaint)
}