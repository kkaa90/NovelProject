package com.e.myapplication.user

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.e.myapplication.LoginInfo
import com.e.myapplication.dataclass.ChkLogin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.loginDataStore: DataStore<LoginInfo> by dataStore(
    fileName = "loginInfo.pb",
    serializer = LoginSetting
)

class LoginRepository(context: Context) {
    private val loginDataStore = context.loginDataStore

    suspend fun readLoginInfo(): LoginInfo {
        return loginDataStore.data.map { it }.first()
    }

    suspend fun writeLoginInfo(chkLogin: ChkLogin) {
        loginDataStore.updateData { login ->
            login.toBuilder()
                .setChkIdSave(chkLogin.chkIdSave)
                .setChkAccSave(chkLogin.chkAutoLogin)
                .setId(chkLogin.id)
                .setPwd(chkLogin.pwd)
                .build()
        }
    }

}