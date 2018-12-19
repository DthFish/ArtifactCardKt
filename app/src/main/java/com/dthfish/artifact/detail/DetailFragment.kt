package com.dthfish.artifact.detail

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Html
import android.view.*
import com.dthfish.artifact.R
import com.dthfish.artifact.bean.CardBean
import com.dthfish.artifact.utils.CardType
import com.dthfish.artifact.utils.ImageLoader
import kotlinx.android.synthetic.main.fragment_detail.*

/**
 * Description
 * Author DthFish
 * Date  2018/12/18.
 */
class DetailFragment : DialogFragment() {
    private lateinit var cardBean: CardBean

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
        cardBean.large_image?.let {
            if (!it.schinese.isNullOrEmpty()) {
                ImageLoader.loadUrl(context!!, it.schinese, iv)
            } else if (!it.default.isNullOrEmpty()) {
                ImageLoader.loadUrl(context!!, it.default, iv)
            }
        }
        if (CardType.HERO == cardBean.card_type) {
            cardBean.card_text.let {
                if (it == null || it.schinese.isNullOrEmpty()) {
                    tvDesc.visibility = View.GONE
                } else {
                    tvDesc.visibility = View.VISIBLE
                    tvDesc.text = Html.fromHtml(it.schinese)
                }

            }
        }
    }
}