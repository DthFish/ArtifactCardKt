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
import com.dthfish.artifact.db.DBManager
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
}