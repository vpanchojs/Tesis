package ec.edu.unl.blockstudy.database.entitiesDao

import android.arch.persistence.room.*
import ec.edu.unl.blockstudy.database.Block


@Dao
interface BlockDao {

    @Query(value = "SELECT * FROM block")
    fun getBlockAll(): MutableList<Block>

    @Update
    fun updateBlock(block: Block): Int

    @Delete
    fun deleteBlock(block: Block): Int

    @Insert
    fun addBlock(block: Block): Long

    @Query("SELECT *FROM block where user_id =:id_user")
    fun getBlockById(id_user: String): Block

    @Query(value = "UPDATE block set time_activity=:time")
    fun updateTimeBlock(time: Int)
}