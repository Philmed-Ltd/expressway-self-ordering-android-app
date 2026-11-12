package com.clicks.app.lib.session

import com.clicks.app.feature.auth.service.AuthenticationLocalSrc
import java.util.Date
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.time.Duration.Companion.milliseconds

object SessionManager {

    private val sessionScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val sessionExpiredChannel = Channel<Unit>(Channel.CONFLATED)
    val sessionExpiredEvent = sessionExpiredChannel.receiveAsFlow()
    private var expiryJob: Job? = null
    private var scheduledExpiryTime: Date? = null

    @Synchronized
    fun scheduleSessionExpiry(
        expiryTime: Date,
        authLocalSrc: AuthenticationLocalSrc? = null
    ) {
        cancelExpiryJob()
        scheduledExpiryTime = expiryTime
        expiryJob = sessionScope.launch {
            val remainingTime = (expiryTime.time - System.currentTimeMillis()).coerceAtLeast(0L)
            delay(remainingTime.milliseconds)
            triggerSessionExpired(authLocalSrc)
        }
    }

    @Synchronized
    fun checkSessionExpiryNow(authLocalSrc: AuthenticationLocalSrc) {
        val storedAuthentication = authLocalSrc.getStoredAuthentication() ?: return
        if (storedAuthentication.hasExpired()) {
            triggerSessionExpired(authLocalSrc)
        } else if (scheduledExpiryTime?.time != storedAuthentication.expiryTime.time) {
            scheduleSessionExpiry(storedAuthentication.expiryTime, authLocalSrc)
        }
    }

    @Synchronized
    fun cancelSessionExpiry() {
        cancelExpiryJob()
        scheduledExpiryTime = null
    }

    @Synchronized
    fun triggerSessionExpired(authLocalSrc: AuthenticationLocalSrc? = null) {
        cancelExpiryJob()
        scheduledExpiryTime = null
        authLocalSrc?.delete()
        sessionExpiredChannel.trySend(Unit)
    }

    private fun cancelExpiryJob() {
        expiryJob?.cancel()
        expiryJob = null
    }
}
