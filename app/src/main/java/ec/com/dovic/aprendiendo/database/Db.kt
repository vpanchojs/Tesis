package ec.com.dovic.aprendiendo.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import ec.com.dovic.aprendiendo.database.entitiesDao.*

@Database(entities = arrayOf(Block::class, Application::class, QuestionBd::class, QuestionnaireBd::class, AnswerBd::class), version = 1)
abstract class Db : RoomDatabase() {

    abstract fun blockDao(): BlockDao
    abstract fun questionnaireDao(): QuestionnaireDao
    abstract fun questionDao(): QuestionDao
    abstract fun answerDao(): AnswerDao
    abstract fun applicationDao(): ApplicationDao

}