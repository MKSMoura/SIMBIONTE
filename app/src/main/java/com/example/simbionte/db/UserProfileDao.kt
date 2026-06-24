package com.example.simbionte.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: String = "default",
    val communicationDepth: Float = 0.5f,
    val warmthPreference: Float = 0.5f,
    val curiosityLevel: Float = 0.5f,
    val avgMessageLength: Float = 0f,
    val questionRate: Float = 0f,
    val reflectionRate: Float = 0f,
    val topicStability: Float = 0.5f,
    val totalInteractions: Int = 0,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val sentimentScore: Float = 0f,
    val userName: String? = null,
    val userNickname: String? = null,
    val purpose: String? = null,
    val characterName: String? = null,
    val limits: String? = null,
    val tonePreference: String? = null,
    val onboardingStep: Int = -1
)

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 'default'")
    suspend fun get(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(profile: UserProfile)
}
