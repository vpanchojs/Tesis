package ec.com.dovic.aprendiendo.questionsComplete

import android.util.Log
import ec.com.dovic.aprendiendo.database.QuestionBd
import ec.com.dovic.aprendiendo.database.QuestionnaireBd
import ec.com.dovic.aprendiendo.entities.Answer
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.newQuestion.events.QuestionEvents
import ec.com.dovic.aprendiendo.questionsComplete.events.QuestionCompleteEvents
import ec.com.dovic.aprendiendo.questionsComplete.ui.QuestionCompleteView
import org.greenrobot.eventbus.Subscribe

/**
 * Created by victor on 5/3/18.
 */
class QuestionCompletePresenterImp(var eventBus: EventBusInterface, var view: QuestionCompleteView, var interactor: QuestionCompleteInteractor) : QuestionCompletePresenter {

    override fun onSuscribe() {
        eventBus.register(this)
    }

    override fun onUnSuscribe() {
        eventBus.unregister(this)
    }

    override fun onGetQuestionAll(idQuestionnaire: Any) {
        interactor.onGetQuestionAll(idQuestionnaire)
    }

    override fun onGetAnswersQuestion(idQuestionnaire: String, idCloud: String) {
        interactor.onGetAnswersQuestion(idQuestionnaire, idCloud)
    }

    override fun deleteQuestionnarie(questionnaireBd: QuestionnaireBd) {
        interactor.deleteQuestionnarie(questionnaireBd)
    }

    @Subscribe
    override fun onEventThread(event: QuestionCompleteEvents) {
        when (event.type) {
            QuestionCompleteEvents.ON_GET_QUESTIONS_SUCCESS -> {
                var questionList = event.any as List<QuestionBd>
                if (questionList.size > 0) {
                    Log.e("pre", "si esta ")
                    view.setQuestions(questionList)
                } else {
                    view.none_results(true)
                }

            }

            QuestionCompleteEvents.ON_GET_ANSWERS_SUCCESS -> {
                var answerList = event.any as List<Answer>
                if (answerList.size > 0)
                    view.setAnswer(answerList)
                else
                    view.none_results(true)
            }

            QuestionCompleteEvents.ON_DELETE_SUCCESS->{
                view.closeActivity()
            }

        }
    }
}