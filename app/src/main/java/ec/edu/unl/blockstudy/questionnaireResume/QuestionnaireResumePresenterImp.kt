package ec.edu.unl.blockstudy.questionnaireResume

import android.util.Log
import ec.edu.unl.blockstudy.entities.Question
import ec.edu.unl.blockstudy.entities.Raiting
import ec.edu.unl.blockstudy.entities.User
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.questionnaireResume.events.QuestionnaireResumeEvents
import ec.edu.unl.blockstudy.questionnaireResume.ui.QuestionnaireResumeView
import org.greenrobot.eventbus.Subscribe

class QuestionnaireResumePresenterImp(var eventBus: EventBusInterface, var view: QuestionnaireResumeView, var interactor: QuestionnaireResumeInteractor) : QuestionnaireResumePresenter {

    override fun onSuscribe() {
        eventBus.register(this)
    }

    override fun onUnSuscribe() {
        eventBus.unregister(this)
    }

    override fun onGetQuestionAll(idQuestionnaire: Any) {
        interactor.onGetQuestionAll(idQuestionnaire)
    }

    override fun onGetUser(idUser: Any) {
        interactor.onGetUser(idUser)
    }

    override fun onGetRaitingsAll(idQuestionnaire: Any) {
        interactor.onGetRaitingsAll(idQuestionnaire)
    }

    override fun setRaiting(idQuestionnaire: Any, raiting: Double, message: String) {
        interactor.setRaiting(idQuestionnaire, raiting, message)
    }

    override fun getQuestionnaire(idQuestionnaire: String) {
        interactor.getQuestionnaire(idQuestionnaire)
    }

    @Subscribe
    override fun onEventThread(event: QuestionnaireResumeEvents) {
        when (event.type) {
            QuestionnaireResumeEvents.ON_GET_QUESTIONS_SUCCESS -> {
                var questionList = event.any as List<Question>
                if (questionList.size > 0) {
                    view.none_results(false)
                    view.setQuestions(questionList)
                } else {
                    view.none_results(true)
                }
            }
            QuestionnaireResumeEvents.ON_GET_QUESTIONS_ERROR -> {

            }

            QuestionnaireResumeEvents.ON_GET_USER_SUCCESS -> {
                view.setUser(event.any as User)
            }

            QuestionnaireResumeEvents.ON_GET_USER_ERROR -> {

            }
            QuestionnaireResumeEvents.ON_SET_RATING_SUCCESS -> {
                view.showMessagge("Calificado")
                view.updateRating(event.type.toDouble())
            }
            QuestionnaireResumeEvents.ON_SET_RATING_ERROR -> {

            }

            QuestionnaireResumeEvents.ON_GET_RATINGS_SUCCESS -> {
                Log.e("aa", "llego al evento")
                var ratingList = event.any as List<Raiting>
                if (ratingList.size > 0) {
                    //   view.none_results(false)
                    view.setRatings(ratingList)
                } else {
                    // view.none_results(true)
                }
            }
            QuestionnaireResumeEvents.ON_GET_RATINGS_ERROR -> {

            }
        }
    }
}

