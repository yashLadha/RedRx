package tech.yashladha.redrx

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_full_screen.*
import tech.yashladha.redrx.Models.Country

class FullScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)

        val country = intent.extras["Country"] as Country
        Picasso.get()
                .load(country.flagUrl)
                .into(iv_country_flag)
    }
}
