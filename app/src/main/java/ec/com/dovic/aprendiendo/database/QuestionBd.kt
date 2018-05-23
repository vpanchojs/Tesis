package ec.com.dovic.aprendiendo.database

import android.arch.persistence.room.*
import android.os.Parcel
import android.os.Parcelable


@Entity(tableName = "question",
        foreignKeys = arrayOf(ForeignKey(entity = QuestionnaireBd::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("questionnaire_id"),
                onDelete = ForeignKey.CASCADE)))

class QuestionBd() : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var statement: String = ""
    var photoUrl: String = ""

    @Ignore
    var answers = ArrayList<AnswerBd>()

    @ColumnInfo(name = "questionnaire_id")
    var questionnaireId: Long = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        statement = parcel.readString()
        photoUrl = parcel.readString()
        questionnaireId = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(statement)
        parcel.writeString(photoUrl)
        parcel.writeLong(questionnaireId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionBd> {
        override fun createFromParcel(parcel: Parcel): QuestionBd {
            return QuestionBd(parcel)
        }

        override fun newArray(size: Int): Array<QuestionBd?> {
            return arrayOfNulls(size)
        }
    }


}