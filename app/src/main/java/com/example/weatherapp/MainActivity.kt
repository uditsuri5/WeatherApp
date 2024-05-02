package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity

import android.widget.SearchView
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
//import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import okhttp3.Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Tag
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private  val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Jalandhar")
        SearchCity()
    }
    private fun SearchCity(){
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }


    private fun fetchWeatherData(cityName:String){
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response=retrofit.getWeatherData(cityName,"1c0fcf6a6455fc619ffab2f6e6b3969f","metric")
        response.enqueue(object :Callback<WeatherApp> {
            override fun onResponse(
                call: Call<WeatherApp>,
                response: retrofit2.Response<WeatherApp>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity=responseBody.main.humidity
                    val windspeed=responseBody.wind.speed
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunSet=responseBody.sys.sunset.toLong()
                    val sealevel=responseBody.main.pressure
                    val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp= responseBody.main.temp_max
                    val minTemp=responseBody.main.temp_min
                    binding.temp.text= "$temperature °C"
                    binding.weather.text=condition
                    binding.maxTemp.text="Max Temp:$maxTemp °C"
                    binding.minTemp.text="Min Temp:$minTemp °C"
                    binding.Humidity.text="$humidity %"
                    binding.windspeed.text="$windspeed m/s"
                    binding.sunrise.text="${time(sunRise)}"
                    binding.sunset.text="${time(sunSet)}"
                    binding.sea.text="$sealevel hPa"
                    binding.condition.text=condition
                    binding.date.text=date()
                        binding.day.text=dayName(System.currentTimeMillis())
                        binding.cityname.text="$cityName"
                   // Log.d("TAG", "onResponse: $temperature")
               changeImagesAccordingToWeatherCondition(condition)

                }



            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }
        })

    }

    private fun changeImagesAccordingToWeatherCondition(conditions:String?) {
        when(conditions){
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy","Haze"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Heavy Snow","Moderate Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

        }
        binding.lottieAnimationView.playAnimation()

    }

    private  fun date():String{
        val sdk=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdk.format((Date()))
    } private  fun time(timestamp:Long):String{
        val sdk=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdk.format((Date(timestamp*1000)))
    }
    fun dayName(timestamp:Long):String{
        val sdk=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdk.format((Date()))
    }
}








