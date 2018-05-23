package ec.edu.unl.blockstudy.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "answer",
        foreignKeys = arrayOf(ForeignKey(entity = QuestionBd::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("question_id"),
                onDelete = ForeignKey.CASCADE)))

class AnswerBd {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var statement: String? = ""
    var correct: Boolean? = false
    var select: Boolean? = false

    @ColumnInfo(name = "question_id")
    var questionId: Long? = null


}