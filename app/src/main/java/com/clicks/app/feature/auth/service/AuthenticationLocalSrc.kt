package com.clicks.app.feature.auth.service

import android.content.Context
import com.clicks.app.feature.auth.model.Authentication
import com.clicks.app.lib.db.AppDatabase

class AuthenticationLocalSrc(context: Context) {
    private val dao = AppDatabase.getDatabase(context).authenticationDao()

    fun insertSync(auth: Authentication) {
        auth.id = 1
        dao.insertSync(auth)
    }

    fun selectSync(): Authentication? {
        val auth = dao.selectSync()
        if ((auth == null) || auth.hasExpired()) {
            if (auth != null) {
                dao.delete()
            }
            return null
        }
        return auth
    }

    fun getToken(): String? = selectSync()?.authToken

    fun delete() {
        dao.delete()
    }

    fun exists(): Boolean = selectSync() != null
}
