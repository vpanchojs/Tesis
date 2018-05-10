package ec.edu.unl.blockstudy.entities

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import java.util.*

/**
 * Created by victor on 30/1/18.
 */
class Subject : Parcelable {
    var id: String = ""
    var name: String = ""
    var active: Boolean = false


    @Exclude
    fun toMapPost(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["name"] = name
        result["active"] = active
        return result
    }

    constructor(parcel: Parcel) {
        id = parcel.readString()
        name = parcel.readString()
        active = parcel.readByte() != 0.toByte()
    }

    constructor(id: String, name: String, active: Boolean) {
        this.id = id
        this.name = name
        this.active = active
    }

    constructor()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeByte(if (active) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Subject> {
        override fun createFromParcel(parcel: Parcel): Subject {
            return Subject(parcel)
        }

        override fun newArray(size: Int): Array<Subject?> {
            return arrayOfNulls(size)
        }
    }


}