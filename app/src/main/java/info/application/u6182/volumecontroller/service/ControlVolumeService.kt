package info.application.u6182.volumecontroller.service

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice
import android.content.Intent;
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import info.application.u6182.volumecontroller.MainActivity
import info.application.u6182.volumecontroller.R
import info.application.u6182.volumecontroller.receiver.HeadSetActionReceiver
import info.application.u6182.volumecontroller.receiver.VolumeChangedActionReceiver

class ControlVolumeService : Service() {

    //音量変更検知レシーバー
    private val volumeChangedActionReceiver = VolumeChangedActionReceiver()
    //ヘッドセット接続検知レシーバー
    private val headSetActionReceiver = HeadSetActionReceiver()

    companion object{
        const val ONGOING_NOTIFICATION_ID: Int = 1
    }

    /**
     * サービスが作成される際に呼び出される。
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    @Override
    override fun onCreate() {
        super.onCreate()

    }

    /**
     * サービスが開始される際に呼び出される。ここではレシーバーの登録と通知の設定を行う。
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    @Override
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //音量変更検知のレシーバー登録
        val volumeChangedIntentFilter = IntentFilter("android.media.VOLUME_CHANGED_ACTION")
        registerReceiver(volumeChangedActionReceiver, volumeChangedIntentFilter)

        //ヘッドセット接続検知のレシーバー登録
        val headSetIntentFilter = IntentFilter().apply {
            addAction(AudioManager.ACTION_HEADSET_PLUG)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        registerReceiver(headSetActionReceiver, headSetIntentFilter)

        //チャンネルIDの登録
        val channelID = "VolumeControllerID"
        createNotificationChannel(channelID)

        //通知をタップした時にアプリを呼び出す準備
        val notificationIntent = Intent(this, MainActivity::class.java)
        intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        //通知の準備
        val builder = NotificationCompat.Builder(this, channelID)
            .setContentTitle("VolumeControllerの起動中")
            .setContentText("メディア音量の制御中")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        //フォアグランドサービスの実行
        startForeground(ONGOING_NOTIFICATION_ID, builder.build())

        return START_STICKY;
    }

    /**
     * サービスが破棄される際、レシーバーの登録解除を行う。
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    @Override
    override fun onDestroy() {
        //レシーバーの解除
        unregisterReceiver(volumeChangedActionReceiver)
        unregisterReceiver(headSetActionReceiver)
        //通知とサービスの切り離し
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Android8.0(API Level26)以上の場合
            stopForeground(Service.STOP_FOREGROUND_DETACH)
        }else{
            stopForeground(false)
        }
        //サービスの停止
        stopSelf()
        super.onDestroy()
    }

    /**
     * サービスがバインドされる際に呼び出される。このサービスではバインドは不要なのでnullを返す。
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    @Override
    override fun onBind(intent: Intent?): IBinder? {
        return null;
    }

    /**
     * 通知チャンネルを作成する。APIレベル26以上でのみ必要。
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    fun createNotificationChannel(channelID: String){
        //API26以下の場合
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            //アプリの通知に表示される情報
            val name = "VolumeController起動中"
            val description = "端末のメディア音量を制御しています"

            //通知のレベル、名称等設定
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance)
            channel.description = description

            //通知の登録
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel((channel))
        }
    }
}