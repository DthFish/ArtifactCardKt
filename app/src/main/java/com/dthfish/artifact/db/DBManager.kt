package com.dthfish.artifact.db

import android.content.Context
import com.dthfish.artifact.bean.Card
import com.dthfish.artifact.bean.CardSetData
import com.dthfish.artifact.common.log
import com.dthfish.artifact.utils.CardType
import com.dthfish.artifact.utils.JsonUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.litepal.LitePal
import org.litepal.extension.deleteAll
import org.litepal.extension.findAsync
import org.litepal.extension.findFirst
import org.litepal.tablemanager.callback.DatabaseListener
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Description
 * Author DthFish
 * Date  2018/12/13.
 */
class DBManager private constructor() {

    private lateinit var context: Context

    companion object {
        val instance: DBManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DBManager()
        }
    }

    fun init(context: Context) {
        this.context = context
        LitePal.initialize(context)
        LitePal.registerDatabaseListener(object : DatabaseListener {
            override fun onUpgrade(oldVersion: Int, newVersion: Int) {
                "LitePal upgrade oldVersion:$oldVersion,newVersion:$newVersion".log("LitePal")
            }

            override fun onCreate() {
                "LitePal create database".log("LitePal")
            }
        })
    }

    fun checkAndCreateCardDB(onStart: () -> Unit, onFinish: () -> Unit) {

        val find = LitePal.findFirst<Card>()
        if (find == null) {
            Observable.just("card_set_1.json", "card_set_2.json")
                .flatMap { s ->
                    Observable.create<Int> { emitter ->
                        parseJsonAndSave(s)
                        emitter.onNext(0)
                        emitter.onComplete()
                    }
                }.doOnSubscribe {
                    LitePal.deleteAll<Card>()

                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Int> {

                    override fun onSubscribe(d: Disposable) {
                        onStart()
                    }

                    override fun onNext(t: Int) {

                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onComplete() {
                        onFinish()
                    }

                })


        } else {
            onFinish()
        }

    }

    private fun parseJsonAndSave(path: String) {
        val jsonStr = StringBuilder()
        var line: String?
        val open = this.context.resources.assets.open(path)
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(InputStreamReader(open))
            line = reader.readLine()
            while (!line.isNullOrEmpty()) {
                jsonStr.append(line)
                line = reader.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            reader?.close()
            open?.close()
        }

        val bean = JsonUtils.parseJson<CardSetData>(jsonStr.toString())

        bean?.card_set?.card_list?.let { list ->
            val map = list.map { it.convertToCard() }
            LitePal.saveAll(map)
            "Path:$path,success!".log("LitePal")
        }
    }

    fun queryAllCard(listener: (MutableList<Card>) -> Unit) {
        LitePal.where("card_type IN ('${CardType.HERO}','${CardType.ITEM}','${CardType.SPELL}','${CardType.IMPROVEMENT}','${CardType.CREEP}')")
//        LitePal.where("card_type IN ('${CardType.ABILITY}','${CardType.PASSIVE_ABILITY}')")
            .order("card_type")
            .findAsync<Card>()
            .listen { it ->
                listener(it)
            }
    }

    fun queryCardByCondition(condition: String, listener: (MutableList<Card>) -> Unit) {
        LitePal.where(condition)
            .order("card_type")
            .findAsync<Card>()
            .listen { it ->
                listener(it)
            }
    }


}