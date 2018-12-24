package com.weatherdex.oye.weatherdex.UI.Detail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.weatherdex.oye.weatherdex.Data.SQLLiteDB.WeatherEntry;

public class DetailActivityViewModel  extends ViewModel {

    //    // Weather forecast the user is looking at
    private MutableLiveData<WeatherEntry> mWeather;
    //
    public DetailActivityViewModel() {
        mWeather = new MutableLiveData<>();
    }
    //
    public MutableLiveData<WeatherEntry> getWeather() {
        return  mWeather;
    }
    //
    public void setWeather(WeatherEntry weatherEntry) {
        mWeather.postValue(weatherEntry);
    }
}
