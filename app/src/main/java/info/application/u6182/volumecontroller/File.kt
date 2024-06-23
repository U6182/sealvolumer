package info.application.u6182.volumecontroller

import android.content.Context
import java.io.*

object File{

    /**
     * テキストを保存する
     * @param path 保存先ファイルのパス
     * @param text 保存するテキスト
     * @return 保存に成功した場合はtrue、それ以外はfalse
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    public fun saveText(path: String, text: String): Boolean{
        var bResult = true
        var outputStreamWriter: OutputStreamWriter? = null
        try{
            val fileOutputStream = MyContext.getAppContext().openFileOutput(path, Context.MODE_PRIVATE)
            outputStreamWriter = OutputStreamWriter(fileOutputStream)
            outputStreamWriter.write(text)
            outputStreamWriter.flush()
        }catch (e: IOException){
            e.printStackTrace()
            bResult = false
        }finally {
            if(outputStreamWriter != null){
                try {
                    outputStreamWriter.close()
                }catch (e: IOException){
                }
            }
        }

        return bResult
    }

    /**
     * ファイルの内容を読み込む
     * @param path 読み込むファイルのパス
     * @return ファイルの内容の文字列
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    public fun readText(path: String): String{
        var inputStreamReader: InputStreamReader? = null
        var text: String? = ""
        try {
            val fileInputStream = MyContext.getAppContext().openFileInput(path)
            inputStreamReader = InputStreamReader(fileInputStream)
            text = inputStreamReader.readText()
        }catch (e: IOException){
            e.printStackTrace()
        }finally {
            if(inputStreamReader != null){
                try {
                    inputStreamReader.close()
                }catch (e: IOException){
                }
            }
        }

        return text!!
    }
}

/**
 * 保存パスを定義する
 * @author 木村 憂哉
 * 作成日：2024-03-07
 */
enum class SAVE(val path: String){
    CONNECTIONCONTROLVOLUME("connectionControlVolume.txt"), DISCONNECTIONCONTROLVOLUME("disConnectionControlVolume.txt"), SERVICEDESTROY("serviceDestroy.txt"),
}