package ec.edu.unl.blockstudy.entities

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import io.objectbox.annotation.Id
import java.util.*

/**
 * Created by victor on 6/2/18.
 */
class Question() : Parcelable {
    @Id
    @get:Exclude
    var idQuestion: Long = 0
    @get:Exclude
    lateinit var idCloud: String
    var statement: String = " "
    @get:Exclude
    var type: Int? = null
    var photoUrl: String = ""
    @get:Exclude
    lateinit var idQuestionnnaire: String

    lateinit var answers: List<Answer>

    @Exclude
    lateinit var hashAnswers: List<Map<String, Any>>

    constructor(parcel: Parcel) : this() {
        idQuestion = parcel.readLong()
        idCloud = parcel.readString()
        statement = parcel.readString()
        type = parcel.readValue(Int::class.java.classLoader) as? Int
        photoUrl = parcel.readString()
        //  idQuestionnnaire = parcel.readString()
        answers = parcel.createTypedArrayList(Answer)
    }


    @Exclude
    fun toMapPost(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["statement"] = statement!!
        result["photoUrl"] = photoUrl!!
        result["answers"] = hashAnswers
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(idQuestion)
        parcel.writeString(idCloud)
        parcel.writeString(statement)
        parcel.writeValue(type)
        parcel.writeString(photoUrl)
//        parcel.writeString(idQuestionnnaire)
        parcel.writeTypedList(answers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }


}