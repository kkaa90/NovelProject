package com.e.treenovel.user

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.e.treenovel.AccountInfo
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream


object ProtoSetting : Serializer<AccountInfo>{
    override val defaultValue: AccountInfo = AccountInfo.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AccountInfo {
        try {
            return AccountInfo.parseFrom(input)
        } catch (exception : InvalidProtocolBufferException){
            throw CorruptionException("확인 불가",exception)
        }
    }

    override suspend fun writeTo(t: AccountInfo, output: OutputStream) =t.writeTo(output)
}

