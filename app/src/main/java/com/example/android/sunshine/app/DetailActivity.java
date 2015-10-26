/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.sunshine.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

public class DetailActivity extends ActionBarActivity {

    //private ShareActionProvider mSAP;
    private final String LOG_TAG= this.getClass().getSimpleName();
    private final String HASHTAG="#SunshineApp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem item =menu.findItem(R.id.menu_item_share);

        mSAP= (ShareActionProvider) item.getActionProvider();*/
        return true;
    }


    /*private void setShareIntent(Intent shareIntent)
    {
        if (mSAP != null)
        {
            mSAP.setShareIntent(shareIntent);
        }
    }*/

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

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
            setHasOptionsMenu(true);
        }

        String message;
        private final String LOG_TAG= this.getClass().getSimpleName();
        private final String HASHTAG="#SunshineApp";
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //setHasOptionsMenu(true);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            TextView tv= (TextView)rootView.findViewById(R.id.textViewdetail);
            Intent intent = getActivity().getIntent();
            message = intent.getStringExtra("StringDetail");

            tv.setText(message);
            return rootView;
        }
        private Intent createShareIntent()
        {
            Intent shareIntent=new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message + HASHTAG);
            return shareIntent;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

            inflater.inflate(R.menu.detail, menu);

            MenuItem item =menu.findItem(R.id.menu_item_share);

            ShareActionProvider mSAP = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            if (mSAP!=null)
            {
                mSAP.setShareIntent(createShareIntent());
            }else{
                Log.v(LOG_TAG,"Share Provider null?");
            }
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id= item.getItemId();
            if (id==R.id.action_settings){
                Intent settingsintent=new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsintent);
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
