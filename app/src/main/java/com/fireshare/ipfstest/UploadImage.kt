package com.fireshare.ipfstest

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import hprose.client.HproseClient

fun uploadToIPFS(context: Context, uri: Uri): String? {
//    withContext(Dispatchers.IO) { // Execute in IO dispatcher
    val baseUrl = "http://192.168.5.4:8080"
    val appId = "heWgeGkeBX2gaENbIBS_Iy1mdTS"

    val client =
        HproseClient.create(baseUrl).useService(HproseService::class.java)
    var offset = 0L
    var byteRead: Int
    val buffer = ByteArray(521 * 1024)
    val json = """
           {"aid": $appId, "ver": "last", "offset": $offset}
            """.trimIndent()
    val request = Gson().fromJson(json, Map::class.java).toMutableMap()
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        inputStream.use { stream ->
            while (stream.read(buffer).also { byteRead = it } != -1) {
                try {
                    request["fsid"] = client.runMApp(
                        "upload_ipfs",
                        request.toMap(), listOf(buffer)
                    )
                    offset += byteRead
                    request["offset"] = offset
                } catch (e: Exception) {
                    Log.e("uploadToIPFS()", "Error: $request $byteRead $e")
                    e.printStackTrace()
                    return null
                }
            }
        }
    }
    // Do not know the tweet mid yet, cannot add reference as 2nd argument.
    // Do it later when uploading tweet.
    val cid = request["fsid"]?.let { client.mfTemp2Ipfs(it.toString(), null) }
    return cid
}

typealias MimeiId = String      // 27 or 64 character long string

interface ScorePair {
    val score: Long
    val member: String
}

interface HproseService {
    fun<T> runMApp(entry: String, request: Map<*, *>, args: List<ByteArray?> = emptyList()): T?
    fun getVarByContext(sid: String, context: String, mapOpt: Map<String, String>? = null): String
    fun login(ppt: String): Map<String, String>
    fun getVar(sid: String, name: String, arg1: String? = null, arg2: String? = null): String
    fun mmCreate(
        sid: String,
        appId: String,
        ext: String,
        mark: String,
        tp: Byte,
        right: Long
    ): MimeiId

    fun mmOpen(sid: String, mid: MimeiId, version: String): String
    fun mmBackup(
        sid: String,
        mid: MimeiId,
        memo: String = "",
        ref: String = ""
    ) // Add default value for 'ref'

    fun mmAddRef(sid: String, mid: MimeiId, mimeiId: MimeiId)
    fun mmSetObject(fsid: String, obj: Any)
    fun mimeiPublish(sid: String, memo: String, mid: MimeiId)
    fun mfOpenTempFile(sid: String): String
    fun mfTemp2Ipfs(fsid: String, ref: MimeiId? = null): MimeiId
    fun mfSetCid(sid: String, mid: MimeiId, cid: MimeiId)
    fun mfSetData(fsid: String, data: ByteArray, offset: Long)
    fun set(sid: String, key: String, value: Any)
    fun get(sid: String, key: String): Any?
    fun hGet(sid: String, key: String, field: String): Any?
    fun hSet(sid: String, key: String, field: String, value: Any)
    fun hDel(sid: String, key: String, field: String)
    fun zAdd(sid: String, key: String, sp: ScorePair)
    fun zRevRange(sid: String, key: String, start: Long, end: Long): List<*>
}