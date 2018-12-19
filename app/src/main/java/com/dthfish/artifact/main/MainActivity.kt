package com.dthfish.artifact.main

import android.os.Bundle
import com.dthfish.artifact.R
import com.dthfish.artifact.base.BaseActivity
import com.dthfish.artifact.bean.SelectBean
import com.dthfish.artifact.selector.SelectorFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private var mainFragment: MainFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainFragment = MainFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .add(R.id.fl_container, mainFragment)
            .add(R.id.fl_menu, SelectorFragment.newInstance())
            .commitAllowingStateLoss()

    }

    fun doSelect(selectBean: SelectBean) {
        layoutDrawer.closeDrawers()
        mainFragment?.doSelect(selectBean)
    }


}





