package com.e.myapplication.user

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.e.myapplication.AccountInfo
import com.e.myapplication.dataclass.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.accountDataStore: DataStore<AccountInfo> by dataStore(
    fileName = "account.pb",
    serializer = ProtoSetting
)

class ProtoRepository(context: Context) {
    private val accountDataStore = context.accountDataStore

    suspend fun readAccountInfo(): AccountInfo {
        return accountDataStore.data.map { it }.first()

    }
    suspend fun writeAccountInfo(user: User){
        accountDataStore.updateData { account ->
            account.toBuilder()
                .setAuthorization(user.authorization)
                .setMemIcon(user.memIcon)
                .setMemNick(user.memNick)
                .setMemId(user.memId)
                .setMemUserid(user.memUserId)
                .setRefreshToken(user.refreshToken)
                .setEmail(user.email)
                .build()
        }
    }
}
