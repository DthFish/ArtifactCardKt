package com.dthfish.drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dthfish.artifact.R
import com.dthfish.artifact.base.BaseFragment
import com.dthfish.artifact.main.MainActivity
import kotlinx.android.synthetic.main.fragment_drawer.*

/**
 * Description
 * Author DthFish
 * Date  2018/12/13.
 */
class DrawerFragment : BaseFragment() {
    companion object {
        fun newInstance(): DrawerFragment {
            return DrawerFragment()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_drawer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        ctvHero.setOnClickListener {
            ctvHero.isChecked = !ctvHero.isChecked
            (activity as MainActivity).findHeroes(ctvHero.isChecked)

        }
    }
}