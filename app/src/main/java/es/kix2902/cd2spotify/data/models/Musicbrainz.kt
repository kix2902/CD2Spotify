package es.kix2902.cd2spotify.data.models

import androidx.room.*

class Musicbrainz {
    @Entity
    data class Release(
        @PrimaryKey val barcode: String,
        var title: String? = null
    )

    @Entity
    data class Artist(
        @PrimaryKey val id: String,
        val name: String
    )

    @Entity(primaryKeys = ["releaseBarcode", "artistId"])
    data class ReleaseArtist(
        val releaseBarcode: String,
        val artistId: String
    )

    data class ReleaseWithArtists(
        @Embedded
        val release: Release,

        @Relation(
            parentColumn = "barcode",
            entity = Artist::class,
            entityColumn = "id",
            associateBy = Junction(
                value = ReleaseArtist::class,
                parentColumn = "releaseBarcode",
                entityColumn = "artistId"
            )
        )
        val artists: List<Artist>
    )
}
