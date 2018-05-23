package ec.com.dovic.aprendiendo.entities

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import java.util.*

/**
 * Created by victor on 28/1/18.
 */
class Academic : Parcelable {
    var id = ""
    var school: String = ""
    var title: String = ""

    constructor() {

    }

    @Exclude
    fun toMapPost(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["school"] = school
        result["title"] = title
        return result
    }

    constructor(school: String, title: String) {
        this.school = school
        this.title = title
    }

    constructor(parcel: Parcel) : this() {
        school = parcel.readString()
        title = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(school)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Academic> {
        override fun createFromParcel(parcel: Parcel): Academic {
            return Academic(parcel)
        }

        override fun newArray(size: Int): Array<Academic?> {
            return arrayOfNulls(size)
        }
    }


}