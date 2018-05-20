package ec.edu.unl.blockstudy.blockResume.ui

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.block.servicie.ServicieBlock
import ec.edu.unl.blockstudy.blockResume.BlockResumePresenter
import ec.edu.unl.blockstudy.blockResume.adapter.QuestionnaireSelectAdapter
import ec.edu.unl.blockstudy.blockResume.adapter.onQuestionnaireAdapterListener
import ec.edu.unl.blockstudy.entities.Block
import ec.edu.unl.blockstudy.entities.QuestionnaireBlock
import ec.edu.unl.blockstudy.entities.objectBox.QuestionnaireBd
import ec.edu.unl.blockstudy.util.BaseActivitys
import kotlinx.android.synthetic.main.fragment_block_resume.*
import javax.inject.Inject


class BlockResumeFragment : Fragment(), View.OnClickListener, BlockResumeView, onQuestionnaireAdapterListener<QuestionnaireBd>, CompoundButton.OnCheckedChangeListener {


    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        //requestPermission()
        if (p1)
            activity!!.startService(Intent(context, ServicieBlock::class.java).putExtra(ServicieBlock.IDBLOCK, block.id))
        else
            activity!!.stopService(Intent(context, ServicieBlock::class.java))

    }

    fun getStateServicie() {
        tobtn_block.isChecked = ServicieBlock.INSTANCE
    }

    val MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 100


    override fun addQuestionnaire(questionaire: QuestionnaireBd) {
        // presenter.addQuestionnaire(questionaire.idQuestionaire, questionaire.idCloud, block.id, questionaire.refQuestions)
    }

    override fun removeQuestionnaire(questionaire: QuestionnaireBd) {
        //presenter.removeQuestionnaire(questionaire.idQuestionaire)
    }

    @Inject
    lateinit var presenter: BlockResumePresenter

    lateinit var application: MyApplication

    lateinit var applications: ArrayList<String>

    lateinit var block: Block

    lateinit var adapter: QuestionnaireSelectAdapter

    var data = ArrayList<QuestionnaireBd>()

    var questonnaireBlock = ArrayList<QuestionnaireBlock>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_block_resume, container, false)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applications = ArrayList()
        setupInjection()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cl_time_activity.setOnClickListener(this)
        cl_apps.setOnClickListener(this)
        tobtn_block.setOnClickListener(this)
        tobtn_block.setOnCheckedChangeListener(this)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = QuestionnaireSelectAdapter(data, questonnaireBlock, this)
        rv_questionnaire.layoutManager = LinearLayoutManager(context)
        rv_questionnaire.adapter = adapter
    }


    fun onSetTime(time: Int) {
        block.timeActivity = time
        presenter.setTimeActivity(block)
    }

    private fun setupInjection() {
        application = activity!!.getApplication() as MyApplication
        application.getBlockResumeComponent(this).inject(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.onSuscribe()
        presenter.getDataBlock()
        presenter.getQuestionnaires()
        getStateServicie()

    }

    override fun onStop() {
        super.onStop()
        presenter.onUnSuscribe()
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.cl_apps -> {
                Log.e("a", "apps")
                //Enviar la lista de las aplicaciones
                //startActivity(Intent(context, ApplicationsActivity::class.java))

                val applicationsFragment = ApplicationsFragment.newInstance(applications)
                applicationsFragment.show(childFragmentManager, "Apps")
            }

            R.id.cl_time_activity -> {
                Log.e("a", "click time")
                if (::block.isInitialized) {
                    val selectTimeActivityFragment = SelectTimeActivityFragment.newInstance(block.timeActivity)
                    selectTimeActivityFragment.show(childFragmentManager, "Time")
                }
            }
        }
    }


    private fun requestPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivityForResult(intent, MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS)
    }

    private fun hasPermission(): Boolean {
        val appOps = activity!!.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        var mode = 0
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), activity!!.getPackageName())
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }


    companion object {
        fun newInstance(): BlockResumeFragment {
            val fragment = BlockResumeFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun showMessagge(message: Any) {
        BaseActivitys.showToastMessage(context!!, message, Toast.LENGTH_SHORT)
    }

    override fun setTimeActivity(time: Int) {
        Log.e("time", time.toString())
        if (time > 0) {
            tv_time_activity.setText(time.toString() + " minutos")
        } else {
            tv_time_activity.setText("Ninguno")
        }
    }

    fun setApplications() {
        presenter.setApplications(applications, block.id)
    }

    override fun setApplicationsSelect(size: Int) {
        Log.e("applicaciones", size.toString())
        tv_num_applications.setText(size.toString() + " Seleccionadas")
    }

    override fun showProgress(show: Boolean) {

    }

    override fun setQuestionnaries(questionnaire_list: List<QuestionnaireBd>) {
        data!!.clear()
        adapter.data.addAll(questionnaire_list)
        adapter.notifyDataSetChanged()
    }

    override fun none_results(show: Boolean) {

    }

    override fun setBlockData(block: Block) {
        this.block = block
        setTimeActivity(block.timeActivity)
        setApplicationsSelect(block.apps.size)
        applications.clear()
        block.apps.forEach {
            applications.add(it.app)
        }

        questonnaireBlock.addAll(block.questionaire)

        adapter.notifyDataSetChanged()

        block.questionaire.forEach {
            //applications.add(it.)
            it.questionsPath.forEach {
                Log.e("path", "${it.path}")
            }
        }
    }

}
