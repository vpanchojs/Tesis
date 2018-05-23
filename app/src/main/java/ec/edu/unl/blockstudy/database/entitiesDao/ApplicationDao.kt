package ec.edu.unl.blockstudy.database.entitiesDao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import ec.edu.unl.blockstudy.database.Application

@Dao
interface ApplicationDao {
    @Query(value = "SELECT * FROM application where block_id = :id_block")
    fun getApplicationByBlock(id_block: Long): List<Application>

    @Query(value = "DELETE FROM application")
    fun deleteAll()

    @Insert
    fun insert(applicationsList: List<Application>): List<Long>
}