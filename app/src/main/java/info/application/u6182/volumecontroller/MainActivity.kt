package info.application.u6182.volumecontroller

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.NumberPicker
import android.widget.Toast
import info.application.u6182.volumecontroller.R
import info.application.u6182.volumecontroller.service.ControlVolumeService

class MainActivity : AppCompatActivity() {

    //サービス破棄フラグ
    private var isNotServiceDestory = false

    /**
     * アクティビティが作成された時に呼ばれるメソッド。
     * 各種設定を行う。
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //コンテキストの設定
        MyContext.createApp(this.applicationContext)
        //ヘッドセット接続時のNumberPickerの設定
        setConnectionControlVolume()
        //ヘッドセット非接続時のNumberPickerの設定
        setDisConnectionControlVolume()
        //登録ボタンの設定
        setVolumeRegisterBtn()

        //音量制御のためのオーディオマネージャーの設定
        HeadSet.setAudioManager(getSystemService(AUDIO_SERVICE) as AudioManager)
        //音量制御のサービスに渡すインテントの生成

        //メディア音量の初期制御
        val volume = if(HeadSet.isDisconnection()){
            HeadSet.disConnectionControlVolume
        }else{
            HeadSet.connectonControlVolume
        }
        HeadSet.controlVolume(volume)

        //チェックボックスでサービス破棄設定を設定する
        val serviceDestoryCheckBox = findViewById<CheckBox>(R.id.serviceDestoryCheckBox)
        serviceDestoryCheckBox.isChecked = File.readText(SAVE.SERVICEDESTROY.path).toBoolean()
        this.isNotServiceDestory = serviceDestoryCheckBox.isChecked
            serviceDestoryCheckBox.setOnClickListener{
            this.isNotServiceDestory = !this.isNotServiceDestory
            val bResult =
                File.saveText(SAVE.SERVICEDESTROY.path, this.isNotServiceDestory.toString())
        }

        val intent = Intent(this, ControlVolumeService::class.java)
        //サービスの開始
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Android8.0(API Level26)以上の場合
            startForegroundService(intent)
        }else{
            startService(intent)
        }
    }

    /**
     * アクティビティが破棄される時に呼ばれるメソッド。
     * 必要に応じてサービスを停止する。
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    override fun onDestroy() {
        val intent = Intent(this, ControlVolumeService::class.java)
        //サービスの停止
        if(!isNotServiceDestory){
            stopService(intent)
        }
        super.onDestroy()
    }

    /**
     * ヘッドセット接続時の音量制御設定を行う。
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    fun setConnectionControlVolume(){
        //ヘッドセット接続時のNumberPickerの設定
        val connectionControlVolumePicker = findViewById<NumberPicker>(R.id.inputConnectionControlVolumePicker)
        connectionControlVolumePicker.maxValue = 15
        connectionControlVolumePicker.minValue = 0

        //最大値を決めてからでないと、初期最大値が0のため初期化されない
        var strVolume = File.readText(SAVE.CONNECTIONCONTROLVOLUME.path)
        val volume = if(strVolume.equals("")) 15 else strVolume.toInt()
        connectionControlVolumePicker.value = volume
        connectionControlVolumePicker.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        HeadSet.connectonControlVolume = volume
    }

    /**
     * ヘッドセット非接続時の音量制御設定を行う。
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    fun setDisConnectionControlVolume(){
        //ヘッドセット非接続時のNumberPickerの設定
        val disConnectionControlVolumePicker = findViewById<NumberPicker>(R.id.inputDisConnectionControlVolumePicker)
        disConnectionControlVolumePicker.maxValue = 15
        disConnectionControlVolumePicker.minValue = 0

        var strVolume = File.readText(SAVE.DISCONNECTIONCONTROLVOLUME.path)
        val volume = if(strVolume.equals("")) 0 else strVolume.toInt()
        disConnectionControlVolumePicker.value = volume
        disConnectionControlVolumePicker.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        HeadSet.disConnectionControlVolume = volume
    }

    /**
     * 音量登録ボタンの設定を行う。
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    fun setVolumeRegisterBtn(){
        //登録ボタンの設定
        val connectionControlVolumePicker = findViewById<NumberPicker>(R.id.inputConnectionControlVolumePicker)
        val disConnectionControlVolumePicker = findViewById<NumberPicker>(R.id.inputDisConnectionControlVolumePicker)
        val volumeRegisterBtn = findViewById<Button>(R.id.volumeRegisterBtn)
        volumeRegisterBtn.setOnClickListener {
            //制御メディア音量の保存
            val volumeConnection = connectionControlVolumePicker.value
            val volumeDisConnection = disConnectionControlVolumePicker.value
            val bResultConnection =
                File.saveText(SAVE.CONNECTIONCONTROLVOLUME.path, volumeConnection.toString())
            val bResultDisConnection =
                File.saveText(SAVE.DISCONNECTIONCONTROLVOLUME.path, volumeDisConnection.toString())
            var message = ""
            if(bResultConnection && bResultDisConnection){
                message = "ヘッドセット接続時の最大音量を" + volumeConnection + "\nヘッドセット非接続時の最大音量を" + volumeDisConnection + "\nに登録しました"
                //ヘッドセットに設定
                HeadSet.connectonControlVolume = volumeConnection
                HeadSet.disConnectionControlVolume = volumeDisConnection
                if(!HeadSet.isDisconnection()){
                    HeadSet.controlVolume(HeadSet.connectonControlVolume)
                }else{
                    HeadSet.controlVolume(HeadSet.disConnectionControlVolume)
                }
            }else{
                message = "登録に失敗しました"
            }

            Toast.makeText(this.applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

}
