package info.application.u6182.volumecontroller.receiver

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import info.application.u6182.volumecontroller.HeadSet

class HeadSetActionReceiver() : BroadcastReceiver(){

    /**
     * ブロードキャストを受信した際に実行される
     * @param context コンテキスト
     * @param intent 受信したインテント
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        //nullチェック
        if(intent == null){
            return;
        }

        //ヘッドセット接続関連のイベントの場合
        if(intent.action == AudioManager.ACTION_HEADSET_PLUG || intent.action == BluetoothDevice.ACTION_ACL_CONNECTED || intent.action == BluetoothDevice.ACTION_ACL_DISCONNECTED){
            //メディア音量の制御を行う
            val volume = if(HeadSet.isDisconnection()){
                HeadSet.disConnectionControlVolume
            }else{
                //ヘッドセットの設定されたメディア音量で制御を行う
                HeadSet.connectonControlVolume
            }
            HeadSet.controlVolume(volume)
        }
    }
}