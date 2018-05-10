package ec.edu.unl.blockstudy.entities

import android.os.Parcel
import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
class QuestionPath() : Parcelable {
    @Id
    var id: Long = 0
    lateinit var path: String
    lateinit var QuestionnaireBlock: ToOne<QuestionnaireBlock>


    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        path = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(path)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionPath> {
        override fun createFromParcel(parcel: Parcel): QuestionPath {
            return QuestionPath(parcel)
        }

        override fun newArray(size: Int): Array<QuestionPath?> {
            return arrayOfNulls(size)
        }
    }
}