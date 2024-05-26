package com.ydd.zhichat.util;

/**
 * Created by Administrator on 2017/7/12 0012.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;

public class NetWorkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                Toast.makeText(context, MyApplication.getInstance().getString(R.string.b8), Toast.LENGTH_SHORT).show();
            } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                Toast.makeText(context, (R.string.b7), Toast.LENGTH_SHORT).show();
            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                Toast.makeText(context, R.string.a71, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, MyApplication.getInstance().getString(R.string.a70), Toast.LENGTH_SHORT).show();
            }
            //API大于23时使用下面的方式进行网络监听
        } else {
            System.out.println("API level 大于23");
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();
            //通过循环将网络信息逐个取出来
            for (int i = 0; i < networks.length; i++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
            }
            if (sb.toString().isEmpty()) {
            }
        }
    }
}
