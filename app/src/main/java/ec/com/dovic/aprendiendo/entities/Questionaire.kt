package ec.com.dovic.aprendiendo.entities

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by victor on 6/2/18.
 */
class Questionaire() : Parcelable {

    var idQuestionaire: Long = 0
    lateinit var idCloud: String
    var title: String? = null
    var idUser: String? = null
    var description: String? = null
    var createDate: Date? = null
    var difficulty: Int = 0
    var subject: String = ""
    var postDate: Date? = null
    var numberQuest: Int = 0
    var assessment: Double = 0.0
    var numAssessment: Int = 0
    var numberDonwloads: Int = 0
    var post: Boolean = false
    var keywords: String = ""


    @Transient
    var questions = ArrayList<Question>()

    @Transient
    var refQuestions = ArrayList<DocumentReference>()


    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["title"] = title!!
        result["description"] = description!!
        result["idUser"] = idUser!!
        result["createDate"] = FieldValue.serverTimestamp()
        result["numberQuest"] = numberQuest
        result["subject"] = subject
        result["post"] = post
        return result
    }

    @Exclude
    fun toMapAux(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["title"] = title!!
        result["description"] = description!!
        result["idUser"] = idUser!!
        result["createDate"] = FieldValue.serverTimestamp()
        result["postDate"] = FieldValue.serverTimestamp()
        result["numberQuest"] = numberQuest
        result["subject"] = subject
        result["keywords"] = keywords
        result["assessment"] = assessment
        result["numAssessment"] = numAssessment
        result["numberDonwloads"] = numberDonwloads
        result["post"] = post
        return result
    }

    @Exclude
    fun toMapQuestions(aux: List<Map<String, Any>>): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["refQuestions"] = aux
        return result
    }


    @Exclude
    fun toMapRating(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["numAssessment"] = numAssessment
        result["assessment"] = assessment

        return result
    }

    @Exclude
    fun toMapPost(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["title"] = title!!
        result["description"] = description!!
        result["postDate"] = FieldValue.serverTimestamp()
        result["numberQuest"] = numberQuest
        result["post"] = post
        result["keywords"] = keywords
        result["assessment"] = assessment
        result["numAssessment"] = numAssessment
        result["difficulty"] = difficulty
        result["subject"] = subject
        return result
    }


    @Exclude
    fun toMapInfoBasic(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["title"] = title!!
        result["description"] = description!!
        return result
    }


    constructor(parcel: Parcel) : this() {
        idQuestionaire = parcel.readLong()
        idCloud = parcel.readString()
        title = parcel.readString()
        idUser = parcel.readString()
        keywords = parcel.readString()
        description = parcel.readString()
        difficulty = parcel.readInt()
        numberQuest = parcel.readInt()
        assessment = parcel.readDouble()
        numberDonwloads = parcel.readInt()
        subject = parcel.readString()
        post = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(idQuestionaire)
        parcel.writeString(idCloud)
        parcel.writeString(title)
        parcel.writeString(idUser)
        parcel.writeString(keywords)
        parcel.writeString(description)
        parcel.writeInt(difficulty)
        parcel.writeInt(numberQuest)
        parcel.writeDouble(assessment)
        parcel.writeInt(numberDonwloads)
        parcel.writeString(subject)
        parcel.writeByte(if (post) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun toMapDownload(): MutableMap<String, Any> {
        val result = HashMap<String, Any>()
        result["numberDonwloads"] = numberDonwloads
        return result
    }

    companion object CREATOR : Parcelable.Creator<Questionaire> {
        override fun createFromParcel(parcel: Parcel): Questionaire {
            return Questionaire(parcel)
        }

        override fun newArray(size: Int): Array<Questionaire?> {
            return arrayOfNulls(size)
        }
    }
}
