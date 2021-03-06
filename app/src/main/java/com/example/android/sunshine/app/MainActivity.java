package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private final String LOG_TAG= this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new Previsaofragment())
                    .commit();
        }
        SharedPreferences opt= getSharedPreferences("",0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id==R.id.action_settings) {
            Intent settingsintent = new Intent(this, SettingsActivity.class);
            startActivity(settingsintent);
            return true;
        }
        if(id==R.id.action_map)
        {
            abremapa();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abremapa() {
        SharedPreferences opt= PreferenceManager.getDefaultSharedPreferences(this);
        String local= opt.getString(getString(R.string.pref_location_key), "Belo Horizonte,br");
        Uri geolocation= Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q",local).build();
        Intent intentmapa= new Intent(Intent.ACTION_VIEW);
        intentmapa.setData(geolocation);
        if(intentmapa.resolveActivity(getPackageManager())!= null)
        {
            startActivity(intentmapa);
        }else{
            Log.v(LOG_TAG, "Erro pra abrir mapa");
        }
    }

}