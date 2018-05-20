package ec.edu.unl.blockstudy.questionnaireResume

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.domain.listeners.OnCallbackApis
import ec.edu.unl.blockstudy.domain.listeners.onDomainApiActionListener
import ec.edu.unl.blockstudy.entities.Question
import ec.edu.unl.blockstudy.entities.Raiting
import ec.edu.unl.blockstudy.entities.User
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.questionnaireResume.events.QuestionnaireResumeEvents


class QuestionnaireResumeRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi, var sharePreferencesApi: SharePreferencesApi, var objectBoxApi: ObjectBoxApi) : QuestionnaireResumeRepository {

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
                    raiting!!.me = it.id == firebaseApi.getUid()
                    ratingsList.add(raiting)
                }
                Log.e("aa", "enviando" + ratingsList.size)
                postEvent(QuestionnaireResumeEvents.ON_GET_RATINGS_SUCCESS, ratingsList!!)
            }

            override fun onError(error: Any?) {
                postEvent(QuestionnaireResumeEvents.ON_GET_RATINGS_ERROR, error!!)
            }
        })
    }

    override fun setRaiting(raiting: Raiting) {
        firebaseApi.onSetRaiting(raiting, object : onDomainApiActionListener {
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
