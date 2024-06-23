package info.application.u6182.volumecontroller

import android.media.AudioManager

object HeadSet {

    //オーディオマネージャー
    private var audioManager: AudioManager? = null
    //ヘッドセット接続時の制御音量
    public var connectonControlVolume = 15
    //ヘッドセット非接続時の制御音量
    public var disConnectionControlVolume = 0

    /**
     * オーディオマネージャーを設定する。
     * @param audioManager 設定するオーディオマネージャー
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    public fun setAudioManager(audioManager: AudioManager){
        HeadSet.audioManager = audioManager
    }

    /**
     * メディア音量を制御する。
     * @param controlVolume 制御する音量
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    public fun controlVolume(controlVolume: Int){
        val volume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)
        //メディア音量を設定された音量で制御を行う
        if(volume!! > controlVolume){
            audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, controlVolume, AudioManager.FLAG_SHOW_UI)
        }
    }

    /**
     * ヘッドセットが接続されていないかどうかを確認する。
     * @return ヘッドセットが接続されていない場合はtrue、それ以外はfalse
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    public fun isDisconnection(): Boolean{
        //ヘッドセットが接続されていない場合
        return !(audioManager!!.isWiredHeadsetOn || audioManager!!.isBluetoothScoOn || audioManager!!.isBluetoothA2dpOn)
    }
}