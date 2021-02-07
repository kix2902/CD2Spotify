package es.kix2902.cd2spotify.data.models

class Spotify {
    data class Album(
        val id: String,
        val artist: String?,
        val title: String?,
        val image: String?,
        var tracks: List<Track> = arrayListOf()
    )

    data class Track(
        val id: String,
        val title: String?,
        val discNumber: Int,
        val trackNumber: Int
    )
}