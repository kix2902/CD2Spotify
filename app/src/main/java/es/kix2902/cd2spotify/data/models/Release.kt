package es.kix2902.cd2spotify.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "releases")
data class Release(
    @PrimaryKey val barcode: String,
    val title: String = "",
    val author: String = ""
)

fun Release.hasInfo(): Boolean {
    return title.isNotEmpty() && author.isNotEmpty()
}