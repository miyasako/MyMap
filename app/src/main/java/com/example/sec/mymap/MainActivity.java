package com.example.sec.mymap;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends Activity implements LocationListener, LocationSource, OnClickListener, LoaderManager.LoaderCallbacks<Address> {

    private static final int ADDRESSLOADER_ID = 0;
    private static final int CURSORLOADER_ID = 1;
    private GoogleMap mMap;
    private OnLocationChangedListener mListener;    //ロケーションソース
    private LocationManager locationManager;
    private double mLat = 0;
    private double mLon = 0;
    private EditText edText;
    private DatabaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null) {
            boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean netIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (gpsIsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500L, 2.0f, this);
            } else if (netIsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500L, 2.0f, this);
            }
        } else {
            Toast.makeText(this, "locationManager is null", Toast.LENGTH_SHORT).show();
        }
        setUpMapIfNeeded();
        edText = (EditText) findViewById(R.id.editText);
        Button Btn = (Button) findViewById(R.id.button);
        Btn.setOnClickListener(this);
        Button btn2 = (Button) findViewById(R.id.button2);
        btn2.setOnClickListener(this);
        dbhelper = new DatabaseHelper(this);

    }


    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStart(){
        super.onStart();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStop(){
        if(locationManager != null){
            locationManager.removeUpdates(this);
        }
        onStop();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.G_Map)).getMap();
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
            mMap.setLocationSource(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_main,menu);

        return true;
    }

    //ローダーコールバックス
    @Override
    public Loader<Address> onCreateLoader(int id, Bundle args) {
        double lat = args.getDouble("lat");
        double lon = args.getDouble("lon");
        return new AddressTaskLoader(this,lat,lon);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.markar_on:
                MarkerOn();
                return true;
            case R.id.markar_off:
                MarkerOff();
                return true;
        }
        return false;
    }


    private void MarkerOn() {
        getLoaderManager().restartLoader(CURSORLOADER_ID, null, curCallback);
    }

    private void MarkerOff() {
        mMap.clear();
    }

    @Override
    public void onLoadFinished(Loader<Address> loader, Address result) {
        if (result != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < result.getMaxAddressLineIndex() +1;i++){
                String item = result.getAddressLine(i);
                if (item == null){
                    break;
                }
                sb.append(item);
            }
            edText.setText(sb.toString());

        }
    }

    @Override
    public void onLoaderReset(Loader<Address> loader) {

    }

    LoaderManager.LoaderCallbacks<Cursor> curCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projection = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_LAT,
                    DatabaseHelper.COLUMN_LON, DatabaseHelper.COLUMN_ADDRESS, DatabaseHelper.COLUMN_DATE};
            CursorLoader cursorLoader = new CursorLoader(MainActivity.this, DejaVuContentProvider.CONTENT_URI, projection, null, null, null);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
            cursor.moveToFirst();
            String strText = "";
            while (!cursor.isAfterLast()) {
                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(cursor.getDouble(1), cursor.getDouble(2)));
                options.title(cursor.getString(3));
                options.snippet(cursor.getString(4));
                mMap.addMarker(options);
                cursor.moveToNext();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };



    //ロケーションリスナー　（現在地が変わったときの通知を受け取る）
    @Override
    public void onLocationChanged(Location location) {//位置が変わったときに呼び出される
        if (mListener != null){
            mListener.onLocationChanged(location);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            mLat = location.getLatitude();
            mLon = location.getLongitude();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {   //ロケーションプロバイダの状態が変わったときに呼び出される
    }

    @Override
    public void onProviderEnabled(String arg0) {
        Toast.makeText(this,"provider enabled",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this,"provider disabled",Toast.LENGTH_SHORT).show();
    }


    //ロケーションソース
    @Override
    public void activate(OnLocationChangedListener listener) {
    mListener = listener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    //オンクリックリスナー
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                getAddressByLoader();
                return;
            case R.id.button2:
                showDialog();
                return;

            default:
                break;
        }
    }

    private void getAddressByLoader() {
        if (mLat != 0){
            Bundle args = new Bundle();
            args.putDouble("lat",mLat);
            args.putDouble("lon", mLon);

            getLoaderManager().restartLoader(ADDRESSLOADER_ID, args, this);
        }
    }

     void showDialog() {
         String address = edText.getText().toString();
         if (address.equals("")){
             Toast.makeText(this,"住所を取得してください",Toast.LENGTH_SHORT).show();
             edText.requestFocus();
             return;
         }
         DialogFragment newFragment = MyAlertDialogFragmant.newInstance(
                 R.string.alert_dialog_confirm_title, R.string.alert_dialog_confirm_message
         );

         newFragment.show(getFragmentManager(), "dialog");
    }

    public void doPositiveClick(){
        savepoint();
        savePointViaCTP();
    }

    public void doNegativeClick(){
    }

    private void savePointViaCTP() {
        ContentValues values = new ContentValues();
        double lat = mLat;
        double lon = mLon;
        String address = edText.getText().toString();
        String strDate = new SimpleDateFormat("yyyy-mm-dd", Locale.US).format(new Date());

        values.put(DatabaseHelper.COLUMN_LAT,lat);
        values.put(DatabaseHelper.COLUMN_LON,lon);
        values.put(DatabaseHelper.COLUMN_ADDRESS,address);
        values.put(DatabaseHelper.COLUMN_DATE, strDate);
        getContentResolver().insert(DejaVuContentProvider.CONTENT_URI, values);
        Toast.makeText(this,"データを保存しました",Toast.LENGTH_SHORT).show();
    }

    private void savepoint(){
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        double lat = mLat;
        double lon = mLon;
        String address = edText.getText().toString();
        String strDate = new SimpleDateFormat("yyyy-mm-dd",Locale.US).format(new Date());

        values.put("lat", lat);
        values.put("lon", lon);
        values.put("address", address);
        values.put("date", strDate);
        try {
            db.insert("dejavu", null, values);
            Toast.makeText(this,"データを保存しました",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this,"保存に失敗しました",Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }
}