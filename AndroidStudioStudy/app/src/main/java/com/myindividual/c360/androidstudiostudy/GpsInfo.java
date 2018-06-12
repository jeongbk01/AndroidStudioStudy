package com.myindividual.c360.androidstudiostudy;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

public class GpsInfo extends Service implements LocationListener {

    private final Context mContext;
    //현재 GPS 사용유무
    boolean isGPSEnable = false;

    //네트워크 사용유무
    boolean isNetworkEnabled = false;

    //GPS 상태값
    boolean isGetLocation = false;

    Location location;

    double lat; //위도
    double lon; //경도
    float acc;  //정확도

    //최소 GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

    //최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
    private static final long MIN_TIME_BW_UPDATES = 100;
    protected LocationManager locationManager;

    public GpsInfo(Context context){
        this.mContext = context;
        getLocation();
    }

    @TargetApi(23)
    public Location getLocation(){
        if(Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){
            showSettingAlert("gps");
            return null;
        }
        try{
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //GPS 정보 가져오기
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnable && !isNetworkEnabled){
                showSettingAlert("gps");
            }else{
                isGetLocation = true;

                if(isGPSEnable){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(location != null){
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }

                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if(location != null){
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return location;
    }

    /**
     * GPS 종료
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GpsInfo.this);
        }
    }

    /**
     * 위도값을 가져옵니다.
     * */
    public double getLatitude(){
        if(getLocation() != null){
            lat = getLocation().getLatitude();
        }
        return lat;
    }

    /**
     * 경도값을 가져옵니다.
     * */
    public double getLongitude(){
        if(getLocation() != null){
            lon = getLocation().getLongitude();
        }
        return lon;
    }
    /**
     * 정확도값을 가져옵니다.
     * */
    public float getAcc(){
        if(getLocation() != null){
            acc = getLocation().getAccuracy();
        }
        return acc;
    }

    /**
     * GPS 나 wifi 정보가 켜져있는지 확인합니다.
     * */
    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    public void showSettingAlert(String type){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        if(type.equals("gps")){
            alertDialog.setTitle("GPS 사용유무 셋팅");
            alertDialog.setMessage("GPS 셋팅이 되어 있지 않습니다. \n 설정창으로 이동 하시겠습니까?");
            alertDialog.setPositiveButton("settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });
            alertDialog.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }

        alertDialog.show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

}
