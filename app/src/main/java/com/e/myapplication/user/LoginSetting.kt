package com.e.myapplication.user

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.e.myapplication.LoginInfo
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object LoginSetting : Serializer<LoginInfo> {
    override val defaultValue: LoginInfo
        get() = LoginInfo.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): LoginInfo {
        try {
            return LoginInfo.parseFrom(input)
        } catch (e : InvalidProtocolBufferException){
            throw CorruptionException("확인 불가", e)
        }
    }

    override suspend fun writeTo(t: LoginInfo, output: OutputStream) =t.writeTo(output)

}