package ec.edu.unl.blockstudy.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "block")
class Block {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    @ColumnInfo(name = "user_id")
    var idUser = ""
    @ColumnInfo(name = "time_activity")
    var timeActivity: Int = -1
}
