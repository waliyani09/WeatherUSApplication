package com.example.weatherusapplication.presentation.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.weatherusapplication.databinding.ActivityMainBinding
import com.example.weatherusapplication.presentation.ui.viewModels.WeatherViewModel
import com.example.weatherusapplication.interfaces.LocationPermissionListener
import com.example.weatherusapplication.domain.utilities.Const
import com.example.weatherusapplication.domain.utilities.LocationPermissionHelper
import com.example.weatherusapplication.domain.utilities.SharedPreferencesManager
import com.example.weatherusapplication.domain.utilities.ToastManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LocationPermissionListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var locationPermissionHelper: LocationPermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationPermissionHelper = LocationPermissionHelper(this, this)
        locationPermissionHelper.checkAndRequestLocationPermission()

        setUpViewModel()

        viewModel.isWeatherDataAvailable.observe(this, Observer { isAvailable ->
            binding.weatherDataAvailable.visibility = if (isAvailable) View.VISIBLE else View.GONE
            binding.weatherDataNotAvailable.visibility = if (isAvailable) View.GONE else View.VISIBLE
        })

        viewModel.isShowProgress.observe(this, Observer { showProgress ->
            binding.mainProgressBar.visibility = if (showProgress) View.VISIBLE else View.GONE
        })

        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            ToastManager.showShortToast(this, errorMessage)
        })

        binding.theSearchButton.setOnClickListener {
            val searchText = binding.theSearchBar.text.toString()
            if (searchText.isEmpty()) {
                ToastManager.showShortToast(this, "Please enter a valid city name!")
            } else {
                viewModel.getWeatherFromAPI(searchText)
            }
        }

        viewModel.responseContainer.observe(this, Observer { response ->
            response?.let {
                with(binding) {
                    setLabelText(labelLatitude, "Latitude: ${it.coord?.lat}")
                    setLabelText(labelLongitude, "Longitude: ${it.coord?.lon}")
                    setLabelText(labelWeather, "Weather: ${it.weather?.getOrNull(0)?.description}")
                    setLabelText(labelWindSpeed, "Wind Speed: ${it.wind?.speed}")
                    setLabelText(labelWindDegree, "Wind Degree: ${it.wind?.deg}")
                    setLabelText(labelWindTemperature, "Temperature: ${it.main?.temp}")
                    setLabelText(labelWindPressure, "Pressure: ${it.main?.pressure}")
                    setLabelText(labelWindHumidity, "Humidity: ${it.main?.humidity}")
                    Glide.with(this@MainActivity)
                        .load(Const.ICON_BASE_URL + (it.weather?.getOrNull(0)?.icon) + ".png")
                        .placeholder(androidx.constraintlayout.widget.R.drawable.abc_ic_clear_material)
                        .into(imageWeather)
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferencesUtil = SharedPreferencesManager.getInstance(this)
        sharedPreferencesUtil.putString(Const.SHARED_PREF_CITY_KEY, binding.theSearchBar.text.toString())
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        loadLastEnteredCity()
    }

    private fun setLabelText(label: TextView, value: String) {
        label.text = value
    }

    private fun loadLastEnteredCity() {
        val sharedPreferencesUtil = SharedPreferencesManager.getInstance(this)
        val lastEnteredCity = sharedPreferencesUtil.getString(Const.SHARED_PREF_CITY_KEY)
        if (lastEnteredCity != null) {
            binding.theSearchBar.setText(lastEnteredCity)
            viewModel.getWeatherFromAPI(lastEnteredCity)
        } else {
            binding.theSearchBar.setText("")
            ToastManager.showShortToast(this, "Please enter a US city name to search for its weather")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.handlePermissionsResult(requestCode, grantResults)
    }

    override fun onPermissionDenied() {
        ToastManager.showShortToast(this, "Location Permission Denied")
        val sharedPreferencesUtil = SharedPreferencesManager.getInstance(this)
        sharedPreferencesUtil.putString(Const.SHARED_PREF_CITY_KEY, "")
        loadLastEnteredCity()
    }

    override fun onPermissionGranted() {
        ToastManager.showShortToast(this, "Location Permission is granted!")
    }
}
