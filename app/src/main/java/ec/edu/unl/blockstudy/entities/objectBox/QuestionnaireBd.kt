package ec.edu.unl.blockstudy.entities.objectBox

import android.os.Parcel
import android.os.Parcelable
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

@Entity
class QuestionnaireBd() : Parcelable {
    @Id
    var id: Long = 0
    var idCloud = ""
    var title: String? = null
    var idUser: String? = null
    var description: String? = null
    var numberQuest: Int = 0
    @Backlink
    lateinit var questions: ToMany<QuestionBd>

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        idCloud = parcel.readString()
        title = parcel.readString()
        idUser = parcel.readString()
        description = parcel.readString()
        numberQuest = parcel.readInt()
    }

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