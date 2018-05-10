package ec.edu.unl.blockstudy.domain

import android.util.Log
import ec.edu.unl.blockstudy.domain.listeners.OnCallbackApis
import ec.edu.unl.blockstudy.domain.listeners.onDomainApiActionListener
import ec.edu.unl.blockstudy.entities.*
import io.objectbox.BoxStore

/**
 * Created by victor on 20/2/18.
 */
class ObjectBoxApi(var boxStore: BoxStore) {
    val TAG = "ObjectBoxApi"
    //    val questionaireBox = boxStore.boxFor(Questionaire::class.java)
    //val questionBox = boxStore.boxFor(Question::class.java)
    // val answerBox = boxStore.boxFor(Answer::class.java)
    val keywordBox = boxStore.boxFor(Keyword::class.java)
    val blockBox = boxStore.boxFor(Block::class.java)
    val applicationBox = boxStore.boxFor(Application::class.java)
    val questionnaireBlockBox = boxStore.boxFor(QuestionnaireBlock::class.java)


    fun createQuestionaire(questionaire: Questionaire, callback: onDomainApiActionListener) {
        //   questionaireBox.put(questionaire)
        Log.e("BOXDB", "Inserted new cuestionario, ID: " + questionaire.idQuestionaire)
        //val questionaire = questionaireBox.get(questionaire.idQuestionaire)
        //Log.e("BOXDB", questionaire.keyword[0].description)
        if (questionaire.idQuestionaire > 0) {
            callback.onSuccess(questionaire)
        } else {
            callback.onError("No se puedo crear el cuestionario")
        }
    }

    fun getMyQuestionnaries(idUser: String, callback: onDomainApiActionListener) {
        // callback.onSuccess(questionaireBox.query().equal(Questionaire_.idUser, idUser).build().find())
    }

    fun getDataQuestionnaire(idQuestionaire: Long, callback: onDomainApiActionListener) {
        //val questionaire = questionaireBox.query().equal(Questionaire_.idQuestionaire, idQuestionaire).build().findFirst()
        /*

               questionaire!!.questions.forEach {
                   Log.e(TAG, it.statement)
               }



               (questionBox.query().equal(Question_.questionaireId, idQuestionaire).build().find()).forEach {
                   Log.e(TAG, it.statement)
                   Log.e(TAG, it.answers.size.toString())
               }
               */
        //callback.onSuccess(questionaire)
    }

    fun onSetQuestion(question: Question, callback: onDomainApiActionListener) {
        //  questionBox.put(question)
        if (question.idQuestion > 0) {
            callback.onSuccess(question)
        } else {
            callback.onError("No se puedo crear el pregunta")
        }
    }

    fun onUpdateQuestion(question: Question, callback: onDomainApiActionListener) {
        boxStore.runInTx {
            question.answers.forEach {
                // answerBox.put(it)
            }
        }

        Log.e(TAG, question.photoUrl + "foto")
        //questionBox.put(question)
        if (question.idQuestion > 0) {
            callback.onSuccess(question)
        } else {
            callback.onError("No se puedo crear el pregunta")
        }
    }

    fun onDataQuestion(idQuestion: Any, callback: onDomainApiActionListener) {
        //  callback.onSuccess(questionBox.query().equal(Question_.__ID_PROPERTY, idQuestion).build().findUnique())
    }

    fun onDeleteQuestion(idQuestion: Long, callback: onDomainApiActionListener) {
        // questionBox.remove(idQuestion)
        callback.onSuccess(Any())

    }

    fun updateQuestionnaire(questionaire: Questionaire, callback: onDomainApiActionListener) {
        Log.e(TAG, "LLEGUE" + questionaire.idQuestionaire)
        //  questionaireBox.put(questionaire)
        callback.onSuccess(Any())
    }

    fun deleteQuestionnnaire(idQuestionaire: Long, callback: onDomainApiActionListener) {
        //  callback.onSuccess(questionaireBox.remove(idQuestionaire))
    }

    fun getQuestionsAll(idQuestionnaire: Long, callback: onDomainApiActionListener) {

    }

    fun getBlockData(idUser: String, callback: onDomainApiActionListener) {
        var block = blockBox.query().equal(Block_.idUser, idUser).build().findUnique()

        if (block != null) {
            callback.onSuccess(block)

        } else {
            var block = Block()
            block.idUser = idUser
            blockBox.put(block)
            callback.onSuccess(block)
        }

    }

    fun onUpdateBlock(block: Block, callback: onDomainApiActionListener) {
        Log.e(TAG, block.timeActivity.toString())
        blockBox.put(block)
        callback.onSuccess(block)
    }

    fun setApplications(apps: List<String>, id: Long, callback: onDomainApiActionListener) {
        applicationBox.removeAll()
        var list = ArrayList<Application>()
        apps.forEach {
            var app = Application()
            app.block.targetId = id
            app.app = it
            list.add(app)
        }
        applicationBox.put(list)
        callback.onSuccess(Any())
        Log.e("a", "se actualizo block $id y elementos ${list.size}")
    }

    fun addQuestionnaireBlock(questionaire: QuestionnaireBlock, callback: OnCallbackApis<Unit>) {
        questionnaireBlockBox.put(questionaire)
        callback.onSuccess(Unit)
    }

    fun removeQuestionnaireBlock(idQuestionaire: Long, callback: OnCallbackApis<Unit>) {
        questionnaireBlockBox.query().equal(QuestionnaireBlock_.idQuestionnaire, idQuestionaire).build().remove()
        questionnaireBlockBox.remove(idQuestionaire)
        callback.onSuccess(Unit)

    }


}