package ec.edu.unl.blockstudy.blockResume

import com.google.firebase.firestore.DocumentReference
import ec.edu.unl.blockstudy.entities.Block
import ec.edu.unl.blockstudy.entities.QuestionPath
import ec.edu.unl.blockstudy.entities.QuestionnaireBlock

class BlockResumeInteractorImp(var repository: BlockResumeRepository) : BlockResumeInteractor {

    override fun setTimeActivity(block: Block) {
        repository.setTimeActivity(block)
    }

    override fun setApplications(apps: List<String>, id: Long) {
        repository.setApplications(apps, id)
    }

    override fun getDataBlock() {
        repository.getDataBlock()
    }

    override fun getQuestionnaires() {
        repository.getQuestionnaires()
    }

    override fun removeQuestionnaire(idQuestionaire: Long) {
        repository.removeQuestionnaire(idQuestionaire)
    }

    override fun addQuestionnaire(idQuestionaire: Long, idCloud: String, idBlock: Long, refQuestions: ArrayList<DocumentReference>) {
        val questionaire = QuestionnaireBlock()
        questionaire.idCloud = idCloud
        questionaire.idQuestionnaire = idQuestionaire
        questionaire.block.targetId = idBlock
        questionaire.questionsPath.addAll(getQuestionsPath(refQuestions, idQuestionaire))
        repository.addQuestionnaire(questionaire)
    }

    fun getQuestionsPath(refQuestions: ArrayList<DocumentReference>, idQuestionaire: Long): List<QuestionPath> {
        val questionPathList = ArrayList<QuestionPath>()
        refQuestions.forEach {
            var questionPath = QuestionPath()
            questionPath.path = it.path
            questionPath.QuestionnaireBlock.targetId = idQuestionaire
            questionPathList.add(questionPath)
        }

        return questionPathList
    }
}
