package com.softwill.alpha.networking;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NetworkUtil {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isNetworkLow(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Log.e("Slow Internet", "....activeNetworkInfo.." + activeNetworkInfo);

        boolean isSlow=false;
        if (activeNetworkInfo!=null)
        {
            int netSubType = activeNetworkInfo.getSubtype();
            Log.e("Slow Internet", "....netSubType.." + netSubType+".=."+TelephonyManager.NETWORK_TYPE_GPRS);
            Log.e("Slow Internet", "....netSubType.." + netSubType+".=."+TelephonyManager.NETWORK_TYPE_EDGE);
            Log.e("Slow Internet", "....netSubType.." + netSubType+".=."+TelephonyManager.NETWORK_TYPE_1xRTT);
            Log.e("Slow Internet", "....netSubType.." + netSubType+".=."+TelephonyManager.NETWORK_TYPE_LTE);

            if (netSubType == TelephonyManager.NETWORK_TYPE_GPRS ||
                    netSubType == TelephonyManager.NETWORK_TYPE_EDGE ||
                    netSubType == TelephonyManager.NETWORK_TYPE_LTE ||
                    netSubType == TelephonyManager.NETWORK_TYPE_1xRTT) {
                //user is in slow network
                isSlow = true;
               // Log.e("Slow Internet", "............." + isSlow);
            }
        }else
        {
            isSlow = true;
        }
        return isSlow;
    }

    public static boolean isConnectedFast(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return (info != null && info.isConnected() && isConnectionFast(info.getType(),info.getSubtype()));
    }

    public static boolean isConnectionFast(int type, int subType){
        if(type==ConnectivityManager.TYPE_WIFI)
        {
            Log.e("Slow Internet", ".......TYPE_WIFI....true..");
            return true;
        }else if(type==ConnectivityManager.TYPE_MOBILE)
        {
            switch(subType)
            {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    Log.e("Slow Internet", ".......NETWORK_TYPE_1xRTT....false..");

                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    Log.e("Slow Internet", ".......NETWORK_TYPE_CDMA....false..");

                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    Log.e("Slow Internet", ".......NETWORK_TYPE_EDGE....false..");

                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    Log.e("Slow Internet", ".......NETWORK_TYPE_EVDO_0....true..");

                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    Log.e("Slow Internet", ".......NETWORK_TYPE_EVDO_A....true..");

                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    Log.e("Slow Internet", ".......NETWORK_TYPE_GPRS....false..");

                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    Log.e("Slow Internet", ".......NETWORK_TYPE_HSDPA....true..");

                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    Log.e("Slow Internet", ".......NETWORK_TYPE_HSPA....true..");

                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    Log.e("Slow Internet", ".......NETWORK_TYPE_HSUPA....true..");

                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    Log.e("Slow Internet", ".......NETWORK_TYPE_UMTS....true..");

                    return true; // ~ 400-7000 kbps
                /*
                 * Above API level 7, make sure to set android:targetSdkVersion
                 * to appropriate level to use these
                 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    Log.e("Slow Internet", ".......NETWORK_TYPE_EHRPD....true..");

                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    Log.e("Slow Internet", ".......NETWORK_TYPE_EVDO_B....true..");

                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    Log.e("Slow Internet", ".......NETWORK_TYPE_HSPAP....true..");

                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    Log.e("Slow Internet", ".......NETWORK_TYPE_IDEN....true..");

                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    Log.e("Slow Internet", ".......NETWORK_TYPE_LTE....true..");

                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    Log.e("Slow Internet", ".......NETWORK_TYPE_UMTS....true..");

                default:
                    return false;
            }
        }else{
            return false;
        }
    }

    public static boolean ConnectionStatus(Context context){
        boolean connection = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if (info != null && info.isConnected()){
            if(info.getType() == ConnectivityManager.TYPE_WIFI){
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                int linkSpeed = wifiManager.getConnectionInfo().getRssi();
                int level = WifiManager.calculateSignalLevel(linkSpeed, 5);
                connection = level >= 2;
            } else if(info.getType() == ConnectivityManager.TYPE_MOBILE){
                if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS){
                    // Bandwidth between 100 kbps and below
                    connection = false;
                } else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE){
                    // Bandwidth between 50-100 kbps
                    connection = false;
                } else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_0){
                    // Bandwidth between 400-1000 kbps
                    connection = true;
                } else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_A){
                    // Bandwidth between 600-1400 kbps
                    connection = true;
                } else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_1xRTT){
                    // Bandwidth between 50-100 kbps
                    connection = false;
                } else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA){
                    // Bandwidth between 14-64 kbps
                    connection = false;
                } else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSDPA){
                    // Bandwidth between 2-14 Mbps
                    connection = true;
                } else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSPA){
                    // Bandwidth between 700-1700 kbps
                    connection = true;
                } else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSUPA){
                    // Bandwidth between 1-23 Mbps
                    connection = true;
                } else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_UMTS){
                    // Bandwidth between 400-7000 kbps
                    connection = true;
                } else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_EHRPD){
                    // Bandwidth between 1-2 Mbps
                    connection = true;
                } else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_B){
                    // Bandwidth between 5 Mbps
                    connection = true;
                } else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSPAP){
                    // Bandwidth between 10-20 Mbps
                    connection = true;
                } else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_IDEN){
                    // Bandwidth between ~25 kbps
                    connection = false;
                } else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE){
                    // Bandwidth between 10+ Mbps
                    connection = true;
                }else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_UNKNOWN){
                    // Unknown
                    connection = false;
                }
            }
        }else{
            return  false;
        }

        return  connection;
    }

    public static Boolean getConnectivityStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}