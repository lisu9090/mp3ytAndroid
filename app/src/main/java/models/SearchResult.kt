package models

import java.util.*

class SearchResult(val itemId: String, val name: String){
    var fileModData: Date? = null

    constructor(itemId: String, name: String, modData: Date) : this(itemId, name) {
        fileModData = modData
    }

    val mImgSrc = "https://i.ytimg.com/vi/$itemId/default.jpg"
}