package ro.go.stecker.hideandseek.network

import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import ro.go.stecker.hideandseek.data.SentCard

private const val BASE_URL = "https://stecker.ddns.net:3000/cards/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
    .baseUrl(BASE_URL)
    .build()

interface CardApiService {
    @POST("new-card")
    suspend fun newCard(@Body sentCard: SentCard): Response<SentCard>
}

object CardApi {
    val retrofitService: CardApiService by lazy {
        retrofit.create(CardApiService::class.java)
    }
}