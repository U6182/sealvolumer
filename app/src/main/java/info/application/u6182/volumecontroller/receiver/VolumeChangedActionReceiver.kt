package info.application.u6182.volumecontroller.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import info.application.u6182.volumecontroller.HeadSet

class VolumeChangedActionReceiver : BroadcastReceiver(){

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

        val action = intent.action
        //音量変更が行われた場合
        if(action.equals("android.media.VOLUME_CHANGED_ACTION")){
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