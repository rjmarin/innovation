package com.example.lab.db.dao

import android.arch.persistence.room.*
import android.database.sqlite.SQLiteConstraintException
import com.example.lab.db.models.ComplaintVote


@Dao
interface ComplaintVoteDao {
    @Query("SELECT * FROM complaint_votes WHERE complaintId LIKE :complaintId AND userEmail LIKE :currentUserEmail")
    fun getCurrentUserComplaintVoteByComplaintId(complaintId: Int, currentUserEmail: String): ComplaintVote?

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(complaintVote: ComplaintVote)

    @Update(onConflict = OnConflictStrategy.FAIL)
    fun update(complaintVote: ComplaintVote)

    // CURRENTYL NOT WORKING
    @Transaction
    fun upsert(complaintVote: ComplaintVote) {
        try {
            insert(complaintVote)
        } catch (exception: SQLiteConstraintException) {
            update(complaintVote)
        }

    }

    @Delete
    fun deleteComplaintVote(complaintVote: ComplaintVote)

}