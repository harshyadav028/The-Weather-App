package com.example.theweatherapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.theweatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData("Jaipur")
        searchCity()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun searchCity() {
        val search=binding.searchView
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
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

    private fun fetchWeatherData(cityName:String) {
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .build().create(ApiInterface::class.java)

        val response= retrofit.getWeatherData(cityName, "696a8533f80491b294642faf32a20236", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody=response.body()
                if(response.isSuccessful &&responseBody!=null){
                    val temperature=responseBody.main.temp.toString()
                    val windSpeed=responseBody.wind.speed.toString()
                    val humidity=responseBody.main.humidity.toString()
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunSet=responseBody.sys.sunset.toLong()
                    val seaLevel=responseBody.main.sea_level.toString()
                    val maxTemp=responseBody.main.temp_max.toString()
                    val minTemp=responseBody.main.temp_min.toString()
                    val condition=responseBody.weather.firstOrNull()?.main?:"Unknown"
                    binding.tvtemp.text="$temperature °C"
                    binding.tvmin.text="Min : $minTemp °C"
                    binding.tvmax.text="Max : $maxTemp °C"
                    binding.windspeed.text="$windSpeed m/s"
                    binding.condition.text="$condition"
                    binding.weatherCondition.text="$condition"
                    binding.hum.text="$humidity %"
                    binding.ssunrise.text=time(sunRise)
                    binding.sunset.text=time(sunSet)
                    binding.sealevel.text="$seaLevel hPa"
                    binding.tvloc.text="$cityName"
                    binding.tvdate.text=date()
                    binding.tvday.text=dayName(System.currentTimeMillis())
                    setBackOnConditionChange(condition)
//                    Log.d("myTag",temperature)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }
        })
    }

    private fun setBackOnConditionChange(condtion:String) {
        when(condtion){
            "Haze","Partly Cloudy","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clear","Clear sky","Sunny" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun2)
            }
            "Light Rain","Drizzle","Moderate Rain","Heavy Rain","Showers" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun dayName(timestamp:Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun time(timestamp:Long):String{
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
    fun date():String{
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
}