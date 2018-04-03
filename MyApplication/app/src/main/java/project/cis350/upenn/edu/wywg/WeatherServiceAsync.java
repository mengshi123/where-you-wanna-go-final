package project.cis350.upenn.edu.wywg;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sanjanasarkar on 4/2/17.
 */

public class WeatherServiceAsync extends AsyncTask<Void, Void, String> {

    private final Location l;

    String key = "&APPID=b4e7c1d080829efa7a3a6a9110f3091a";

    public WeatherServiceAsync(Location l) {
        this.l = l;
    }

    protected String doInBackground(Void... urls) {
        // Do some validation here

        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + (int) l.getLat() + "&lon=" + (int) l.getLongi() + key);
            //URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=Rome,it&APPID=a6760d9422fe867000beb6ed2f60e167");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response) {

        String test = response;
        try {
// parse the json result returned from the service
            JSONObject jsonResult = null;
            try {
                jsonResult = new JSONObject(test);
            } catch (JSONException e) {
                e.printStackTrace();
            }

// parse out the temperature from the JSON result
            double temperature = 0;
            try {
                temperature = jsonResult.getJSONObject("main").getDouble("temp");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            temperature = ConvertTemperatureToFarenheit(temperature);
            l.setTemp(temperature);

            // parse out the pressure from the JSON Result
            try {
                double pressure = jsonResult.getJSONObject("main").getDouble("pressure");
            } catch (JSONException e) {
                e.printStackTrace();
            }

// parse out the humidity from the JSON result
            try {
                double humidity = jsonResult.getJSONObject("main").getDouble("humidity");
            } catch (JSONException e) {
                e.printStackTrace();
            }

// parse out the description from the JSON result

            String description = jsonResult.getJSONArray("weather").getJSONObject(0).getString("description");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private double ConvertTemperatureToFarenheit(double temperature) {
        return (temperature - 273) * (9/5) + 32;
    }
}
