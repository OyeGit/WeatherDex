package com.weatherdex.oye.weatherdex.UI.Detail;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.weatherdex.oye.weatherdex.AppExecutors;
import com.weatherdex.oye.weatherdex.Data.SQLLiteDB.WeatherEntry;
import com.weatherdex.oye.weatherdex.R;

import com.weatherdex.oye.weatherdex.Utilities.WeatherWeatherUtils;
import com.weatherdex.oye.weatherdex.databinding.ActivityDetailBinding;
import com.weatherdex.oye.weatherdex.Utilities.WeatherDateUtils;

import java.util.Date;


public class DetailActivity extends LifecycleActivity {

    public static final String WEATHER_ID_EXTRA = "WEATHER_ID_EXTRA";

    private ActivityDetailBinding mDetailBinding;
    private DetailActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        long timestamp = getIntent().getLongExtra(WEATHER_ID_EXTRA, -1);
        Date date = new Date(timestamp);

        mViewModel = ViewModelProviders.of(this).get(DetailActivityViewModel.class);

        mViewModel.getWeather().observe(this, weatherEntry -> {
            // If the weather forecast details change, update the UI
            if (weatherEntry != null) bindWeatherToUI(weatherEntry);
        });

        AppExecutors.getInstance().diskIO().execute(()-> {
            try {

                // Pretend this is the network loading data
                Thread.sleep(4000);
                Date today = WeatherDateUtils.getNormalizedUtcDateForToday();
                WeatherEntry pretendWeatherFromDatabase = new WeatherEntry
                        (1, 210, today,88.0,99.0,71,1030.0,
                                10.0,0.0, 74, 5, 6, 7);
                mViewModel.setWeather(pretendWeatherFromDatabase);

                Thread.sleep(2000);
                pretendWeatherFromDatabase = new WeatherEntry
                        (1, 952, today,88.0,99.0,71,1030.0,
                                10.0,0.0, 74, 5, 6, 7);
                mViewModel.setWeather(pretendWeatherFromDatabase);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }


    private void bindWeatherToUI(WeatherEntry weatherEntry) {
        /****************
         * Weather Icon *
         ****************/

        int weatherId = weatherEntry.getWeatherIconId();
        int weatherImageId = WeatherWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);

        /* Set the resource ID on the icon to display the art */
        mDetailBinding.primaryInfo.weatherIcon.setImageResource(weatherImageId);

        /****************
         * Weather Date *
         ****************/
        /*
         * The date that is stored is a GMT representation at midnight of the date when the weather
         * information was loaded for.
         *
         * When displaying this date, one must add the GMT offset (in milliseconds) to acquire
         * the date representation for the local date in local time.
         * SunshineDateUtils#getFriendlyDateString takes care of this for us.
         */
        long localDateMidnightGmt = weatherEntry.getDate().getTime();
        String dateText = WeatherDateUtils.getFriendlyDateString(DetailActivity.this, localDateMidnightGmt, true);
        mDetailBinding.primaryInfo.date.setText(dateText);

        /***********************
         * Weather Description *
         ***********************/
        /* Use the weatherId to obtain the proper description */
        String description = WeatherWeatherUtils.getStringForWeatherCondition(DetailActivity.this, weatherId);

        /* Create the accessibility (a11y) String from the weather description */
        String descriptionA11y = getString(R.string.a11y_forecast, description);

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding.primaryInfo.weatherDescription.setText(description);
        mDetailBinding.primaryInfo.weatherDescription.setContentDescription(descriptionA11y);

        /* Set the content description on the weather image (for accessibility purposes) */
        mDetailBinding.primaryInfo.weatherIcon.setContentDescription(descriptionA11y);

        /**************************
         * High (max) temperature *
         **************************/

        double maxInCelsius = weatherEntry.getTempMax();

        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        String highString = WeatherWeatherUtils.formatTemperature(DetailActivity.this, maxInCelsius);

        /* Create the accessibility (a11y) String from the weather description */
        String highA11y = getString(R.string.a11y_high_temp, highString);

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding.primaryInfo.highTemperature.setText(highString);
        mDetailBinding.primaryInfo.highTemperature.setContentDescription(highA11y);

        /*************************
         * Low (min) temperature *
         *************************/

        double minInCelsius = weatherEntry.getTempMin();
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        String lowString = WeatherWeatherUtils.formatTemperature(DetailActivity.this, minInCelsius);

        String lowA11y = getString(R.string.a11y_low_temp, lowString);

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding.primaryInfo.lowTemperature.setText(lowString);
        mDetailBinding.primaryInfo.lowTemperature.setContentDescription(lowA11y);

        /************
         * Humidity *
         ************/

        double humidity = weatherEntry.getHumidity();
        String humidityString = getString(R.string.format_humidity, humidity);
        String humidityA11y = getString(R.string.a11y_humidity, humidityString);

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding.extraDetails.humidity.setText(humidityString);
        mDetailBinding.extraDetails.humidity.setContentDescription(humidityA11y);

        mDetailBinding.extraDetails.humidityLabel.setContentDescription(humidityA11y);

        /****************************
         * Wind speed and direction *
         ****************************/
        /* Read wind speed (in MPH) and direction (in compass degrees)*/
        double windSpeed = weatherEntry.getWind();
        double windDirection = weatherEntry.getDegrees();
        String windString = WeatherWeatherUtils.getFormattedWind(DetailActivity.this, windSpeed, windDirection);
        String windA11y = getString(R.string.a11y_wind, windString);

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding.extraDetails.windMeasurement.setText(windString);
        mDetailBinding.extraDetails.windMeasurement.setContentDescription(windA11y);
        mDetailBinding.extraDetails.windLabel.setContentDescription(windA11y);

        /************
         * Pressure *
         ************/
        double pressure = weatherEntry.getPressure();

        /*
         * Format the pressure text using string resources. The reason we directly access
         * resources using getString rather than using a method from SunshineWeatherUtils as
         * we have for other data displayed in this Activity is because there is no
         * additional logic that needs to be considered in order to properly display the
         * pressure.
         */
        String pressureString = getString(R.string.format_pressure, pressure);

        String pressureA11y = getString(R.string.a11y_pressure, pressureString);

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding.extraDetails.pressure.setText(pressureString);
        mDetailBinding.extraDetails.pressure.setContentDescription(pressureA11y);
        mDetailBinding.extraDetails.pressureLabel.setContentDescription(pressureA11y);
    }

}
