package ec.com.dovic.aprendiendo.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey


/**
 * Created by victor on 3/4/18.
 */
@Entity(tableName = "application",
        foreignKeys = arrayOf(ForeignKey(entity = Block::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("block_id"),
                onDelete = ForeignKey.CASCADE)))
data class Application(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        var packagename: String = "",
        @ColumnInfo(name = "block_id")
        var blockId: Long = 0)

