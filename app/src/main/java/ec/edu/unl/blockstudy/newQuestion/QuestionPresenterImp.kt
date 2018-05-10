package ec.edu.unl.blockstudy.newQuestion

import ec.edu.unl.blockstudy.entities.Answer
import ec.edu.unl.blockstudy.entities.Question
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.newQuestion.events.QuestionEvents
import ec.edu.unl.blockstudy.newQuestion.ui.QuestionView
import org.greenrobot.eventbus.Subscribe

/**
 * Created by victor on 26/2/18.
 */
class QuestionPresenterImp(var eventBus: EventBusInterface, var view: QuestionView, var interactor: QuestionInteractor) : QuestionPresenter {

    override fun onResume() {
        eventBus.register(this)
    }

    override fun onPause() {
        eventBus.unregister(this)
    }

    override fun onCreateQuestion(url: String, anserws: ArrayList<Answer>, statement: String, idQuestionnaire: String) {
        view.showProgressDialog("Creando pregunta")
        interactor.onCreateQuestion(url, anserws, statement, idQuestionnaire)
    }

    override fun onGetDataQuestion(idQuestion: Any, idQuestionnaire: Any) {
        view.showProgressDialog("Obtienendo informacion de la pregunta")
        interactor.onGetDataQuestion(idQuestion, idQuestionnaire)
    }

    override fun updateQuestion(url_photo: String, answerList: ArrayList<Answer>, statement: String, idQuestion: Long, idQuestionnaire: Long) {
        // interactor.onUpdateQuestion(url_photo, answerList, statement, idQuestion, idQuestionnaire)
    }

    override fun updateQuestion(idQuestion: String, statament: String, photo_url: String, answerList: java.util.ArrayList<Answer>?, idQuestionnaire: String) {
        view.showProgressDialog("Actualizando pregunta")
        interactor.updateQuestion(idQuestion, statament, photo_url, answerList, idQuestionnaire)
    }

    override fun updateQuestion(question: Question) {
        view.showProgressDialog("Actualizando pregunta")
        // interactor.onUpdateQuestion(question)
    }

    override fun onDeteleQuestion(idQuestion: String, idQuestionnaire: String) {
        view.showProgressDialog("Eliminando Pregunta")
        interactor.onDeteleQuestion(idQuestion, idQuestionnaire)
    }

    @Subscribe
    override fun onEventThread(event: QuestionEvents) {
        when (event.type) {
            QuestionEvents.ON_CREATE_QUESTION_SUCCESS -> {
                view.hideProgressDialog()
                view.showMessagge("Pregunta Creada")
                view.setNavigationQuestionnnaire()
            }

            QuestionEvents.ON_CREATE_QUESTION_ERROR -> {
                view.hideProgressDialog()
                view.showMessagge(event.any.toString())
            }
            QuestionEvents.ON_GET_ANSWERS_SUCCESS -> {
                view.hideProgressDialog()
                var anserws = event.any as List<Answer>
                if (anserws.size > 0) {
                    view.setDataAnswers(anserws)
                } else {

                }

            }
            QuestionEvents.ON_GET_ANSWERS_ERROR -> {
                view.hideProgressDialog()
                view.showMessagge(event.any.toString())
            }
            QuestionEvents.ON_DELETE_QUESTION_SUCCESS -> {
                view.hideProgressDialog()
                view.showMessagge("Pregunta eliminada")
                view.setNavigationQuestionnnaire()
            }
            QuestionEvents.ON_UPDATE_QUESTION_SUCCESS -> {
                view.hideProgressDialog()
                view.showMessagge("Pregunta actualizada")
                view.setNavigationQuestionnnaire()
            }

        }
    }
}