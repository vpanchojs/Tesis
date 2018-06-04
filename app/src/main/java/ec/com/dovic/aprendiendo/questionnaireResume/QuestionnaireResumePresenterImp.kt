package ec.com.dovic.aprendiendo.questionnaireResume

import android.util.Log
import android.view.View
import ec.com.dovic.aprendiendo.entities.Question
import ec.com.dovic.aprendiendo.entities.Raiting
import ec.com.dovic.aprendiendo.entities.User
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.questionnaireResume.events.QuestionnaireResumeEvents
import ec.com.dovic.aprendiendo.questionnaireResume.ui.QuestionnaireResumeView
import org.greenrobot.eventbus.Subscribe

class QuestionnaireResumePresenterImp(var eventBus: EventBusInterface, var view: QuestionnaireResumeView, var interactor: QuestionnaireResumeInteractor) : QuestionnaireResumePresenter {

    override fun onSuscribe() {
        eventBus.register(this)
    }

    override fun onUnSuscribe() {
        eventBus.unregister(this)
    }

    override fun onGetQuestionAll(idQuestionnaire: Any) {
        view.showProgress(View.VISIBLE)
        interactor.onGetQuestionAll(idQuestionnaire)
    }

    override fun onGetUser(idUser: Any) {
        interactor.onGetUser(idUser)
    }

    override fun onGetRaitingsAll(idQuestionnaire: Any) {
        interactor.onGetRaitingsAll(idQuestionnaire)
    }

    override fun setRaiting(idQuestionnaire: Any, raiting: Double, message: String, update: Boolean, oldRaiting: Double) {
        view.showButtonRaiting(View.INVISIBLE)
        interactor.setRaiting(idQuestionnaire, raiting, message, update, oldRaiting)
    }

    override fun getQuestionnaire(idQuestionnaire: String) {
        interactor.getQuestionnaire(idQuestionnaire)
    }

    override fun isDownloaded(idQuestionnaire: String) {
        interactor.isDownloaded(idQuestionnaire)
    }

    override fun isExistQuestionnnaireLocal(idCloud: String) {
        interactor.isExistQuestionnnaireLocal(idCloud)
    }

    @Subscribe
    override fun onEventThread(event: QuestionnaireResumeEvents) {
        when (event.type) {
            QuestionnaireResumeEvents.ON_GET_QUESTIONS_SUCCESS -> {
                view.showProgress(View.GONE)
                var questionList = event.any as List<Question>
                if (questionList.size > 0) {
                    view.none_results(false)
                    view.setQuestions(questionList)
                } else {
                    view.none_results(true)
                }
            }
            QuestionnaireResumeEvents.ON_GET_QUESTIONS_ERROR -> {
                view.showProgress(View.GONE)
                view.showMessagge("Problemas obteniendo preguntas")
            }

            QuestionnaireResumeEvents.ON_GET_IS_DOWNLOADED_SUCCESS -> {

                if (event.any as Boolean) {
                    view.showButtonRaiting(View.VISIBLE)
                } else {
                    view.showButtonRaiting(View.INVISIBLE)
                }
                view.setDownload(event.any as Boolean)

            }

            QuestionnaireResumeEvents.ON_GET_USER_SUCCESS -> {
                view.setUser(event.any as User)
            }

            QuestionnaireResumeEvents.ON_GET_USER_ERROR -> {

            }
            QuestionnaireResumeEvents.ON_SET_RATING_SUCCESS -> {
                view.showButtonRaiting(View.VISIBLE)
                view.showMessagge("Calificado ")
                view.updateRating(event.any as Raiting)
            }
            QuestionnaireResumeEvents.ON_SET_RATING_ERROR -> {
                view.showButtonRaiting(View.VISIBLE)
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

            QuestionnaireResumeEvents.ON_IS_EXIST_QUESTIONNNAIRE_LOCAL -> {
                Log.e("PRE", "SI HUBO RESPUESTA ${event.any as Boolean}")
                if (event.any as Boolean) {
                    view.confirmDownloadQuestionnaire()
                } else {
                    view.dowloadQuestionnaire()
                }
            }

        }
    }
}

