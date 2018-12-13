package com.dthfish.artifact

import android.support.multidex.MultiDexApplication
import com.dthfish.artifact.db.DBManager

/**
 * Description
 * Author DthFish
 * Date  2018/12/7.
 */
class CardApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        DBManager.instance.init(this)
    }
}
