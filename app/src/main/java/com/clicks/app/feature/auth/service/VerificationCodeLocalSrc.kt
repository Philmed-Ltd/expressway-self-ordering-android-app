package com.clicks.app.feature.auth.service

import android.content.Context
import com.clicks.app.feature.auth.model.VerificationCode
import com.clicks.app.lib.db.AppDatabase

class VerificationCodeLocalSrc(context: Context) {
    private val dao = AppDatabase.getDatabase(context).verificationCodeDao()

    fun insertSync(code: VerificationCode) = dao.insertSync(code)
    fun selectSync(): VerificationCode? = dao.selectSync()
    fun delete() = dao.delete()
}
