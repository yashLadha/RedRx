package tech.yashladha.redrx.Adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_view.view.*
import tech.yashladha.redrx.FullScreen
import tech.yashladha.redrx.Models.Country
import tech.yashladha.redrx.R

class ImageAdapter(val countries: List<Country>, val context: Context): RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.image_view, parent, false)
    )

    override fun getItemCount(): Int = countries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(countries[position])

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(country: Country) = with(itemView) {
            val iv = itemView.iv_flag
            Picasso.get()
                    .load(country.flagUrl)
                    .into(iv)

            iv.setOnClickListener({
                Toast.makeText(context, "Clicked " + country.name, Toast.LENGTH_SHORT).show()
                val intent = Intent(context, FullScreen::class.java)
                intent.putExtra("Country", country)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(intent)
            })

            itemView.tv_country_name.text = country.name
        }
    }

}