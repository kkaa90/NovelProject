package com.e.myapplication.user

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStore
import com.e.myapplication.AccountInfo
import com.e.myapplication.dataclass.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

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
                .setMemId(user.mem_id)
                .setMemUserid(user.mem_userid)
                .build()
        }
    }
}
