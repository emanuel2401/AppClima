package com.example.appclima

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appclima.api.Constant
import com.example.appclima.api.NetworkResponse
import com.example.appclima.api.RetrofitInstance
import com.example.appclima.model.WeatherModel
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult : LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    fun getData(city : String){
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try{
                val response = weatherApi.getWeather(Constant.apiKey,city)
                if(response.isSuccessful){
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                }else{
                    _weatherResult.value = NetworkResponse.Error("Failed to load data")
                }
            }
            catch (e : Exception){
                _weatherResult.value = NetworkResponse.Error("Failed to load data")
            }

        }
    }

    fun getDataForMultipleCities(cities: List<String>) {
        viewModelScope.launch {
            cities.forEach { city ->
                try {
                    val response = weatherApi.getWeather(Constant.apiKey, city)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _weatherResult.value = NetworkResponse.Success(it)
                        }
                    } else {
                        _weatherResult.value = NetworkResponse.Error("Failed to load data for city: $city")
                    }
                } catch (e: Exception) {
                    _weatherResult.value = NetworkResponse.Error("Failed to load data for city: $city")
                }
            }
        }
    }
}