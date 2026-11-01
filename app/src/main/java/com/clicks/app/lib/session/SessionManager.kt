package com.clicks.app.lib.session

import com.clicks.app.feature.auth.service.AuthenticationLocalSrc
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SessionManager {

    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent: SharedFlow<Unit> = _sessionExpiredEvent.asSharedFlow()

    fun triggerSessionExpired(authLocalSrc: AuthenticationLocalSrc? = null) {
        authLocalSrc?.delete()
        _sessionExpiredEvent.tryEmit(Unit)
    }
}
