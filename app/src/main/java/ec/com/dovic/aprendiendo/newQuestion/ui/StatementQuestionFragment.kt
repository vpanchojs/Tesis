package ec.com.dovic.aprendiendo.newQuestion.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.util.GlideApp
import kotlinx.android.synthetic.main.fragment_statement_question.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class StatementQuestionFragment : Fragment(), View.OnClickListener, TextWatcher {

    private var photo: String = ""
    private val MY_PERMISSIONS_REQUEST_CODE = 1
    private val REQUEST_GET_IMAGE = 100
    private val REQ_CODE_SPEECH_INPUT = 0

    companion object {
        private val ARG_PHOTO = "photo"
        private val ARG_STATEMENT = "statament"

        fun newInstance(statement: HashMap<String, String>): StatementQuestionFragment {
            val fragment = StatementQuestionFragment()
            val args = Bundle()
            args.putSerializable(ARG_STATEMENT, statement)
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var mStatement: HashMap<String, String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mStatement = arguments!!.getSerializable(ARG_STATEMENT) as HashMap<String, String>
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statement_question, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        setupEvent()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.fab_add_photo -> {
                if (checkPermissions()) {
                    getPhoto()
                }
            }
            R.id.fab_mic_statament -> {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Dicte el enunciado de su pregunta")

                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)

            }
        }
    }


    private fun setData() {
        tie_statement.setText(mStatement.get("statament"))
        if (mStatement.get("photo").isNullOrBlank()) {


        } else {
            tv_none_photo.visibility = View.GONE
            GlideApp.with(this)
                    .load(mStatement.get("photo"))
                    .centerCrop()
                    .into(iv_photo_question)
        }
    }

    private fun setupEvent() {
        tie_statement.addTextChangedListener(this)
        fab_add_photo.setOnClickListener(this)
        tie_statement.setOnFocusChangeListener { view, b ->
            if (b) {
                fab_mic_statament.visibility = View.VISIBLE
            } else {
                fab_mic_statament.visibility = View.GONE
            }
        }

        fab_mic_statament.setOnClickListener(this)


    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        mStatement.put("statament", p0.toString())
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
        } catch (e: IOException) {

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
            if (cameraIntent.resolveActivity(context!!.packageManager) != null) {
                intentList = addIntentsToList(intentList, cameraIntent) as MutableList<Intent>
            }
        }

        if (pickIntent.resolveActivity(context!!.packageManager) != null) {
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
        val resInfo = context!!.getPackageManager().queryIntentActivities(intent, 0)
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
        context!!.sendBroadcast(mediaScanIntent)
    }

    private fun getRealPathFromURI(contentURI: Uri?): String? {
        var result: String? = null

        val cursor = context!!.getContentResolver().query(contentURI!!, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path
        } else {
            if (contentURI.toString().contains("mediaKey")) {
                cursor!!.close()

                try {
                    val file = File.createTempFile("tempImg", ".jpg", context!!.getCacheDir())
                    val input = context!!.getContentResolver().openInputStream(contentURI)
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
                cursor.moveToFirst()
                val dataColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                result = cursor.getString(dataColumn)
                cursor.close()
            }
        }
        return result
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(context!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CODE)
            return false
        } else {
            return true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_GET_IMAGE -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    val fromCamera = data == null || data.data == null
                    if (fromCamera) {
                        addToGallery()
                    } else {
                        photo = getRealPathFromURI(data!!.data).toString()
                    }

                    mStatement.put("photo", photo)

                    tv_none_photo.visibility = View.GONE
                    GlideApp.with(context!!)
                            .load(photo)
                            .centerCrop()
                            .into(iv_photo_question)

                }
            }
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    tie_statement.setText(result.get(0).toString())
                }
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
                            Toast.makeText(context, "Permisos necesarios para funcionamiento mostrar el mapa ", Toast.LENGTH_LONG).show()
                }


            }
        }
    }
}
