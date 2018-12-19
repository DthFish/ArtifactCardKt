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
import com.dthfish.artifact.bean.Card
import com.dthfish.artifact.bean.CardBean
import com.dthfish.artifact.bean.SearchBean
import com.dthfish.artifact.db.DBManager
import com.dthfish.artifact.detail.DetailFragment
import com.dthfish.artifact.utils.*
import com.liaoinstan.springview.container.DefaultFooter
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import kotlinx.android.synthetic.main.fragment_main.*
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
        val types = mutableListOf<String>()
        val subTypes = mutableListOf<String>()

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
        var colorConditions = ""
        searchBean.colors.forEachIndexed { index, b ->
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
        searchBean.rarities.forEachIndexed { index, b ->
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

        } else if (!types.isEmpty()) {
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
            cardList.map { it.convent2CardBean() }.let { list ->
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