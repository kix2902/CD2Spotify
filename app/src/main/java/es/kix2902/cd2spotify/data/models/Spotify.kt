package es.kix2902.cd2spotify.data.models

class Spotify {
    data class Album(
        val id: String,
        val artist: List<Artist>,
        val title: String?,
        val image: String?,
        val favorite: Boolean = false,
        var tracks: List<Track> = arrayListOf()
    )

    data class Artist(
        val id: String,
        val name: String
    )

    data class Track(
        val id: String,
        val title: String?,
        val discNumber: Int,
        val trackNumber: Int,
        val favorite: Boolean = false
    )
}