package com.vkc.myweatherapp

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.search.SearchView
import com.vkc.myweatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Tag
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 99ba381edba2c31f1f016274de585836
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fatchWeatherData("noida")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object:android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null ) {
                    fatchWeatherData(query)
                }
                return true
            }


            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fatchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response =
            retrofit.getWeatherData(cityName, "99ba381edba2c31f1f016274de585836", "metric")
        response.enqueue(object : Callback<myData> {
            override fun onResponse(p0: Call<myData>, response: Response<myData>) {
                val responseBody = response.body()
                if (response.isSuccessful) {
                    val temperature = responseBody?.main?.temp.toString()
                    val humidity = responseBody?.main?.humidity
                    val windSpeed = responseBody?.wind?.speed
                    val sunRise = responseBody?.sys?.sunrise?.toLong()
                    val sunSet = responseBody?.sys?.sunset?.toLong()
                    val sea = responseBody?.main?.sea_level
                    val condition = responseBody?.weather?.firstOrNull()?.main ?: "unknown"
                    val maxtemp = responseBody?.main?.temp_max
                    val mintemp = responseBody?.main?.temp_min
                    val feel = responseBody?.main?.feels_like
                    // Log.d("TAG", "onResponse: $temperature")
                    binding.temp.text = "$temperature °C"
                    binding.weather.text = "$condition"
                    binding.humidity.text = "$humidity %"
                    binding.wind.text = "$windSpeed m/s"
                    binding.sunrise.text = "${sunRise?.let { time(it) }}"
                    binding.sunset.text = "${sunSet?.let { time(it) }}"
                    binding.sea.text = "$sea "
                    binding.condition.text = "$condition"
                    binding.mimTemp.text = "MinTemp:$mintemp °C"
                    binding.maxTemp.text = "MaxTemp:$maxtemp °C"
                    binding.cityName.text = "$cityName"
                    binding.feels.text ="Feels like $feel °"
                    binding.day.text =dayName(System.currentTimeMillis())
                    binding.date.text =date()

                    changeImgAccordingToWeather(condition)
                }
            }

            override fun onFailure(p0: Call<myData>, p1: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changeImgAccordingToWeather(conditins:String) {
      when(conditins){
          "Clear Sky","Sunny","Clear"->{
              binding.root.setBackgroundResource(R.drawable.sunny)
              binding.lottieAnimationView.setAnimation(R.raw.sun)
          }
          "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
              binding.root.setBackgroundResource(R.drawable.cloud)
              binding.lottieAnimationView.setAnimation(R.raw.cloud)
          }
          "Light Rain","Drizzle","Moderate rain","Showers","Heavy Rain" ->{
              binding.root.setBackgroundResource(R.drawable.rain_background)
              binding.lottieAnimationView.setAnimation(R.raw.rain)
          }
          "Light Snow","Moderate Snow","heavy Snow","Blizzard" ->{
              binding.root.setBackgroundResource(R.drawable.snow)
              binding.lottieAnimationView.setAnimation(R.raw.snow)
          }
          else ->{
              binding.root.setBackgroundResource(R.drawable.sunny)
              binding.lottieAnimationView.setAnimation(R.raw.sun)
          }

      }
    binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timeStamp:Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timeStamp*1000)))
    }

    fun dayName(timeStamp :Long): String{
           val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
            return sdf.format((Date()))
        }

}