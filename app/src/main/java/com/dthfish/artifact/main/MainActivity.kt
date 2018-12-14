package com.dthfish.artifact.main

import android.os.Bundle
import com.dthfish.artifact.R
import com.dthfish.artifact.base.BaseActivity
import com.dthfish.artifact.bean.SearchBean
import com.dthfish.drawer.DrawerFragment

class MainActivity : BaseActivity() {

    private var mainFragment: MainFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainFragment = MainFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .add(R.id.fl_container, mainFragment)
            .add(R.id.fl_menu, DrawerFragment.newInstance())
            .commitAllowingStateLoss()

    }

    fun findHeroes(flag: Boolean) {
        mainFragment?.findHeroes(flag)


    }

    fun gotoSearch(searchBean: SearchBean) {

    }


}





