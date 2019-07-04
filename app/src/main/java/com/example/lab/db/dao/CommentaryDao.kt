package com.example.lab.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.example.lab.db.models.Commentary

@Dao
interface CommentaryDao {
    @Query("SELECT * FROM commentaries WHERE complaintId LIKE :complaintId")
    fun getComplaintCommentaries(complaintId: Int): List<Commentary>

    @Insert
    fun insertAll(vararg commentary: Commentary)
}