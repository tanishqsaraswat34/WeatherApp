package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

///0d3961ccd045a0c33acc443cd29a23f1
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        fetchWeatherData("Agra")
        SearchCity()

    }

    private fun SearchCity() {
        val searchview = binding.searchView
        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

    private fun fetchWeatherData(cityName : String ){
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName, "0d3961ccd045a0c33acc443cd29a23f1", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody= response.body()
                if (response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity= responseBody.main.humidity
                    val windSpeed= responseBody.wind.speed
                    val sunrise= responseBody.sys.sunrise.toLong()
                    val sunset= responseBody.sys.sunset.toLong()
                    val sealevel= responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxtemp= responseBody.main.temp_max
                    val mintemp= responseBody.main.temp_min
//                    Log.d("TAG", "onResponse: $temperature")
                    binding.temp.text= "$temperature°C"
                    binding.weather.text= condition
                    binding.maxtem.text= "Max Temp: $maxtemp °C"
                    binding.mintem.text= "Min Temp: $mintemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windspeed.text= "$windSpeed m/s"
                    binding.condition.text="$condition "
                    binding.sunrise.text="${time(sunrise)}"
                    binding.sunset.text="${time(sunset)}"
                    binding.sea.text="$sealevel hPa"
                    binding.cityName.text="$cityName"
                    binding.day.text=dayName(System.currentTimeMillis())
                    binding.date.text=  date()

                    changeimagesaccweather(condition)


                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })



        }

    private fun changeimagesaccweather(conditions: String) {
        when (conditions){
            "Clear Sky","Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background )
                binding.lottieAnimationView.setAnimation(R.raw.sun)

            }
            "Partly Clouds", "Clouds","Overcast", "Mist", "Foggy"  -> {
                binding.root.setBackgroundResource(R.drawable.colud_background )
                binding.lottieAnimationView.setAnimation(R.raw.cloud)

            }
            "Light Rain","Drizzle","Moderate rain","Showers","Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background )
                binding.lottieAnimationView.setAnimation(R.raw.rain)

            }
            "Light Snow","Moderate Snow", "Heavy Snow", "Blizzards" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background )
                binding.lottieAnimationView.setAnimation(R.raw.snow)

            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background )
                binding.lottieAnimationView.setAnimation(R.raw.sun)

            }
        }
        binding.lottieAnimationView.playAnimation()

    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        return sdf.format((Date()))

    }
private fun time(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:MM", Locale.getDefault())
    return sdf.format((Date(timestamp*1000)))
}

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        return sdf.format((Date()))



    }

}