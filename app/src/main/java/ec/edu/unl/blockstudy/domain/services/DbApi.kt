package ec.edu.unl.blockstudy.domain.services

import android.util.Log
import ec.edu.unl.blockstudy.database.*
import ec.edu.unl.blockstudy.domain.listeners.OnCallbackApis
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class DbApi(var db: Db) {


    fun getMyQuestionnaires(callback: OnCallbackApis<List<QuestionnaireBd>>) {
        Log.e("bd", "consultado en la bd")
        var questionnaires: List<QuestionnaireBd>
        doAsync {
            questionnaires = db.questionnaireDao().getQuestionnaireAll()
            uiThread {
                callback.onSuccess(questionnaires)
            }
        }
    }


    fun getQuestionsAllForQuestionnairesAll(idsQuestionnaires: ArrayList<Long>, callback: OnCallbackApis<List<QuestionBd>>) {
        var questionsList = arrayListOf<QuestionBd>()

        doAsync {
            idsQuestionnaires.forEach {
                var questions = db.questionDao().getQuestionOfQuestionnaire(it)
                questions.forEach {
                    it.answers.addAll(db.answerDao().getAnswerOfQuestion(it.id))
                    Log.e("db", "respuestas ${it.answers.size}")
                }
                questionsList.addAll(questions)
            }

            uiThread {
                callback.onSuccess(questionsList)
            }
        }

    }


    fun getQuestions(idQuestionnaire: Long, callback: OnCallbackApis<List<QuestionBd>>) {
        var questions: List<QuestionBd>
        Log.e("db", "id cuestionario $idQuestionnaire")
        doAsync {
            questions = db.questionDao().getQuestionOfQuestionnaire(idQuestionnaire)
            Log.e("db", "preguntas ${questions.size}")
            questions.forEach {

                it.answers.addAll(db.answerDao().getAnswerOfQuestion(it.id.toLong()))
                Log.e("db", "respuestas ${it.answers.size}")
            }

            uiThread {
                callback.onSuccess(questions)
            }
        }

    }

    fun insertQuestionnaire(questionnaireBd: QuestionnaireBd, callback: OnCallbackApis<Long>) {
        var id: Long
        Thread({
            id = db.questionnaireDao().insertQuestionnaire(questionnaireBd)
            callback.onSuccess(id)
        }).start()

    }

    fun insertQuestion(questionBd: QuestionBd, callback: OnCallbackApis<Long>) {
        var id: Long
        Thread({
            id = db.questionDao().insertQuestion(questionBd)
            callback.onSuccess(id)
        }).start()

    }

    fun insertAnswer(answerBd: AnswerBd, callback: OnCallbackApis<Long>) {
        var id: Long = 0
        Thread({
            id = db.answerDao().insertAnswer(answerBd)
            callback.onSuccess(id)
        }).start()

    }

    fun deleteQuestionnaire(questionnaireBd: QuestionnaireBd, callcack: OnCallbackApis<Int>) {
        var num: Int
        doAsync {
            num = db.questionnaireDao().deleteQuestionnaire(questionnaireBd)
            if (num > 0)
                callcack.onSuccess(num)
            else
                callcack.onError(Any())
        }

    }

    fun getBlock(id: String, callback: OnCallbackApis<Block>) {

        var block: Block
        doAsync {
            Log.e("aa", "gggg" + db.blockDao().getBlockById(id))
            block = db.blockDao().getBlockById(id)
            if (block == null) {
                var blockAux = Block()
                blockAux.idUser = id
                blockAux.id = db.blockDao().addBlock(blockAux)
                callback.onSuccess(blockAux)
            } else {
                callback.onSuccess(block)
            }
        }

    }

    fun setApplications(app: List<Application>, onCallbackApis: OnCallbackApis<Int>) {
        var num: Int
        Log.e("db", "aplicaciontions")

        Thread({
            Log.e("db", "eliminando aplicaciones")
            db.applicationDao().deleteAll()
            num = db.applicationDao().insert(app).size
            Log.e("db", "se insertaron $num")
            if (num > 0) {
                Log.e("db", "success aplicaciontions")
                onCallbackApis.onSuccess(num)
            } else {
                Log.e("db", "error aplicaciontions")
                onCallbackApis.onSuccess(num)
            }

        }).start()


        /*
        doAsync {


            num = db.applicationDao().insert(app).size

            if (num > 0) {
                Log.e("db", "success aplicaciontions")
                onCallbackApis.onSuccess(num)
            } else {
                Log.e("db", "error aplicaciontions")
                onCallbackApis.onSuccess(num)
            }

        }
        */
    }

    fun getApplicationsbyIdBlock(id: Long, onCallbackApis: OnCallbackApis<List<Application>>) {
        var applicationsList: List<Application>
        Thread({
            Log.e("db", " obteniendo aplicaciones")
            applicationsList = db.applicationDao().getApplicationByBlock(id)
            Log.e("db", " ${applicationsList.size} aplicaciones")
            onCallbackApis.onSuccess(applicationsList)
        }).start()
    }

    fun addorRemoveQuestionnaireBlock(id: Long, idBlock: Long, onCallbackApis: OnCallbackApis<Unit>) {
        Thread({
            db.questionnaireDao().updateIdBlock(id, idBlock)
            onCallbackApis.onSuccess(Unit)
        }).start()

    }

    fun updateTimeBlock(time: Int, onCallbackApis: OnCallbackApis<Int>) {
        Thread({
            db.blockDao().updateTimeBlock(time)
            onCallbackApis.onSuccess(time)
        }).start()
    }


}