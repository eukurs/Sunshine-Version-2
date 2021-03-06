package com.example.android.sunshine.app;

/**
 * Created by caio on 20/10/2015.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * A placeholder fragment containing a simple view.
 */
public class Previsaofragment extends Fragment
{
    String SHARED_PREF_NAME="SunshinePref";
    String[] data={};
    List<String> weekForecast;
    ArrayAdapter<String> adapter;
    public Previsaofragment() {
    }

    private final String LOG_TAG= this.getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //data
        weekForecast = new ArrayList<String>(Arrays.asList(data));

        adapter= new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview,weekForecast);
        ListView lv = (ListView) rootView.findViewById(R.id.listview_forecast);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), adapter.getItem(position), Toast.LENGTH_SHORT).show();
                Intent detailtIntent = new Intent(getActivity(), DetailActivity.class);
                detailtIntent.putExtra("StringDetail", adapter.getItem(position));
                startActivity(detailtIntent);
            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        atualizaTela();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.previsaofragment, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();

        if(id==R.id.action_refresh)
        {
            //funciona
            atualizaTela();
            return true;
        }else if (id==R.id.action_settings){
            Intent settingsintent=new Intent(getActivity(), SettingsActivity.class);
            startActivity(settingsintent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void atualizaTela() {
        SharedPreferences opt= PreferenceManager.getDefaultSharedPreferences(getActivity());
        //Log.v(LOG_TAG, );
        //SharedPreferences opt= PreferenceManager.getDefaultSharedPreferences
        String temptype= opt.getString(getString(R.string.pref_temptype_key),"Celsius");

        PegaPrevisaoTask previsaoTask1= new PegaPrevisaoTask();
        //previsaoTask1.execute("http://api.openweathermap.org/data/2.5/forecast/daily?id=2270968&mode=json&units=metric&cnt=7&APPID=e71891c74c107a46e8fabab52d68fd71");
        previsaoTask1.execute(opt.getString(getString(R.string.pref_location_key), "Contagem,br"),"e71891c74c107a46e8fabab52d68fd71");
    }


    public class PegaPrevisaoTask extends AsyncTask<String, Void, String[]>
    {
        private final String LOG_TAG= this.getClass().getSimpleName();
        @Override
        protected String[] doInBackground(String... params)
        {

            if(params.length == 0)
            {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String[] result=new String[]{};

            String format="json";
            String units="metric";
            int numDays=7;

            try
            {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //URL url = new URL(params[0]);
                String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                String QUERY_PARAM = "q";
                String FORMAT_PARAM = "mode";
                String UNITS_PARAM = "units";
                String DAYS_PARAM = "cnt";
                String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, params[1])
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                result=getWeatherDataFromJson(forecastJsonStr, numDays);

                //Log.v(LOG_TAG,result.toString());

            } catch (IOException e)
            {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                //e.printStackTrace();
                Log.e(LOG_TAG, "Error ", e);
                //Deu merda no parse getWeatherDataFromJson()
            } finally
            {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null)
                {
                    try
                    {
                        reader.close();
                    } catch (final IOException e)
                    {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            //weekForecast=new ArrayList<String>(Arrays.asList(strings));
            //adapter=new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview,weekForecast);
            if (strings != null)
            {
                adapter.clear();
                //adapter.addAll(strings);
                for (String s : strings)
                {
                    adapter.add(s);
                }
            }

            super.onPostExecute(strings);

        }


    }

 /** The date/time conversion code is going to be moved outside the asynctask later,
 * so for convenience we're breaking it out into its own method now.
 */
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        //SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE dd MMM");
        String temp=shortenedDateFormat.format(time);
        temp=temp.substring(0,1).toUpperCase() + temp.substring(1);
        return temp;
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        SharedPreferences opt= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String temptype= opt.getString(getString(R.string.pref_temptype_key), "Celsius");

        if (temptype.charAt(0)=='C')
        {

        }else if (temptype.charAt(0)=='F')
        {
            high=(high*1.8)+32;
            low=(low*1.8)+32;

        }
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);
        //return "Erro no calculo de temp";
        //String highLowStr = roundedHigh + "/" + roundedLow;
        //return highLowStr;
        return roundedHigh + "/" + roundedLow;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        String[] resultStrs = new String[numDays];
        for(int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime;
            // Cheating to convert this to UTC time, which is what we want anyhow
            dateTime = dayTime.setJulianDay(julianStartDay+i);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        for (String s : resultStrs) {
            //Log.v(this.getClass().getSimpleName(), "Forecast entry: " + s);
        }
        return resultStrs;

    }
}