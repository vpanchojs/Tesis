package ec.edu.unl.blockstudy.domain

import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import ec.edu.unl.blockstudy.MyApplication

class VolleyApi(var app: MyApplication) {
    val TAG = "VolleyApi"
    var request: RequestQueue = Volley.newRequestQueue(app.applicationContext)

    val PATH_API = "http://54.211.137.24:8050/"
    val PATH_LOGIN = "api-dsafio-auth-token/login?format=json"
    val PATH_SIGNUP = "usuario/nuevo/"

    fun onSingIn(email: String, password: String) {
        var parametros = HashMap<String, String>()
        parametros.put("email", email)
        parametros.put("password", password)
        Log.e(TAG, parametros.toString())

        /*
        val ObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                PATH_API + PATH_LOGIN,
                JSONObject(parametros),
                object : Response.Listener<JSONObject> {
                    override fun onResponse(response: JSONObject) {
                        callback.onSucces(response)
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        callback.onError(error)

                    }
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Content-Type", "application/json")
                return headers
            }

        }

        request.add(ObjectRequest)
        */

    }

}