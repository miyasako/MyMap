package com.example.sec.mymap;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class AddressTaskLoader extends AsyncTaskLoader<Address> {

    private Geocoder mGeocoder;
    private double mlat;
    private double mlon;

    public AddressTaskLoader(Context context, double lat, double lon) {
        super(context);
        mGeocoder = new Geocoder(context, Locale.getDefault());
        mlat = lat;
        mlon = lon;
    }

    @Override
    public Address loadInBackground() {
        Address result = null;
        try{
            List<Address> results = mGeocoder.getFromLocation(mlat,mlon,1);
            if(results != null && !results.isEmpty()){
                result = results.get(0);
            }
        } catch (IOException e) {
            Log.e("AddressTaskLoader",e.getMessage());
        }

        return result;
    }
    @Override
    protected void onStartLoading(){
        forceLoad();
    }
}
