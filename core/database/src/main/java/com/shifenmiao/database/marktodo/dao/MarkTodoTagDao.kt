package com.shifenmiao.database.marktodo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifenmiao.database.marktodo.entity.MarkTodoTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkTodoTagDao {

    @Query("SELECT * FROM marktodo_tag ORDER BY sort_order ASC, created_at ASC")
    fun observeAll(): Flow<List<MarkTodoTagEntity>>

    @Query("SELECT * FROM marktodo_tag ORDER BY sort_order ASC, created_at ASC")
    suspend fun getAll(): List<MarkTodoTagEntity>

    @Query("SELECT COUNT(*) FROM marktodo_tag")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(tags: List<MarkTodoTagEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tag: MarkTodoTagEntity)

    @Query("DELETE FROM marktodo_tag WHERE id = :tagId")
    suspend fun deleteById(tagId: String)
}
