package com.example.lab.db.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

@Entity(tableName = "complaint_votes",
    primaryKeys = ["complaintId", "userEmail"],
    foreignKeys = [
    ForeignKey(entity = User::class,
        parentColumns = arrayOf("email"),
        childColumns = arrayOf("userEmail"),
        onDelete = CASCADE),

    ForeignKey(entity = Complaint::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("complaintId"),
        onDelete = CASCADE)
])

data class ComplaintVote(
    @NonNull @ColumnInfo(name = "complaintId") val complaintId: Int,
    @NonNull @ColumnInfo(name = "userEmail") val userEmail: String,
    @NonNull @ColumnInfo(name = "type") val voteType: Int
)

enum class VoteType(val voteTypeId: Int) {
    UP(1),
    DOWN(2)
}