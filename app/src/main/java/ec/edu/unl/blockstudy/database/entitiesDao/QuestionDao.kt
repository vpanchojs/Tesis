package ec.edu.unl.blockstudy.database.entitiesDao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import ec.edu.unl.blockstudy.database.QuestionBd

@Dao
interface QuestionDao {

    @Query(value = "SELECT * FROM question")
    fun getQuestionAll(): List<QuestionBd>

    @Query(value = "SELECT * FROM question where questionnaire_id = :id")
    fun getQuestionOfQuestionnaire(id: Long): List<QuestionBd>

    @Insert
    fun insertQuestion(questionBd: QuestionBd): Long

}