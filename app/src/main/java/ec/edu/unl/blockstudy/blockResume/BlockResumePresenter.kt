package ec.edu.unl.blockstudy.blockResume

import com.google.firebase.firestore.DocumentReference
import ec.edu.unl.blockstudy.entities.Block
import ec.edu.unl.blockstudy.util.Presenter

interface BlockResumePresenter : Presenter {

    fun setTimeActivity(time: Block)

    fun setApplications(apps: List<String>, id: Long)

    fun getDataBlock()

    fun getQuestionnaires()

    fun addQuestionnaire(idQuestionaire: Long, idCloud: String, idBlock: Long, refQuestions: ArrayList<DocumentReference>)

    fun removeQuestionnaire(idQuestionaire: Long)


}
