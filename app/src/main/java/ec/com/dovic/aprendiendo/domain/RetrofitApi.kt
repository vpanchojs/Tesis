package ec.com.dovic.aprendiendo.domain

import android.util.Log
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitApi {
    companion object {
        const val TAG = "RetrofitApi"
        val PATH_API = "https://aprendiendo-75d32.appspot.com/"

    }

    val retrofit = Retrofit.Builder()
            .baseUrl(PATH_API)
            .addConverterFactory(GsonConverterFactory.create(
                    GsonBuilder().serializeNulls().create())
            )
            .build()

    val request = retrofit.create(RetrofitMethods::class.java)

    fun generateRecommendations(idUser: String, idQuestionnaire: String) {
        var parametros = HashMap<String, String>()
        parametros.put("id_user", idUser)
        parametros.put("id_questionnaire", idQuestionnaire)

        request.generateRecommendations(parametros).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                Log.e(TAG, "mal ${t.toString()}")

            }

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                Log.e(TAG, "bien ${response.toString()} ")
            }
        })
    }

}