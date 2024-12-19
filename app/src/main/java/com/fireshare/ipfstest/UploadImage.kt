package com.fireshare.ipfstest

import android.util.Log
import com.google.gson.Gson
import hprose.client.HproseClient
import java.util.Random

fun uploadToIPFS(): String? {
    val baseUrl = "http://192.168.5.4:8080"
    val appId = "heWgeGkeBX2gaENbIBS_Iy1mdTS"

    val client = HproseClient.create(baseUrl).useService(HproseService::class.java)
    val buffer = ByteArray(32).apply {
        Random().nextBytes(this)
    }
    val json = """
           {"aid": $appId, "ver": "last", "offset": 0}
            """.trimIndent()
    val request = Gson().fromJson(json, Map::class.java)
    try {
        val fsid = client.runMApp<String?>("upload_ipfs", request, listOf(buffer))
        val cid = fsid?.let { client.mfTemp2Ipfs(it, null) }
        return cid
    } catch (e: Exception) {
        Log.e("uploadToIPFS()", "Error: $request $e")
        e.printStackTrace()
        return null
    }
}

typealias MimeiId = String      // 27 or 64 character long string

interface HproseService {
    fun<T> runMApp(entry: String, request: Map<*, *>, args: List<ByteArray?> = emptyList()): T?
    fun mfTemp2Ipfs(fsid: String, ref: MimeiId? = null): MimeiId
}