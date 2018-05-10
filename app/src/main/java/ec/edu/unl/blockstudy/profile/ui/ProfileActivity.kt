package ec.edu.unl.blockstudy.profile.ui

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.entities.Academic
import ec.edu.unl.blockstudy.entities.Subject
import ec.edu.unl.blockstudy.entities.User
import ec.edu.unl.blockstudy.profile.ProfilePresenter
import ec.edu.unl.blockstudy.util.GlideApp
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ProfileActivity : AppCompatActivity(), View.OnClickListener, UpdateMyInfoFragment.OnUpdateInfoListener, FormationAcademicFragment.OnUpdateFormationAcademicListener, AddSubjectFragment.OnAddSubjectListener, ProfileView {

    private val TAG = "Profile"
    lateinit var application: MyApplication
    lateinit var progressDialog: ProgressDialog
    private var user: User? = User()
    private val MY_PERMISSIONS_REQUEST_CODE = 1
    private val REQUEST_GET_IMAGE = 100
    private var photo: String? = ""


    @Inject
    lateinit var presenter: ProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setupToolBar()
        cl_my_info.setOnClickListener(this)
        cl_time_activity.setOnClickListener(this)
        cl_preferences.setOnClickListener(this)
        fab_photo_user.setOnClickListener(this)
        setupInjection()
        presenter.getDataUser()
        presenter.getPreferences()
        presenter.getAcademic()
    }

    private fun setupInjection() {
        application = getApplication() as MyApplication
        application.getProfileComponent(this).inject(this)
    }

    private fun setupToolBar() {
        setSupportActionBar(tb_profile)
        supportActionBar?.title = getString(R.string.menu_tv_my_profile)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.cl_my_info -> {
                if (user != null) {
                    Log.e(TAG, user!!.email + "AAA")
                    val updateMyInfoFragment = UpdateMyInfoFragment.newInstance(user!!.name, user!!.lastname, user!!.email)
                    updateMyInfoFragment.show(supportFragmentManager, "Actualizar Informacion")
                }
            }
            R.id.cl_time_activity -> {
                var formationAcademicFragment: FormationAcademicFragment? = null

                if (user!!.academics.size > 0) {
                    formationAcademicFragment = FormationAcademicFragment.newInstance(user!!.academics.get(0).school, user!!.academics.get(0).title)
                } else {
                    formationAcademicFragment = FormationAcademicFragment.newInstance("", "")
                }
                formationAcademicFragment.show(supportFragmentManager, "Actualizar Academica")
            }
            R.id.cl_preferences -> {
                val addSubjectFragment = AddSubjectFragment.newInstance(user!!.preferences)
                addSubjectFragment.show(supportFragmentManager, "Agregar Preferencia")
            }
            R.id.fab_photo_user -> {
                if (checkPermissions()) {
                    getPhoto()
                }
            }
        }
    }

    /*Codigo fotos*/
    private fun getFile(): File? {
        var photoFile: File? = null
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timestamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        try {
            photoFile = File.createTempFile(imageFileName, ".jpg", storageDir)
            photo = photoFile!!.absolutePath
            Log.e(TAG, "obtener foto")
        } catch (e: IOException) {
            //showSnackbar(R.string.main_error_dispatch_camera);
            Log.e("crear archivo", e.message.toString())
        }

        return photoFile
    }

    fun getPhoto() {
        var chooserIntent: Intent? = null
        var intentList: MutableList<Intent> = ArrayList()
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra("return-data", true)

        val photoFile = getFile()
        if (photoFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
            if (cameraIntent.resolveActivity(packageManager) != null) {
                intentList = addIntentsToList(intentList, cameraIntent) as MutableList<Intent>
            }
        }

        if (pickIntent.resolveActivity(packageManager) != null) {
            intentList = addIntentsToList(intentList, pickIntent) as MutableList<Intent>
        }

        if (intentList.size > 0) {
            chooserIntent = Intent.createChooser(intentList.removeAt(intentList.size - 1),
                    getString(R.string.main_message_picture_source))
            chooserIntent!!.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toTypedArray<Parcelable>())
        }

        if (chooserIntent != null) {
            startActivityForResult(chooserIntent, REQUEST_GET_IMAGE)
        }
    }

    private fun addIntentsToList(list: MutableList<Intent>, intent: Intent): List<Intent> {
        val resInfo = getPackageManager().queryIntentActivities(intent, 0)
        for (resolveInfo in resInfo) {
            val packageName = resolveInfo.activityInfo.packageName
            val targetIntent = Intent(intent)
            targetIntent.`package` = packageName
            list.add(targetIntent)
        }

        return list
    }

    private fun addToGallery() {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val file = File(photo)
        val contentUri = Uri.fromFile(file)
        mediaScanIntent.data = contentUri
        sendBroadcast(mediaScanIntent)
    }

    private fun getRealPathFromURI(contentURI: Uri?): String? {
        var result: String? = null

        val cursor = getContentResolver().query(contentURI!!, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path
        } else {
            if (contentURI.toString().contains("mediaKey")) {
                cursor!!.close()

                try {
                    val file = File.createTempFile("tempImg", ".jpg", getCacheDir())
                    val input = getContentResolver().openInputStream(contentURI)
                    val output = FileOutputStream(file)

                    try {
                        val buffer = ByteArray(4 * 1024)
                        var read = 0

                        while ((input!!.read(buffer)) != -1) {
                            output.write(buffer, 0, read)
                        }
                        output.flush()
                        result = file.absolutePath
                    } finally {
                        output.close()
                        input!!.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else {
                cursor!!.moveToFirst()
                val dataColumn = cursor!!.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                result = cursor!!.getString(dataColumn)
                cursor!!.close()
            }
        }
        return result
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CODE)
            return false
        } else {
            return true
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GET_IMAGE) {
            if (resultCode == RESULT_OK) {
                val fromCamera = data == null || data.data == null
                if (fromCamera) {
                    addToGallery()
                } else {
                    photo = getRealPathFromURI(data!!.data)
                }
                //civ_photo_user.setImageURI(Uri.parse(photo))
                presenter.updatePhotoUser(photo!!)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CODE -> if (grantResults.size > 0) {
                when (permissions[0]) {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE ->
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        else
                            Toast.makeText(this, "Permisos necesarios para funcionamiento mostrar el mapa ", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onUpdateInfor(name: String, lastname: String, email: String) {
        presenter.updateInfo(name, lastname, email)
    }

    override fun onUpdateAcademic(school: String, title: String) {
        presenter.updateAcademic(school, title)
    }

    override fun onAddSubjects(subjects: ArrayList<Subject>) {
        Log.e(TAG, "TEMATICAS" + subjects.size)
        presenter.addSubject(subjects)
    }

    override fun showMessagge(message: Any) {
        if (message is Int) {
            Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun showProgressDialog(message: Any) {
        progressDialog = ProgressDialog(this)
        if (message is Int)
            progressDialog.setMessage(getString(message));
        else
            progressDialog.setMessage(message.toString())
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    override fun hideProgressDialog() {
        progressDialog.hide()
    }

    override fun setPreferences(preferences: ArrayList<Subject>) {
        user!!.preferences = preferences
        var textoSubjects = ""
        preferences.forEach {
            Log.e(TAG, it.name)
            textoSubjects = textoSubjects + it.name + " - "
        }
        tv_empty_preferences.text = textoSubjects
    }

    override fun setAcademic(academic: ArrayList<Academic>) {
        user!!.academics = academic
        academic.forEach {
            tv_time_activity.text = it.title + ", " + it.school
        }

    }

    override fun setPhoto() {
        GlideApp.with(this)
                .load(user!!.photo)
                .placeholder(R.drawable.ic_person_black_24dp)
                .centerCrop()
                .error(R.drawable.ic_person_black_24dp)
                .into(civ_photo_user)
    }

    override fun setInfoUser(user: User) {
        this.user!!.name = user.name
        this.user!!.lastname = user.lastname
        tv_name_user.text = user!!.name + " " + user!!.lastname
    }

    override fun setDataProfile(user: User) {
        this.user!!.name = user.name
        this.user!!.lastname = user.lastname
        this.user!!.photo = user.photo
        this.user!!.email = user.email

        tv_name_user.text = user!!.name + " " + user!!.lastname
        tv_email.text = user!!.email
        setPhoto()
    }
}
