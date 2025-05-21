package com.example.syncmycontacts.data.model

data class Contact(
    val name: String? = "",
    val phone: String? = ""
){
    fun doesMatchSearchQuery(query:String):Boolean{
        val matchCombinations = listOf(
            "$name",
            "$phone",
            "${name?.lowercase()}",
            "${name?.uppercase()}"
        )
        return matchCombinations.any{
            it.contains(query)
        }


    }
}