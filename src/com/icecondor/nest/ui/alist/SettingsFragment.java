package com.icecondor.nest.ui.alist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;
import android.util.Log;

import com.icecondor.nest.Constants;
import com.icecondor.nest.Prefs;
import com.icecondor.nest.R;

public class SettingsFragment extends PreferenceFragment
                              implements OnSharedPreferenceChangeListener,
                                         OnPreferenceClickListener {

    private SharedPreferences sharedPrefs;
    private final String[] keys = {Constants.PREFERENCE_API_URL,
                                   Constants.PREFERENCE_RECORDING_FREQUENCY_SECONDS,
                                   Constants.PREFERENCE_AUTOSTART,
                                   Constants.PREFERENCE_SOURCE_GPS,
                                   Constants.PREFERENCE_SOURCE_CELL,
                                   Constants.PREFERENCE_SOURCE_WIFI};

    public SettingsFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.APP_TAG, "SettingsFragment onCreate");

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        Preference logout = findPreference("logout_pref_notused");
        logout.setOnPreferenceClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSummaries();
    }

    protected void refreshSummaries() {
        for(String key: keys) {
            refreshSummary(key);
        }
    }

    public void refreshSummary(String key) {
        Preference preference = getPreferenceScreen().findPreference(key);
        String summary = "";
        try {
            summary = sharedPrefs.getString(key, "<none>");
            if(key.equals(Constants.PREFERENCE_RECORDING_FREQUENCY_SECONDS)) {
                int seconds = Integer.parseInt(summary);
                int minutes = seconds/60;
                if(seconds < 60) {
                    summary = "every "+seconds+" seconds";
                } else if (seconds == 60) {
                    summary = "every minute";
                } else if (seconds > 60) {
                    summary = "every "+minutes+" minutes";
                }
            }
        } catch (java.lang.ClassCastException e) {
            boolean onOff = sharedPrefs.getBoolean(key, false);
            summary = onOff ? "On" : "Off";
        }
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(key.equals(Constants.PREFERENCE_SOURCE_GPS)) {
            if(sharedPrefs.getBoolean(key, false) && !lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                summary += " - Warning: System GPS is OFF";
            }
        }
        if(key.equals(Constants.PREFERENCE_SOURCE_CELL) || key.equals(Constants.PREFERENCE_SOURCE_WIFI)) {
            if(sharedPrefs.getBoolean(key, false)  && !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                summary += " - Warning: NETWORK set to OFF";
            }
        }
        preference.setSummary(summary);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        Preference preference = getPreferenceScreen().findPreference(key);
        if(preference != null) {
            if(key.equals(Constants.PREFERENCE_AUTOSTART)){
                boolean onOff = sharedPreferences.getBoolean(Constants.PREFERENCE_AUTOSTART, false);
                if(onOff){

                } else {

                }

            }
            refreshSummary(key);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Log.d(Constants.APP_TAG, "onPreferenceClick "+preference.getKey());
        if(preference.getKey().equals("logout_pref_notused")) {
            Prefs prefs = new Prefs(this.getActivity());
            Log.d(Constants.APP_TAG, "logging out "+prefs.getAuthenticatedUsername());
            ((Main)getActivity()).doLogout();
            Intent intent = new Intent(this.getActivity(), Main.class);
            startActivity(intent);
        }
        return false;
    }
}