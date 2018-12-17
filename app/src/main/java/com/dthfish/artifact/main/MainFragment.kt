package com.dthfish.artifact.main

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dthfish.artifact.R
import com.dthfish.artifact.base.BaseFragment
import com.dthfish.artifact.bean.Card
import com.dthfish.artifact.bean.CardBean
import com.dthfish.artifact.bean.SearchBean
import com.dthfish.artifact.common.log
import com.dthfish.artifact.db.DBManager
import com.dthfish.artifact.utils.CARD_TYPE_ARRAY
import com.dthfish.artifact.utils.CardType
import com.dthfish.artifact.utils.RARITY_TYPE_ARRAY
import com.dthfish.artifact.utils.SUB_CARD_TYPE_ARRAY
import com.liaoinstan.springview.container.DefaultFooter
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import kotlinx.android.synthetic.main.fragment_main.*
import org.jetbrains.anko.support.v4.toast
import org.litepal.LitePal
import org.litepal.extension.findAsync

/**
 * Description
 * Author DthFish
 * Date  2018/12/13.
 */
class MainFragment : BaseFragment() {

    companion object {
        fun newInstance(): MainFragment {

            return MainFragment()
        }
    }

    private var adapter: CommonAdapter<CardBean>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        adapter = object : CommonAdapter<CardBean>(context, R.layout.item_card, mutableListOf()) {
            override fun convert(holder: ViewHolder?, bean: CardBean?, position: Int) {
                holder?.getView<ImageView>(R.id.iv_icon)?.let {
                    Glide.with(this@MainFragment).load(bean?.mini_image?.default).into(it)
                }
                holder?.getView<TextView>(R.id.tv_name)?.let {
                    it.text = bean?.card_name?.schinese
                }

            }
        }
        adapter!!.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {
            override fun onItemLongClick(view: View?, holder: RecyclerView.ViewHolder?, position: Int): Boolean {
                return false
            }

            override fun onItemClick(view: View?, holder: RecyclerView.ViewHolder?, position: Int) {
                adapter!!.datas[position].apply {

                    this@MainFragment.toast(this.card_name?.schinese.toString())
                }
            }

        })
        rv.adapter = adapter

        springView.isEnableFooter = true
        // 装作我在用分页加载的样子（滑稽）
        springView.footer = DefaultFooter(context)
        DBManager.instance.queryAllCard { it ->
            it.map { it.convent2CardBean() }.let {
                adapter!!.datas.addAll(it)
                adapter!!.notifyDataSetChanged()
            }
        }

    }

    fun findHeroes(flag: Boolean) {
        if (flag) {
            LitePal.where("rarity == ?", "Uncommon")
                .findAsync<Card>()
                .listen { it ->
                    it.map { it.convent2CardBean() }.let { list ->
                        adapter?.datas?.let {
                            it.clear()
                            it.addAll(list)
                            adapter?.notifyDataSetChanged()
                        }

                    }
                }
        }
    }

    fun doSearch(searchBean: SearchBean) {
        var types = mutableListOf<String>()
        var subTypes = mutableListOf<String>()
        var colors = mutableListOf<String>()
        var rarities = mutableListOf<String>()

        searchBean.types.forEachIndexed { index, b ->
            if (b) {
                types.add(CARD_TYPE_ARRAY[index])
            }
        }
        searchBean.itemTypes.forEachIndexed { index, b ->
            if (b) {
                subTypes.add(SUB_CARD_TYPE_ARRAY[index])
            }
        }

        searchBean.colors.forEachIndexed { index, b ->
            if (b) {

            }
        }
        searchBean.rarities.forEachIndexed { index, b ->
            if (b) {
                if (index == 0) {
                    rarities.add("IS NULL")
                } else {
                    rarities.add(RARITY_TYPE_ARRAY[index])
                }
            }
        }
        var conditions = ""
        var ignoreSub = false
        if (subTypes.size == 5) {
            types.add(CardType.ITEM)
            ignoreSub = true
        } else if (subTypes.isEmpty()) {
            ignoreSub = true
        }

        var hasSpell = false
        var hasImprovement = false
        // Spell Improvement类型中要去掉 item_def 为空的条目
        if ((types.isEmpty() && ignoreSub) || types.size == 5) {
            conditions =
                    "(card_type IN ('${CardType.HERO}','${CardType.ITEM}','${CardType.CREEP}'))"
            hasSpell = true
            hasImprovement = true
        } else if (types.size == 1 && types[0] == CardType.SPELL) {
            conditions = "(card_type == '${CardType.SPELL}' AND item_def IS NOT NULL)"

        } else if (types.size == 1 && types[0] == CardType.IMPROVEMENT) {
            conditions = "(card_type == '${CardType.IMPROVEMENT}' AND item_def IS NOT NULL)"

        } else if (types.size == 2 && types.containsAll(listOf(CardType.IMPROVEMENT, CardType.SPELL))) {
            conditions = "(card_type IN ('${CardType.IMPROVEMENT}','${CardType.SPELL}') AND item_def IS NOT NULL)"

        } else if(!types.isEmpty()){
            conditions = "(card_type IN ("
            types.forEach {
                when (it) {
                    CardType.SPELL -> hasSpell = true
                    CardType.IMPROVEMENT -> hasImprovement = true
                    else -> conditions += "'$it',"
                }
            }
            conditions = conditions.substring(0, conditions.length - 1)
            conditions += "))"
        }

        if (hasSpell) {
            conditions += " OR (card_type == '${CardType.SPELL}' AND item_def IS NOT NULL)"
        }
        if (hasImprovement) {
            conditions += " OR (card_type == '${CardType.IMPROVEMENT}' AND item_def IS NOT NULL)"
        }


        conditions.log()
        if (!ignoreSub) {
            if(!conditions.isEmpty()){
                conditions += " OR "
            }
            conditions += "(sub_type IN ("
            subTypes.forEach {
                conditions += "'$it',"
            }
            conditions = conditions.substring(0, conditions.length - 1)
            conditions += "))"
        }

        DBManager.instance.queryCardByCondition(conditions) {
            it.map { it.convent2CardBean() }.let { list ->
                adapter?.datas?.let {
                    it.clear()
                    it.addAll(list)
                    adapter?.notifyDataSetChanged()
                }

            }
        }


    }
}