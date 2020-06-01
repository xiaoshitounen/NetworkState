package swu.xl.networkstate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import static android.net.NetworkCapabilities.TRANSPORT_CELLULAR;

public class MainActivity extends AppCompatActivity {

    //回调
    ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback(){

        //可用网络接入
        //当我们的网络的某个能力发生了变化回调，会回调多次
        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            //一般在此处获取网络类型然后判断网络类型

            //Toast.makeText(MainActivity.this, "网络接入", Toast.LENGTH_SHORT).show();
        }

        //网络可用的回调
        @Override
        public void onAvailable(@NonNull Network network) {
            //和 onLost 成对出现

            Toast.makeText(MainActivity.this, "网络可用", Toast.LENGTH_SHORT).show();
        }

        //网络断开
        @Override
        public void onLost(@NonNull Network network) {
            //如果通过ConnectivityManager#getActiveNetwork()返回null，表示当前已经没有其他可用网络了。

            Toast.makeText(MainActivity.this, "网络断开", Toast.LENGTH_SHORT).show();
        }
    };

    //注册回调
    private void registerNetworkCallback(Context context) {
        //获取 ConnectivityManager
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;

        //获取 NetworkRequest 的 Builder
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        //强制使用蜂窝数据网络-移动数据
        //builder.addTransportType(TRANSPORT_CELLULAR);
        //强制使用wifi网络
        //builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);

        //注册
        NetworkRequest networkRequest = builder.build();
        connectivityManager.registerNetworkCallback(networkRequest,callback);
    }

    //注销回调
    private void unregisterNetworkCallback(Context context) {
        //获取 ConnectivityManager
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;

        //注销
        connectivityManager.unregisterNetworkCallback(callback);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //首先判断有没有网络
        /*if (isOnline(this)) {
            Toast.makeText(this, "有网络", Toast.LENGTH_SHORT).show();

            //继续判断什么类型网络
            testNetWorkState(this);

        }else {
            Toast.makeText(this, "没有网络", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerNetworkCallback(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterNetworkCallback(this);
    }

    /**
     * 判断当前网络状态
     * @param context
     * @return
     */
    private boolean testNetWorkState(@NonNull Context context){
        //获取 ConnectivityManager
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;

        //如果版本大于23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //获取 NetWork
            Network network = connectivityManager.getActiveNetwork();

            if(network!=null){
                //获取 NetworkCapabilities
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);

                if(networkCapabilities != null){
                    if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                        //WIFI
                        Toast.makeText(context, "WIFI", Toast.LENGTH_SHORT).show();
                        return true;
                    }else if(networkCapabilities.hasTransport(TRANSPORT_CELLULAR)){
                        //移动数据
                        Toast.makeText(context, "mobile", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
            }

        }else {
            //获取NetworkInfo
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    //WIFI
                    Toast.makeText(context, "WIFI", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    //移动数据
                    Toast.makeText(context, "mobile", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断是否联网
     */
    private boolean isOnline(@NonNull Context context){
        //获取 ConnectivityManager
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;

        //如果版本大于23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //获取 NetWork
            Network network = connectivityManager.getActiveNetwork();

            if(network != null) {
                //获取 NetworkCapabilities
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);

                return (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET));
            }

        }else {

            //获取NetworkInfo
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            return (networkInfo != null && networkInfo.isConnected());
        }

        return false;
    }
}