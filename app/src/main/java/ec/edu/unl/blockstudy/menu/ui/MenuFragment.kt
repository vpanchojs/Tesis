package ec.edu.unl.blockstudy.menu.ui

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
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.entities.User
import ec.edu.unl.blockstudy.login.ui.LoginActivity
import ec.edu.unl.blockstudy.menu.MenusPresenter
import ec.edu.unl.blockstudy.menu.adapter.OptionsAdapter
import ec.edu.unl.blockstudy.menu.adapter.onOptionsAdapterListener
import ec.edu.unl.blockstudy.profile.ui.ProfileActivity
import ec.edu.unl.blockstudy.util.GlideApp
import ec.edu.unl.blockstudy.util.OptionMenu
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
        data!!.add(OptionMenu(R.drawable.ic_key, getString(R.string.menu_option_update_password)))
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
        var view = inflater!!.inflate(R.layout.fragment_menu, container, false)
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
        progressDialog.hide()
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
            0 -> {
                showMessagge("Cambiar Contrasena")
                val changePasswordFragment = ChangePasswordFragment.newInstance()
                changePasswordFragment.show(childFragmentManager, "Cambiar Contrasena")
            }
            1 -> {
                showMessagge("Terminos y Condiciones")
            }
            2 -> {
                showMessagge("Ayuda")
            }
            3 -> {
                showMessagge("Cerrar session")
                presenter.onSingOut()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cl_my_profile -> {
                navigationToProfile()
            }
        }
    }

    fun onUpdatePassword(password: String, passwordOld: String) {
        presenter.onUpdatePassword(password, passwordOld)
    }

    override fun setDataProfile(user: User) {
        this.user = user
        tv_name_user.text = user.name
        tv_email.text = user.email
        GlideApp.with(context!!)
                .load(user.photo)
                .placeholder(R.drawable.ic_person_black_24dp)
                .centerCrop()
                .error(R.drawable.ic_person_black_24dp)
                .into(civ_user)


    }

    companion object {
        fun newInstance(): MenuFragment {
            val fragment = MenuFragment()
            return fragment
        }
    }
}
