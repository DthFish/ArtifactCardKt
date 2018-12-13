package com.dthfish.artifact

import android.content.Intent
import android.os.Bundle
import com.dthfish.artifact.base.BaseActivity
import com.dthfish.artifact.db.DBManager
import com.dthfish.artifact.main.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*

/**
 * Description
 * Author DthFish
 * Date  2018/12/12.
 */
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        DBManager.instance.checkAndCreateCardDB({
            tv.text = "加载数据库中..."
        }) {
            toMain()
        }

    }

    private fun toMain() {
        tv.text = "SplashPage 两秒后进入..."
        tv.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }

}