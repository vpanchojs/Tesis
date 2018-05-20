package ec.edu.unl.blockstudy.entities

import android.os.Parcel
import android.os.Parcelable
import ec.edu.unl.blockstudy.entities.objectBox.QuestionnaireBd
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

/**
 * Created by victor on 1/4/18.
 */
@Entity
class Block() : Parcelable {
    @Id
    var id: Long = 0
    var timeActivity: Int = -1

    @Backlink
    lateinit var apps: ToMany<Application>

    @Backlink
    lateinit var questionaire: ToMany<QuestionnaireBlock>

    var idUser: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        timeActivity = parcel.readInt()
        idUser = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeInt(timeActivity)
        parcel.writeString(idUser)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Block> {
        override fun createFromParcel(parcel: Parcel): Block {
            return Block(parcel)
        }

        override fun newArray(size: Int): Array<Block?> {
            return arrayOfNulls(size)
        }
    }
}