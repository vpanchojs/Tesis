package ec.com.dovic.aprendiendo.questionnaireResume.ui

import android.content.*
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import ec.com.dovic.aprendiendo.MyApplication
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.entities.Question
import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.entities.Score
import ec.com.dovic.aprendiendo.entities.User
import ec.com.dovic.aprendiendo.questionnaireResume.QuestionnaireResumePresenter
import ec.com.dovic.aprendiendo.questionnaireResume.adapter.QuestionAdapter
import ec.com.dovic.aprendiendo.questionnaireResume.adapter.RatingsAdapter
import ec.com.dovic.aprendiendo.questionnaireResume.servicie.DonwloadIntentService
import ec.com.dovic.aprendiendo.util.BaseActivitys
import kotlinx.android.synthetic.main.activity_questionnaire_resume.*
import kotlinx.android.synthetic.main.bottom_sheet_raiting.*
import kotlinx.android.synthetic.main.content_questionnaire_resume.*
import javax.inject.Inject

class QuestionnaireResumeActivity : AppCompatActivity(), QuestionnaireResumeView, View.OnClickListener, RatingFragment.OnRatingListener {

    override fun onSetRaiting(value: Double, comment: String, update: Boolean, oldRaiting: Double) {
        Log.e("RA", "VALUE" + value)
        presenter.setRaiting(questionaire.idCloud, value, comment, update, oldRaiting)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.cl_header_bs -> {
                when (bottomSheetBehavior.state) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
            R.id.btn_raiting -> {
                val ratingFragment = RatingFragment.newInstance(getRaitingMe())
                ratingFragment.show(supportFragmentManager, "Calificar")
            }
            R.id.btn_get_questionnaire -> {
                //Consultar si existe un cuestionario
                presenter.isExistQuestionnnaireLocal(questionaire.idCloud)
            }
        }
    }

    fun getRaitingMe(): Score? {
        return ratingsList.findLast {
            it.me == true
        }
    }

    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    var questionList = ArrayList<Question>()
    var ratingsList = ArrayList<Score>()
    var is_download = false
    lateinit var brDownLoad: BroadcastReceiver

    lateinit var application: MyApplication
    lateinit var adapter: QuestionAdapter
    lateinit var adapterRating: RatingsAdapter

    @Inject
    lateinit var presenter: QuestionnaireResumePresenter


    lateinit var questionaire: Questionaire

    companion object {
        val PARAM_QUESTIONNAIRE = "questionnaire"
        val ACTION_NOTIFY_DOWNLOAD = "action_download"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionnaire_resume)
        questionaire = intent.getParcelableExtra(PARAM_QUESTIONNAIRE)
        setDataQuestionnaire()
        toolbar.setTitle("Resumen Cuestionario")
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setupBroadcast()
        setupInjection()
        setupEvents()
        setupBottomSheet()
        setupRecyclerView()
    }

    private fun setupBroadcast() {
        brDownLoad = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p1!!.getBooleanExtra("success", false)) {
                    progressbar_down.visibility = View.INVISIBLE
                    btn_get_questionnaire.visibility = View.VISIBLE
                    btn_get_questionnaire.isEnabled = false
                    btn_get_questionnaire.text = "Descargado"
                    showButtonRaiting(View.VISIBLE)
                }
            }
        }
    }

    private fun setupEvents() {
        cl_header_bs.setOnClickListener(this)
        btn_raiting.setOnClickListener(this)
        btn_get_questionnaire.setOnClickListener(this)
    }

    fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        iv_icon_open.rotation = 0F
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        iv_icon_open.rotation = 180F

                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }

    private fun setDataQuestionnaire() {
        tv_title.setText(questionaire.title)
        tv_description.setText(questionaire.description)
        tv_category.text = questionaire.subject
        if (questionaire.assessment <= 0.0) {
            tv_raiting.setText("Sin")
        } else {
            tv_raiting.setText(questionaire.assessment.toString())
        }
        tv_questions_num.setText(questionaire.numberQuest.toString())

        tv_subtitle_bs.text = "${questionaire.numAssessment} calificaciones"
    }

    private fun setupInjection() {
        application = getApplication() as MyApplication
        application.getQuestionnaireResumeComponent(this).inject(this)
    }

    fun setupRecyclerView() {
        adapter = QuestionAdapter(questionList)
        adapterRating = RatingsAdapter(ratingsList)


        val mDividerItemDecoration = DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL)

        rv_questions.addItemDecoration(mDividerItemDecoration)
        rv_questions.layoutManager = LinearLayoutManager(this)
        rv_raitings.addItemDecoration(mDividerItemDecoration)
        rv_raitings.layoutManager = LinearLayoutManager(this)

        rv_questions.adapter = adapter
        rv_raitings.adapter = adapterRating

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navigationBack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        presenter.onUnSuscribe()
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(brDownLoad)
    }

    override fun onResume() {
        super.onResume()
        presenter.onSuscribe()
        presenter.onGetQuestionAll(questionaire.idCloud)
        presenter.getQuestionnaire(questionaire.idCloud)
        //presenter.onGetUser(questionaire.idUser!!)
        presenter.onGetRaitingsAll(questionaire.idCloud)
        presenter.isDownloaded(questionaire.idCloud)
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(brDownLoad, IntentFilter(ACTION_NOTIFY_DOWNLOAD))
    }

    override fun showMessagge(message: Any) {
        BaseActivitys.showToastMessage(this, message, Toast.LENGTH_SHORT)
    }

    override fun showProgress(visibility: Int) {
        progressbar_questions.visibility = visibility
    }

    override fun none_results(show: Boolean) {
        if (show)
            tv_none_questions.visibility = View.VISIBLE
        else
            tv_none_questions.visibility = View.GONE
    }

    override fun navigationBack() {
        finish()
    }

    override fun setQuestions(questionList: List<Question>) {
        this.questionList.clear()
        this.questionList.addAll(questionList)
        adapter.notifyDataSetChanged()
    }

    override fun setUser(user: User) {
        tv_user.text = "${user.name}  ${user.lastname}"
    }

    override fun updateRating(rating: Score) {

        tv_raiting.setText(rating.value.toString())

        val raitingMe = getRaitingMe()

        if (raitingMe == null) {
            ratingsList.add(rating)
        } else {
            raitingMe.comment = rating.comment
            raitingMe.value = rating.value
        }
        adapterRating.notifyDataSetChanged()
        tv_subtitle_bs.text = "${ratingsList.size} calificaciones"
    }

    override fun setRatings(ratingList: List<Score>) {
        Log.e("aa", "todo llego" + ratingList.size)
        tv_subtitle_bs.text = "${ratingList.size} calificaciones"
        this.ratingsList.clear()
        this.ratingsList.addAll(ratingList)
        adapterRating.notifyDataSetChanged()
    }

    override fun showButtonRaiting(visible: Int) {
        btn_raiting.visibility = visible
    }

    override fun dowloadQuestionnaire() {
        showMessagge("Descargando Cuestionario")
        btn_get_questionnaire.visibility = View.INVISIBLE
        progressbar_down.visibility = View.VISIBLE
        val intent = Intent(this, DonwloadIntentService::class.java)
        intent.putExtra(DonwloadIntentService.IDQUESTIONNAIRE, questionaire.idCloud)
        intent.putExtra(DonwloadIntentService.ISDOWNLOAD, is_download)
        startService(intent)
    }

    override fun confirmDownloadQuestionnaire() {
        createDeleteDialog().show()
    }


    fun createDeleteDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Mensaje de confirmación")
                .setMessage("El cuestionario ya existe en su repositorio local, desea duplicarlo?")
                .setPositiveButton("ACEPTAR"
                ) { _, which ->
                    dowloadQuestionnaire()
                }
                .setNegativeButton("CANCELAR",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, which: Int) {}
                        })
        return builder.create()
    }

    override fun setDownload(b: Boolean) {
        is_download = b
    }

    override fun setDataQuestionnaire(questionaire: Questionaire) {
        tv_title.setText(questionaire.title)
        tv_description.setText(questionaire.description)
        tv_category.text = questionaire.subject
        if (questionaire.assessment <= 0.0) {
            tv_raiting.setText("Sin")
        } else {
            tv_raiting.setText(questionaire.assessment.toString())
        }
        tv_questions_num.setText(questionaire.numberQuest.toString())

        tv_subtitle_bs.text = "${questionaire.numAssessment} calificaciones"

        presenter.onGetUser(questionaire.idUser!!)
    }
}
