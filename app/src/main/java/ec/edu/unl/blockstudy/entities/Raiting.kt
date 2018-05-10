package ec.edu.unl.blockstudy.entities

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import java.util.*

/**
 * Created by victor on 28/3/18.
 */
class Raiting() : Parcelable {

    var idRaiting: String = ""
    var value: Double = 0.0
    lateinit var comment: String
    lateinit var idQuestionaire: String
    lateinit var date: Date


    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["value"] = value!!
        result["comment"] = comment!!
        result["date"] = FieldValue.serverTimestamp()!!
        return result
    }

    constructor(parcel: Parcel) : this() {
        idRaiting = parcel.readString()
        value = parcel.readDouble()
        comment = parcel.readString()
        idQuestionaire = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idRaiting)
        parcel.writeDouble(value)
        parcel.writeString(comment)
        parcel.writeString(idQuestionaire)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Raiting> {
        override fun createFromParcel(parcel: Parcel): Raiting {
            return Raiting(parcel)
        }

        override fun newArray(size: Int): Array<Raiting?> {
            return arrayOfNulls(size)
        }
    }

}