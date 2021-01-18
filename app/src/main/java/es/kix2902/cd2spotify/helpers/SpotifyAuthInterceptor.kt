package es.kix2902.cd2spotify.helpers

import es.kix2902.cd2spotify.data.PreferencesRepository
import es.kix2902.cd2spotify.domain.SaveSpotifyAuth
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.notifyAll
import okhttp3.internal.wait

class SpotifyAuthInterceptor(
    private val preferencesRepository: PreferencesRepository,
    private val authService: AuthorizationService,
    private val saveSpotifyToken: SaveSpotifyAuth
) : Interceptor {

    private var isRefreshing = false

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        var request = chain.request()
        isRefreshing = false

        val authState = preferencesRepository.loadSpotifyAuth()!!
        val token = authState.accessToken

        val builder = request.newBuilder()
        builder.header("Authorization", "Bearer $token")
        builder.method(original.method, original.body)

        request = builder.build()
        var response = chain.proceed(request)

        if (response.code == 401) {
            synchronized(this) {
                val currentToken = authState.accessToken
                if (currentToken != null && currentToken == token) {
                    try {
                        getRefreshToken(authState, authService, saveSpotifyToken) //async
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                if (authState.accessToken != null) {
                    builder.header("Authorization", "Bearer ${authState.accessToken}")
                    request = builder.build()
                    response.close()
                    response = chain.proceed(request)
                }
            }
        }
        return response
    }

    @Synchronized
    fun getRefreshToken(
        authState: AuthState,
        authService: AuthorizationService,
        saveSpotifyToken: SaveSpotifyAuth
    ) {
        if (!isRefreshing) {
            isRefreshing = true
            authState.performActionWithFreshTokens(authService) { _, _, _ ->
                synchronized(this@SpotifyAuthInterceptor) {
                    saveSpotifyToken.invoke(authState)
                    isRefreshing = false
                    this@SpotifyAuthInterceptor.notifyAll()
                }
            }
        }
        this.wait()
    }
}