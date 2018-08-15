package ec.com.dovic.aprendiendo.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable


@Entity(tableName = "questionnnaire")
class QuestionnaireBd() : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "cloud_id")
    var idCloud = ""
    var title: String? = null
    var idUser: String? = null
    var idUserLocal: String? = null
    var description: String? = null
    var numberQuest: Int = 0
    var me = false
    @Ignore
    var questions: ArrayList<QuestionBd>? = null


    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        idCloud = parcel.readString()
        title = parcel.readString()
        idUser = parcel.readString()
        description = parcel.readString()
        numberQuest = parcel.readInt()
    }

    @ColumnInfo(name = "block_id")
    var blockId: Long = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(idCloud)
        parcel.writeString(title)
        parcel.writeString(idUser)
        parcel.writeString(description)
        parcel.writeInt(numberQuest)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionnaireBd> {
        override fun createFromParcel(parcel: Parcel): QuestionnaireBd {
            return QuestionnaireBd(parcel)
        }

        override fun newArray(size: Int): Array<QuestionnaireBd?> {
            return arrayOfNulls(size)
        }
    }

}