package ec.edu.unl.blockstudy.questionnaireResume.ui

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.entities.Question
import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.entities.Raiting
import ec.edu.unl.blockstudy.entities.User
import ec.edu.unl.blockstudy.questionnaireResume.QuestionnaireResumePresenter
import ec.edu.unl.blockstudy.questionnaireResume.adapter.QuestionAdapter
import ec.edu.unl.blockstudy.questionnaireResume.adapter.RatingsAdapter
import ec.edu.unl.blockstudy.questionnaireResume.servicie.DonwloadIntentService
import ec.edu.unl.blockstudy.util.BaseActivitys
import kotlinx.android.synthetic.main.activity_questionnaire_resume.*
import kotlinx.android.synthetic.main.bottom_sheet_raiting.*
import kotlinx.android.synthetic.main.content_questionnaire_resume.*
import javax.inject.Inject

class QuestionnaireResumeActivity : AppCompatActivity(), QuestionnaireResumeView, View.OnClickListener, RatingFragment.OnRatingListener {

    override fun onSetRaiting(value: Double, comment: String) {
        Log.e("RA", "VALUE" + value)
        presenter.setRaiting(questionaire.idCloud, value, comment)
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
                //presenter.getQuestionnaire(questionaire.idCloud)
                Log.e("aa", "sasdasd")
                val intent = Intent(this, DonwloadIntentService::class.java)
                intent.putExtra(DonwloadIntentService.IDQUESTIONNAIRE, questionaire.idCloud)
                startService(intent)
            }
        }
    }

    fun getRaitingMe(): Raiting? {
        return ratingsList.findLast {
            it.me == true
        }
    }

    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    var questionList = ArrayList<Question>()
    var ratingsList = ArrayList<Raiting>()

    lateinit var application: MyApplication
    lateinit var adapter: QuestionAdapter
    lateinit var adapterRating: RatingsAdapter

    @Inject
    lateinit var presenter: QuestionnaireResumePresenter


    lateinit var questionaire: Questionaire

    companion object {
        val PARAM_QUESTIONNAIRE = "questionnaire"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionnaire_resume)
        questionaire = intent.getParcelableExtra(PARAM_QUESTIONNAIRE)
        setDataQuestionnaire()
        toolbar.setTitle("Cuestionario")
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setupInjection()
        setupEvents()
        setupBottomSheet()
        setupRecyclerView()
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
        adapter = QuestionAdapter(questionList!!)
        adapterRating = RatingsAdapter(ratingsList!!)


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
    }

    override fun onResume() {
        super.onResume()
        presenter.onSuscribe()
        presenter.onGetQuestionAll(questionaire.idCloud)
        presenter.onGetUser(questionaire.idUser!!)
        presenter.onGetRaitingsAll(questionaire.idCloud)
    }

    override fun showMessagge(message: Any) {
        BaseActivitys.showToastMessage(this, message, Toast.LENGTH_SHORT)
    }

    override fun showProgress(show: Boolean) {

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
        this.questionList!!.clear()
        this.questionList!!.addAll(questionList)
        adapter.notifyDataSetChanged()
    }

    override fun setUser(user: User) {
        tv_user.setText(user.name + " " + user.lastname)
    }

    override fun updateRating(rating: Double) {
        tv_raiting.setText(rating.toString())
    }

    override fun setRatings(ratingList: List<Raiting>) {
        Log.e("aa", "todo llego" + ratingList.size)
        tv_subtitle_bs.setText(ratingList.size.toString() + " calificaciones")
        this.ratingsList!!.clear()
        this.ratingsList!!.addAll(ratingList)
        adapterRating.notifyDataSetChanged()
    }
}
