package ec.com.dovic.aprendiendo.database.entitiesDao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import ec.com.dovic.aprendiendo.database.AnswerBd

@Dao
interface AnswerDao {

    @Query(value = "SELECT * FROM answer")
    fun getAnswerAll(): List<AnswerBd>

    @Query(value = "SELECT * FROM answer where question_id = :id")
    fun getAnswerOfQuestion(id: Long): List<AnswerBd>

    @Insert
    fun insertAnswer(answerBd: AnswerBd): Long
}
