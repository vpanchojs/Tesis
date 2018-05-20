package ec.edu.unl.blockstudy.entities

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import java.util.*

class Answer() : Parcelable {
    var idAnswer: Long = 0
    var idCloud: String = ""
    var statement: String? = ""
    var correct: Boolean? = false
    var select: Boolean? = false
    lateinit var idQuestion: String


    constructor(parcel: Parcel) : this() {
        idAnswer = parcel.readLong()
        statement = parcel.readString()
        correct = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    @Exclude
    fun toMapPost(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["statement"] = statement!!
        result["correct"] = correct!!
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(idAnswer)
        parcel.writeString(statement)
        parcel.writeValue(correct)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Answer> {
        override fun createFromParcel(parcel: Parcel): Answer {
            return Answer(parcel)
        }

        override fun newArray(size: Int): Array<Answer?> {
            return arrayOfNulls(size)
        }
    }
}