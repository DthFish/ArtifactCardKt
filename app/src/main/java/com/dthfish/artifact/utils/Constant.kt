package com.dthfish.artifact.utils

/**
 * Description
 * Author DthFish
 * Date  2018/12/13.
 */
object CardType {
    const val HERO = "Hero"
    const val ITEM = "Item"
    const val CREEP = "Creep"
    const val SPELL = "Spell"
    const val IMPROVEMENT = "Improvement"
    const val ABILITY = "Ability"
    const val PASSIVE_ABILITY = "Passive Ability"
}

val CARD_TYPE_ARRAY = listOf(CardType.HERO, CardType.CREEP, CardType.SPELL, CardType.IMPROVEMENT)

object SubCardType {
    const val WEAPON = "Weapon"
    const val ARMOR = "Armor"
    const val ACCESSORY = "Accessory"
    const val CONSUMABLE = "Consumable"
    const val DEED = "Deed"
}

val SUB_CARD_TYPE_ARRAY =
    listOf(SubCardType.WEAPON, SubCardType.ARMOR, SubCardType.ACCESSORY, SubCardType.CONSUMABLE, SubCardType.DEED)

object RarityType {
    const val COMMON = "Common"
    const val UNCOMMON = "Uncommon"
    const val RARE = "Rare"
}

val RARITY_TYPE_ARRAY = listOf("NULL", RarityType.COMMON, RarityType.UNCOMMON, RarityType.RARE)

object RefType {
    const val INCLUDES = "includes"
    const val PASSIVE_ABILITY = "passive_ability"
    const val ACTIVE_ABILITY = "active_ability"
}