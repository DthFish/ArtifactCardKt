package com.dthfish.artifact.detail

import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.dthfish.artifact.R
import com.dthfish.artifact.bean.CardBean
import com.dthfish.artifact.db.DBManager
import com.dthfish.artifact.utils.CardType
import com.dthfish.artifact.utils.ImageLoader
import com.dthfish.artifact.utils.RefType
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import kotlinx.android.synthetic.main.fragment_detail.*
import org.jetbrains.anko.support.v4.dip

/**
 * Description
 * Author DthFish
 * Date  2018/12/18.
 */
class DetailFragment : DialogFragment() {
    private lateinit var cardBean: CardBean
    private val cardBeans = mutableListOf<CardBean>()
    private var adapter: CommonAdapter<CardBean>? = null

    companion object {
        fun newInstance(cardBean: CardBean): DetailFragment {
            val detailFragment = DetailFragment()
            val bundle = Bundle()
            bundle.putSerializable("CardBean", cardBean)
            detailFragment.arguments = bundle

            return detailFragment
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return
        val window = dialog.window ?: return
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        window.attributes = lp
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.base_dialog)

        if (arguments != null) {
            cardBean = arguments!!.getSerializable("CardBean") as CardBean
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvDetail.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        adapter = object : CommonAdapter<CardBean>(context, R.layout.item_card_detail, cardBeans) {
            override fun convert(holder: ViewHolder?, bean: CardBean?, position: Int) {
                holder?.getView<ImageView>(R.id.iv)?.let { iv ->
                    bean?.large_image?.let {
                        if (!it.schinese.isNullOrEmpty()) {
                            ImageLoader.loadUrl(context!!, it.schinese, iv)
                        } else if (!it.default.isNullOrEmpty()) {
                            ImageLoader.loadUrl(context!!, it.default, iv)
                        }
                    }
                }
                holder?.getView<TextView>(R.id.tvDesc)?.let { tvDesc ->
                    if (CardType.HERO == bean?.card_type) {
                        bean.card_text.let {
                            if (it == null || it.schinese.isNullOrEmpty()) {

                                tvDesc.visibility = View.GONE
                            } else {
                                tvDesc.visibility = View.VISIBLE
                                tvDesc.text = Html.fromHtml(it.schinese)
                            }
                        }
                    } else {
                        tvDesc.visibility = View.GONE
                    }
                }


            }
        }
        rvDetail.adapter = adapter
        rvDetail.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect?,
                view: View?,
                parent: RecyclerView?,
                state: RecyclerView.State?
            ) {
                outRect?.set(dip(5), 0, dip(5), 0)
            }
        })
        PagerSnapHelper().attachToRecyclerView(rvDetail)

        cardBeans.add(cardBean)

        cardBean.references.let { list ->
            var conditions = ""
            if (!list.isNullOrEmpty()) {
                list!!.filter { RefType.INCLUDES == it.ref_type || RefType.REFERENCES == it.ref_type }.forEach { ref ->

                    if (conditions.isEmpty()) {
                        conditions = "(" + ref.card_id

                    } else {
                        conditions += "," + ref.card_id
                    }
                }
                if (conditions.isNotEmpty()) {
                    conditions += ")"
                }
            }

            if (conditions.isNotEmpty()) {
                DBManager.instance.queryCardByCondition("card_id IN $conditions") { cards ->
                    if (!cards.isNullOrEmpty()) {
                        cards.map { it.convent2CardBean() }.let { cardBeanList ->
                            cardBeans.addAll(cardBeanList)

                        }
                    }
                    showDetail(cardBeans)
                }
            } else {
                showDetail(cardBeans)
            }
        }


    }

    private fun showDetail(cardBeans: MutableList<CardBean>) {
        adapter?.notifyDataSetChanged()

    }
}