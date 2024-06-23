package info.application.u6182.volumecontroller

import android.content.Context

object MyContext{

    //コンテキスト
    private lateinit var appContext: Context

    /**
     * アプリケーションコンテキストを設定する。
     * @param appContext アプリケーションコンテキスト
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    fun createApp(appContext: Context){
        MyContext.appContext = appContext
    }

    /**
     * 設定されたアプリケーションコンテキストを取得する。
     * @return アプリケーションコンテキスト
     * @author 木村 憂哉
     * 作成日：2024-03-07
     */
    fun getAppContext(): Context{
        return appContext
    }

}