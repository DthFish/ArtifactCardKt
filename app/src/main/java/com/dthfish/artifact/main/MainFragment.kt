package com.dthfish.artifact.main

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dthfish.artifact.R
import com.dthfish.artifact.base.BaseFragment
import com.dthfish.artifact.bean.CardBean
import com.dthfish.artifact.bean.SelectBean
import com.dthfish.artifact.db.DBManager
import com.dthfish.artifact.detail.DetailFragment
import com.dthfish.artifact.utils.*
import com.liaoinstan.springview.container.DefaultFooter
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import kotlinx.android.synthetic.main.fragment_main.*

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
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = object : CommonAdapter<CardBean>(context, R.layout.item_card, mutableListOf()) {
            override fun convert(holder: ViewHolder?, bean: CardBean?, position: Int) {
                holder?.getView<ImageView>(R.id.iv_icon)?.let {
                    ImageLoader.loadUrl(context!!, bean?.mini_image?.default, it)
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
                adapter!!.datas[position]?.let {
                    DetailFragment.newInstance(it).show(childFragmentManager!!, "detail")

                }
            }

        })
        rv.adapter = adapter

        springView.isEnableFooter = true
        // 装作我在用分页加载的样子（滑稽）
        springView.footer = DefaultFooter(context)
        doSelect(SelectBean())// 默认

    }
    // 筛选掉了很多构建里面不能选择的但是 json 里有的数据，搜索的时候就不去除这部分数据了
    fun doSelect(selectBean: SelectBean) {
        val types = mutableListOf<String>()
        val subTypes = mutableListOf<String>()

        selectBean.types.forEachIndexed { index, b ->
            if (b) {
                types.add(CARD_TYPE_ARRAY[index])
            }
        }
        selectBean.itemTypes.forEachIndexed { index, b ->
            if (b) {
                subTypes.add(SUB_CARD_TYPE_ARRAY[index])
            }
        }
        var colorConditions = ""
        selectBean.colors.forEachIndexed { index, b ->
            if (b) {
                colorConditions += " OR"
                when (index) {
                    0 -> colorConditions += " is_red == 1"
                    1 -> colorConditions += " is_green == 1"
                    2 -> colorConditions += " is_blue == 1"
                    3 -> colorConditions += " is_black == 1"
                }
            }
        }
        if (colorConditions.isNotEmpty()) {
            colorConditions = colorConditions.replaceFirst(" OR", "")
        }

        var rarityConditions = ""
        selectBean.rarities.forEachIndexed { index, b ->
            if (b) {
                rarityConditions += " OR"
                when (index) {
                    0 -> rarityConditions += " rarity IS NULL"
                    1 -> rarityConditions += " rarity == '${RarityType.COMMON}'"
                    2 -> rarityConditions += " rarity == '${RarityType.UNCOMMON}'"
                    3 -> rarityConditions += " rarity == '${RarityType.RARE}'"
                }
            }
        }

        if (rarityConditions.isNotEmpty()) {
            rarityConditions = rarityConditions.replaceFirst(" OR", "")
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
        var hasCreep = false
        // Spell Improvement Creep类型中要去掉 item_def 为空的条目
        if ((types.isEmpty() && ignoreSub) || types.size == 5) {
            conditions =
                    "(card_type IN ('${CardType.HERO}','${CardType.ITEM}'))"
            hasSpell = true
            hasImprovement = true
            hasCreep = true
        } else if (!types.isEmpty()) {
            types.forEach {
                when (it) {
                    CardType.SPELL -> hasSpell = true
                    CardType.IMPROVEMENT -> hasImprovement = true
                    CardType.CREEP -> hasCreep = true
                    else -> conditions += if (conditions.isNotEmpty()) "'$it'," else "(card_type IN ('$it',"
                }
            }
            if (conditions.isNotEmpty()) {
                conditions = conditions.substring(0, conditions.length - 1)
                conditions += "))"
            }
        }

        if (hasSpell) {
            if (conditions.isNotEmpty()) {
                conditions += " OR "
            }
            conditions += "(card_type == '${CardType.SPELL}' AND item_def IS NOT NULL)"
        }
        if (hasImprovement) {
            if (conditions.isNotEmpty()) {
                conditions += " OR "
            }
            conditions += "(card_type == '${CardType.IMPROVEMENT}' AND item_def IS NOT NULL)"
        }
        if (hasCreep) {
            if (conditions.isNotEmpty()) {
                conditions += " OR "
            }
            conditions += "(card_type == '${CardType.CREEP}' AND item_def IS NOT NULL)"
        }

        if (!ignoreSub) {
            if (conditions.isNotEmpty()) {
                conditions += " OR "
            }
            conditions += "(sub_type IN ("
            subTypes.forEach {
                conditions += "'$it',"
            }
            conditions = conditions.substring(0, conditions.length - 1)
            conditions += "))"
        }
        conditions = "($conditions)"
        if (colorConditions.isNotEmpty()) {
            conditions += " AND ($colorConditions)"
        }

        if (rarityConditions.isNotEmpty()) {
            conditions += " AND ($rarityConditions)"

        }

        DBManager.instance.queryCardByCondition(conditions) { cardList ->

            cardList.asSequence()
                //治疗药膏,泉水烧瓶,知识魔药,回城卷轴 用 sql 比较难去掉所以这里用 filter
                .filter { (SubCardType.CONSUMABLE == it.sub_type && it.rarity.isNullOrEmpty()).not() }
                .map { it.convent2CardBean() }.toList().let { list ->
                    adapter?.datas?.let {
                        it.clear()
                        it.addAll(list)
                        adapter?.notifyDataSetChanged()
                        rv.scrollToPosition(0)
                    }

                }
        }


    }
}