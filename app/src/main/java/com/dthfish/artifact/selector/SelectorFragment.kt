package com.dthfish.artifact.selector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dthfish.artifact.R
import com.dthfish.artifact.base.BaseFragment
import com.dthfish.artifact.bean.SelectBean
import com.dthfish.artifact.main.MainActivity
import kotlinx.android.synthetic.main.fragment_selector.*

/**
 * Description
 * Author DthFish
 * Date  2018/12/13.
 */
class SelectorFragment : BaseFragment() {
    companion object {
        fun newInstance(): SelectorFragment {
            return SelectorFragment()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_selector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        ctvHero.setOnClickListener {
            ctvHero.toggle()

        }
        // 主卡
        ctvMain.setOnClickListener {
            ctvMain.toggle()
            if (ctvMain.isChecked) {
                ctvCreep.isChecked = false
                ctvSpell.isChecked = false
                ctvImprovement.isChecked = false
            }
        }
        ctvCreep.setOnClickListener {
            ctvCreep.toggle()
            if (ctvMain.isChecked) {
                ctvMain.isChecked = false
            }
        }
        ctvSpell.setOnClickListener {
            ctvSpell.toggle()
            if (ctvMain.isChecked) {
                ctvMain.isChecked = false
            }

        }
        ctvImprovement.setOnClickListener {
            ctvImprovement.toggle()
            if (ctvMain.isChecked) {
                ctvMain.isChecked = false
            }

        }
        // 物品
        ctvItem.setOnClickListener {
            ctvItem.toggle()
            if (ctvItem.isChecked) {
                ctvWeapon.isChecked = false
                ctvArmor.isChecked = false
                ctvAccessory.isChecked = false
                ctvConsumable.isChecked = false
            }
        }
        ctvWeapon.setOnClickListener {
            ctvWeapon.toggle()
            if (ctvItem.isChecked) {
                ctvItem.isChecked = false
            }
        }
        ctvArmor.setOnClickListener {
            ctvArmor.toggle()
            if (ctvItem.isChecked) {
                ctvItem.isChecked = false
            }
        }
        ctvAccessory.setOnClickListener {
            ctvAccessory.toggle()
            if (ctvItem.isChecked) {
                ctvItem.isChecked = false
            }
        }
        ctvConsumable.setOnClickListener {
            ctvConsumable.toggle()
            if (ctvItem.isChecked) {
                ctvItem.isChecked = false
            }
        }
        // 颜色
        tvColor.setOnClickListener {
            ctvRed.isChecked = false
            ctvGreen.isChecked = false
            ctvBlue.isChecked = false
            ctvBlack.isChecked = false

        }
        ctvRed.setOnClickListener {
            ctvRed.toggle()
        }
        ctvGreen.setOnClickListener {
            ctvGreen.toggle()
        }
        ctvBlue.setOnClickListener {
            ctvBlue.toggle()
        }
        ctvBlack.setOnClickListener {
            ctvBlack.toggle()
        }
        // 稀有度
        tvRarity.setOnClickListener {
            ctvIron.isChecked = false
            ctvCopper.isChecked = false
            ctvSilver.isChecked = false
            ctvGold.isChecked = false

        }
        ctvIron.setOnClickListener {
            ctvIron.toggle()
        }
        ctvCopper.setOnClickListener {
            ctvCopper.toggle()
        }
        ctvSilver.setOnClickListener {
            ctvSilver.toggle()
        }
        ctvGold.setOnClickListener {
            ctvGold.toggle()
        }

        btnSearch.setOnClickListener {
            SelectBean().apply {
                this.types[0] = ctvHero.isChecked
                this.types[1] = ctvMain.isChecked or ctvCreep.isChecked
                this.types[2] = ctvMain.isChecked or ctvSpell.isChecked
                this.types[3] = ctvMain.isChecked or ctvImprovement.isChecked

                this.itemTypes[0] = ctvItem.isChecked or ctvWeapon.isChecked
                this.itemTypes[1] = ctvItem.isChecked or ctvArmor.isChecked
                this.itemTypes[2] = ctvItem.isChecked or ctvAccessory.isChecked
                this.itemTypes[3] = ctvItem.isChecked or ctvConsumable.isChecked//Consumable
                this.itemTypes[4] = ctvItem.isChecked or ctvConsumable.isChecked// deed 归为 Consumable

                this.colors[0] = ctvRed.isChecked
                this.colors[1] = ctvGreen.isChecked
                this.colors[2] = ctvBlue.isChecked
                this.colors[3] = ctvBlack.isChecked

                this.rarities[0] = ctvIron.isChecked
                this.rarities[1] = ctvCopper.isChecked
                this.rarities[2] = ctvSilver.isChecked
                this.rarities[3] = ctvGold.isChecked

                (activity as MainActivity).doSelect(this)
            }
        }

    }
}