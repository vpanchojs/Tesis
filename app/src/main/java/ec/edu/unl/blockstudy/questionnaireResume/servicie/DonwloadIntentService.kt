package ec.edu.unl.blockstudy.questionnaireResume.servicie

import android.app.IntentService
import android.content.Intent
import android.util.Log
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi


/**
 * Created by victor on 28/3/18.
 */
class DonwloadIntentService : IntentService("DonwloadIntentService") {

    lateinit var application: MyApplication
    lateinit var firebaseApi: FirebaseApi
    lateinit var objectBoxApi: ObjectBoxApi

    companion object {
        val IDQUESTIONNAIRE = "id"
    }

    override fun onCreate() {
        super.onCreate()
        setupInjection()
    }

    private fun setupInjection() {
        application = getApplication() as MyApplication
        firebaseApi = application.domainModule!!.providesFirebaseApi()
        objectBoxApi = application.domainModule!!.providesObjectBoxApi()
    }

    override fun onHandleIntent(p0: Intent?) {
        var idQuestionnaire = p0!!.getStringExtra(IDQUESTIONNAIRE)
        Log.e("servicie", idQuestionnaire)

        // firebaseApi.getQuestionnarie()

    }

}