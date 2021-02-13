package es.kix2902.cd2spotify.helpers

import androidx.recyclerview.widget.RecyclerView
import es.kix2902.cd2spotify.data.models.Musicbrainz

fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(adapterPosition, itemViewType)
    }
    return this
}

fun Musicbrainz.ReleaseWithArtists.hasInfo(): Boolean {
    return !release.title.isNullOrEmpty() || artists.isNotEmpty()
}