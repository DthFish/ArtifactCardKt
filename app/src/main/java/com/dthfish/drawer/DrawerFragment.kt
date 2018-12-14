package com.dthfish.drawer

import android.os.Bundle
import android.support.v7.widget.AppCompatCheckedTextView
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
            (it as AppCompatCheckedTextView).toggle()
            (activity as MainActivity).findHeroes(ctvHero.isChecked)

        }
        ctvMain.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()
        }

        ctvCreep.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()

        }
        ctvSpell.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()


        }
        ctvImprovement.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()

        }

        ctvItem.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()
        }
        ctvWeapon.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()
        }
        ctvArmor.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()
        }
        ctvAccessory.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()
        }
        ctvConsumable.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()
        }
        tvColor.setOnClickListener {

        }
        ctvRed.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()
        }
        ctvGreen.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()
        }
        ctvBlue.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()
        }
        ctvBlack.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()
        }
        tvRarity.setOnClickListener {

        }
        ctvIron.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()
        }
        ctvCopper.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()
        }
        ctvSilver.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()
        }
        ctvGold.setOnClickListener {
            (it as AppCompatCheckedTextView).toggle()
        }

    }
}