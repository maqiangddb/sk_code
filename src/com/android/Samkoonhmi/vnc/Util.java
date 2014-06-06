package com.android.Samkoonhmi.vnc;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by mqddb on 13-8-20.
 */
public class Util {
    public static final String VNC_TAG = "VNCserver";
    public static final boolean ENG = true;

    public static void LOGI(String msg) {
        if (ENG) {
            Log.i(VNC_TAG, msg);
        }
    }


    public static String getIpAddress() {

        try {
            String ipv4;
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                LOGI("===="+intf.toString());
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress()))
                            return ipv4;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static int makePort(String ip) {
        LOGI("makePort:"+ip);
        String[] datas = ip.trim().split("\\.");
        for (int i = 0 ; i < datas.length ; i++) {
            LOGI("="+datas[i]);
        }
        if (datas.length < 4) {
            return 0;
        }
        int d1 = Integer.parseInt(datas[0]);
        int d2 = Integer.parseInt(datas[1]);
        int d3 = Integer.parseInt(datas[2]);
        int d4 = Integer.parseInt(datas[3]);
        LOGI("["+d1+","+d2+","+d3+","+d4+"]");
        int result = (((d3 + d4) << 8) & 0xffff) | 0x400;
        LOGI("result;"+result);
        return result;
    }

    public static String getHttpPort(Context context) {
        String port= PreferenceManager.getDefaultSharedPreferences(context).getString("port", "843");
        String httpport;
        try
        {
            int port1=Integer.parseInt(port);
            port=String.valueOf(port1);
            httpport=String.valueOf(port1-100);
        }
        catch(NumberFormatException e)
        {
            port="843";
            httpport="743";
        }
        return httpport;
    }

}
