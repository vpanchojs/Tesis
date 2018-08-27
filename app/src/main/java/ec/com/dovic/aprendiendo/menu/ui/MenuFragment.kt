package ec.com.dovic.aprendiendo.menu.ui

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ec.com.dovic.aprendiendo.MyApplication
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.block.servicie.ServicieBlock
import ec.com.dovic.aprendiendo.entities.User
import ec.com.dovic.aprendiendo.login.ui.LoginActivity
import ec.com.dovic.aprendiendo.menu.MenusPresenter
import ec.com.dovic.aprendiendo.menu.adapter.OptionsAdapter
import ec.com.dovic.aprendiendo.menu.adapter.onOptionsAdapterListener
import ec.com.dovic.aprendiendo.profile.ui.ProfileActivity
import ec.com.dovic.aprendiendo.util.GlideApp
import ec.com.dovic.aprendiendo.util.OptionMenu
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.fragment_menu.view.*
import javax.inject.Inject

class MenuFragment : Fragment(), MenusView, onOptionsAdapterListener, View.OnClickListener {
    private val TAG = "MenuFragment"
    private var adapterOptions: OptionsAdapter? = null
    private var data: ArrayList<OptionMenu>? = ArrayList()
    lateinit var progressDialog: ProgressDialog
    private var user: User? = null

    @Inject
    lateinit var presenter: MenusPresenter
    lateinit var myApplication: MyApplication


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  data!!.add(OptionMenu(R.drawable.ic_key, getString(R.string.menu_option_update_password)))
        data!!.add(OptionMenu(R.drawable.ic_termins_conditions, getString(R.string.menu_option_termins_and_conditions)))
        data!!.add(OptionMenu(R.drawable.ic_help, getString(R.string.menu_option_help)))
        data!!.add(OptionMenu(R.drawable.ic_exit, getString(R.string.menu_option_signout)))
        adapterOptions = OptionsAdapter(data!!, this)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        presenter.getMyProfile()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        view.rv_menu_options.layoutManager = LinearLayoutManager(context)
        view.rv_menu_options.adapter = adapterOptions
        view.cl_my_profile.setOnClickListener(this)
        setupInjection()

        return view
    }


    private fun setupInjection() {
        myApplication = activity!!.getApplication() as MyApplication
        myApplication.getMenusComponent(this).inject(this)
    }


    /*
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }
*/

    override fun showProgressDialog(message: Int) {
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage(getString(message));
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    override fun hideProgressDialog() {
        progressDialog.dismiss()
    }

    override fun showMessagge(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    override fun navigationToProfile() {
        startActivity(Intent(context, ProfileActivity::class.java))
    }

    override fun navigationToTermsAndConditions() {

    }

    override fun navigationToLogin() {
        startActivity(Intent(context, LoginActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    override fun onClick(position: Int) {
        when (position) {
            /* 0 -> {
                 val changePasswordFragment = ChangePasswordFragment.newInstance()
                 changePasswordFragment.show(childFragmentManager, "Cambiar Contrasena")
             }
             */
            0 -> {
                presenter.crearCuestionario()
                showMessagge("Términos y Condiciones")
            }
            1 -> {
                showMessagge("Ayuda")
            }
            2 -> {
                presenter.onSingOut()
                activity!!.stopService(Intent(context, ServicieBlock::class.java))
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cl_my_profile -> {
                // navigationToProfile()
            }
        }
    }

    fun onUpdatePassword(password: String, passwordOld: String) {
        presenter.onUpdatePassword(password, passwordOld)
    }

    override fun setDataProfile(user: User) {
        this.user = user
        tv_name_user.text = user.name
        GlideApp.with(context!!)
                .load(user.photo)
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .centerCrop()
                .error(R.drawable.ic_account_circle_black_24dp)
                .into(civ_user)


    }

    companion object {
        fun newInstance(): MenuFragment {
            val fragment = MenuFragment()
            return fragment
        }
    }
}
