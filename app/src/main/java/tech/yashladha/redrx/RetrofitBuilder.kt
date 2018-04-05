package tech.yashladha.redrx

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder(val baseUrl: String) {

    private lateinit var retrofit: Retrofit

    fun getBuilder(): Retrofit {
        retrofit = Retrofit.Builder()
                .baseUrl(this.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit
    }

}