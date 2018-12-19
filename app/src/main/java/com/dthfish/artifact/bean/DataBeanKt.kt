package com.dthfish.artifact.bean

import com.dthfish.artifact.common.b2o
import com.dthfish.artifact.common.o2b
import com.squareup.moshi.JsonClass
import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import java.io.Serializable

/**
 * Description
 * Author DthFish
 * Date  2018/12/6.
 */
@JsonClass(generateAdapter = true)
data class CardSetData(var card_set: CardSetBean?)

@JsonClass(generateAdapter = true)
data class CardSetBean(var version: Int, var set_info: SetInfo?, var card_list: List<CardBean>?)

@JsonClass(generateAdapter = true)
data class SetInfo(var set_id: Int, var pack_item_def: Int, var name: LanguageBean?)

@JsonClass(generateAdapter = true)
data class LanguageBean(var english: String?, var schinese: String?, var tchinese: String?) : Serializable

@JsonClass(generateAdapter = true)
data class ImageBean(var default: String?, var english: String?, var schinese: String?, var tchinese: String?) :
    Serializable

@JsonClass(generateAdapter = true)
data class ReferencesBean(var card_id: Int, var ref_type: String?, var count: Int?) : Serializable

@JsonClass(generateAdapter = true)
data class CardBean(
    var card_id: Int,
    var base_card_id: Int,
    var card_type: String?,
    var card_name: LanguageBean?,
    var card_text: LanguageBean?,
    var mini_image: ImageBean?,
    var large_image: ImageBean?,
    var ingame_image: ImageBean?,
    var is_black: Boolean = false,
    var is_red: Boolean = false,
    var is_green: Boolean = false,
    var is_blue: Boolean = false,
    var attack: Int?,
    var hit_points: Int?,
    var armor: Int?,
    var mana_cost: Int?,
    var sub_type: String?,
    var gold_cost: Int?,
    var rarity: String?,
    var item_def: Int?,
    var references: List<ReferencesBean>?
):Serializable {
    fun convertToCard(): Card {

        return Card(
            card_id,
            base_card_id,
            card_type,
            card_name?.english,
            card_name?.schinese,
            card_name?.tchinese,
            card_text.o2b(),
            mini_image.o2b(),
            large_image.o2b(),
            ingame_image.o2b(),
            is_black,
            is_red,
            is_green,
            is_blue,
            attack,
            hit_points,
            armor,
            mana_cost,
            sub_type,
            gold_cost,
            rarity,
            item_def,
            if (references.isNullOrEmpty()) null else references.o2b()
        )
    }
}

class Card(
    var card_id: Int,
    var base_card_id: Int,
    var card_type: String?,//Creep,Hero,Item,Spell,Improvement 强化,Ability,Passive Ability 被动,Pathing （线路 删除）,Stronghold（删除
    var card_name_english: String?,
    var card_name_schinese: String?,
    var card_name_tchinese: String?,
    var card_text: ByteArray?,
    var mini_image: ByteArray?,
    var large_image: ByteArray?,
    var ingame_image: ByteArray?,
    var is_black: Boolean,
    var is_red: Boolean,
    var is_green: Boolean,
    var is_blue: Boolean,
    var attack: Int?,
    var hit_points: Int?,
    var armor: Int?,
    var mana_cost: Int?,
    var sub_type: String?,//物品有的属性 Weapon,Armor,Accessory,Deed,Consumable
    var gold_cost: Int?,
    var rarity: String?,//null 自带卡等,Common,Uncommon,Rare
    var item_def: Int?,
    var references: ByteArray?
) : LitePalSupport(), Serializable {
    @Column(unique = true)
    var id: Int? = 0

    fun convent2CardBean(): CardBean {
        return CardBean(
            this.card_id,
            this.base_card_id,
            this.card_type,
            LanguageBean(
                this.card_name_english,
                this.card_name_schinese,
                this.card_name_tchinese
            ),
            this.card_text.b2o(),
            this.mini_image.b2o(),
            this.large_image.b2o(),
            this.ingame_image.b2o(),
            this.is_black,
            this.is_red,
            this.is_green,
            this.is_blue,
            this.attack,
            this.hit_points,
            this.armor,
            this.mana_cost,
            this.sub_type,
            this.gold_cost,
            this.rarity,
            this.item_def,
            this.references.b2o()
        )
    }
}

class SearchBean {
    // 一位代表一个类型，和 ui 位置对应
    //依次 英雄，怪，术，强，
    var types: BooleanArray = booleanArrayOf(false, false, false, false)
    //兵，甲，命，回，deed
    var itemTypes: BooleanArray = booleanArrayOf(false, false, false, false, false)
    //依次 红，绿，蓝，黑
    var colors: BooleanArray = booleanArrayOf(false, false, false, false)
    //依次 铁，铜，银，金 对应 null,common,uncommon,rare
    var rarities: BooleanArray = booleanArrayOf(false, false, false, false)
    //颜色 没有就是全部，对物品不影响
    //稀有度 对所有影响
}