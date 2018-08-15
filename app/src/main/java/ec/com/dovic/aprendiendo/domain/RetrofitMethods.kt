package ec.com.dovic.aprendiendo.domain

import ec.com.dovic.aprendiendo.entities.Question
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetrofitMethods {
    @Headers("Content-Type: application/json")
    @POST(" ")
    fun generateRecommendations(@Body params: HashMap<String, String>): Call<String>
}