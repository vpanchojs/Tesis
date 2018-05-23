package ec.com.dovic.aprendiendo.database.entitiesDao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import ec.com.dovic.aprendiendo.database.QuestionnaireBd

@Dao
interface QuestionnaireDao {

    @Insert
    fun insertQuestionnaire(questionnaireBd: QuestionnaireBd): Long

    @Query(value = "SELECT * FROM questionnnaire")
    fun getQuestionnaireAll(): List<QuestionnaireBd>

    @Delete
    fun deleteQuestionnaire(id: QuestionnaireBd): Int

    @Query(value = "UPDATE questionnnaire set block_id= :idBlock where id=:id")
    fun updateIdBlock(id: Long, idBlock: Long)

    @Query(value = "SELECT * FROM questionnnaire where block_id=:id")
    fun getQuestionnaireByBlock(id: Long): List<QuestionnaireBd>
}