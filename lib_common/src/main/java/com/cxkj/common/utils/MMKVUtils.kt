package com.cxkj.common.utils

import android.os.Parcelable
import com.tencent.mmkv.MMKV



class MMKVUtils private constructor() {

    companion object {
        val instance : MMKVUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MMKVUtils()
        }
    }

    //字符串
    fun encodeString(key : String , value : String?) {
        MMKV.defaultMMKV().encode(key , value)
    }

    fun decodeString(key : String) : String? {
        return MMKV.defaultMMKV().decodeString(key)
    }

    fun decodeString(key : String , defaultValue : String?) : String? {
        return MMKV.defaultMMKV().decodeString(key , defaultValue)
    }

    //int型
    fun encodeInt(key : String , value : Int) {
        MMKV.defaultMMKV().encode(key , value)
    }

    fun decodeInt(key : String) : Int {
        return MMKV.defaultMMKV().decodeInt(key)
    }

    fun decodeInt(key : String , defaultValue : Int) : Int {
        return MMKV.defaultMMKV().decodeInt(key , defaultValue)
    }

    //Long
    fun encodeLong(key : String , value : Long) {
        MMKV.defaultMMKV().encode(key , value)
    }

    fun decodeLong(key : String) : Long {
        return MMKV.defaultMMKV().decodeLong(key)
    }

    fun decodeLong(key : String , defaultValue : Long) : Long {
        return MMKV.defaultMMKV().decodeLong(key , defaultValue)
    }

    //Float
    fun encodeFloat(key : String , value : Float) {
        MMKV.defaultMMKV().encode(key , value)
    }

    fun decodeFloat(key : String) : Float {
        return MMKV.defaultMMKV().decodeFloat(key)
    }

    fun decodeFloat(key : String , defaultValue : Float) : Float {
        return MMKV.defaultMMKV().decodeFloat(key , defaultValue)
    }

    //Double
    fun encodeDouble(key : String , value : Double) {
        MMKV.defaultMMKV().encode(key , value)
    }

    fun decodeDouble(key : String) : Double {
        return MMKV.defaultMMKV().decodeDouble(key)
    }

    fun decodeDouble(key : String , defaultValue : Double) : Double {
        return MMKV.defaultMMKV().decodeDouble(key , defaultValue)
    }

    //Double
    fun encodeBoolean(key : String , value : Boolean) {
        MMKV.defaultMMKV().encode(key , value)
    }

    fun decodeBoolean(key : String) : Boolean {
        return MMKV.defaultMMKV().decodeBool(key)
    }

    fun decodeBoolean(key : String , defaultValue : Boolean) : Boolean {
        return MMKV.defaultMMKV().decodeBool(key , defaultValue)
    }

    //Parcelable

    fun encodeParcelable(key : String , value : Parcelable) {
        MMKV.defaultMMKV().encode(key , value)
    }

    fun <T : Parcelable?> decodeParcelable(key : String , tClass : Class<T>?) : T? {
        return MMKV.defaultMMKV().decodeParcelable(key , tClass)
    }

    fun <T : Parcelable?> decodeParcelable(
        key : String , tClass : Class<T>? ,
        defaultValue : T ,
    ) : T? {
        return MMKV.defaultMMKV().decodeParcelable(key , tClass , defaultValue)
    }
}