package ec.com.dovic.aprendiendo.questionnaireResume

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.listeners.OnCallbackApis
import ec.com.dovic.aprendiendo.domain.listeners.onDomainApiActionListener
import ec.com.dovic.aprendiendo.domain.services.DbApi
import ec.com.dovic.aprendiendo.entities.Question
import ec.com.dovic.aprendiendo.entities.Raiting
import ec.com.dovic.aprendiendo.entities.User
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.questionnaireResume.events.QuestionnaireResumeEvents


class QuestionnaireResumeRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi, var dbApi: DbApi) : QuestionnaireResumeRepository {


    override fun isExistQuestionnnaireLocal(idCloud: String) {
        dbApi.isExistQuestionnaire(firebaseApi.getUid(), idCloud, object : OnCallbackApis<Boolean> {
            override fun onSuccess(response: Boolean) {
                Log.e("res", "repondio")
                postEvent(QuestionnaireResumeEvents.ON_IS_EXIST_QUESTIONNNAIRE_LOCAL, response)
            }

            override fun onError(error: Any?) {

            }
        })
    }

    override fun isDownloaded(idQuestionnaire: String) {
        firebaseApi.isDownLoadedQuestionnaire(idQuestionnaire, object : OnCallbackApis<Boolean> {
            override fun onSuccess(response: Boolean) {
                postEvent(QuestionnaireResumeEvents.ON_GET_IS_DOWNLOADED_SUCCESS, response)
            }

            override fun onError(error: Any?) {

            }
        })
    }

    override fun getQuestionnaire(idQuestionnaire: String) {
        /*
        firebaseApi.getQuestionsComplete(idQuestionnaire, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {

            }

            override fun onError(error: Any?) {

            }
        })
        */
    }


    override fun onGetQuestionAll(idQuestionnaire: Any) {
        firebaseApi.getQuestions(idQuestionnaire.toString(), object : OnCallbackApis<QuerySnapshot> {
            override fun onSuccess(response: QuerySnapshot) {

                var questionsList = ArrayList<Question>()
                response.documents.forEach {
                    var question = it.toObject(Question::class.java)
                    question!!.idCloud = it.id
                    questionsList.add(question)
                }

                postEvent(QuestionnaireResumeEvents.ON_GET_QUESTIONS_SUCCESS, questionsList!!)
            }

            override fun onError(error: Any?) {
                postEvent(QuestionnaireResumeEvents.ON_GET_QUESTIONS_ERROR, error!!)
            }
        })
    }

    override fun onGetRaitingsAll(idQuestionnaire: Any) {
        Log.e("aa", "LLEGUE")
        firebaseApi.onGetRatingsAll(idQuestionnaire, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                var aux = response as QuerySnapshot
                var ratingsList = ArrayList<Raiting>()
                aux.documents.forEach {
                    var raiting = it.toObject(Raiting::class.java)
                    raiting!!.idRaiting = it.id
                    raiting.me = it.id == firebaseApi.getUid()
                    ratingsList.add(raiting)
                }
                Log.e("aa", "enviando" + ratingsList.size)
                postEvent(QuestionnaireResumeEvents.ON_GET_RATINGS_SUCCESS, ratingsList)
            }

            override fun onError(error: Any?) {
                postEvent(QuestionnaireResumeEvents.ON_GET_RATINGS_ERROR, error!!)
            }
        })
    }

    override fun setRaiting(raiting: Raiting, update: Boolean, oldRaiting: Double) {
        firebaseApi.onSetRaiting(raiting, update,oldRaiting, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(QuestionnaireResumeEvents.ON_SET_RATING_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {
                postEvent(QuestionnaireResumeEvents.ON_SET_RATING_ERROR, error!!)
            }
        })
    }

    override fun onGetUser(idUser: Any) {
        firebaseApi.onGetUser(idUser, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                response as DocumentSnapshot
                var user = response.toObject(User::class.java)
                user!!.idUser = response.id
                postEvent(QuestionnaireResumeEvents.ON_GET_USER_SUCCESS, user!!)
            }

            override fun onError(error: Any?) {
                postEvent(QuestionnaireResumeEvents.ON_GET_USER_ERROR, error!!)
            }
        })
    }

    override fun postEvent(type: Int, any: Any) {
        var event = QuestionnaireResumeEvents(type, any)
        eventBus.post(event)
    }
}
