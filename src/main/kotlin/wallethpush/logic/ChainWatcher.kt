package wallethpush.logic

import kontinuum.ConfigProvider
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import wallethpush.*
import wallethpush.model.PushMessage
import wallethpush.model.PushMessageData

val JSONMediaType: MediaType = MediaType.parse("application/json")

fun buildBlockRequest() = buildRequest(RequestBody.create(JSONMediaType, "{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":1}"))

fun buildBlockByNumberRequest(number: String)
        = buildRequest(RequestBody.create(JSONMediaType, "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBlockByNumber\",\"params\":[\"$number\", true],\"id\":1}}"))

fun buildRequest(body: RequestBody) = Request.Builder().url(ConfigProvider.config.eth_rpc_url)
        .method("POST", body)
        .build()!!

fun watchChain() {

    var lastBlock = "0x0"

    while (true) {
        try {
            Thread.sleep(1000)

            val newBlock = okhttp.newCall(buildBlockRequest()).execute().body().use { blockNumberAdapter.fromJson(it.source()) }.result

            if (newBlock != lastBlock) {
                lastBlock = newBlock
                println("New Block" + newBlock)
                processBlockNumber(newBlock)
            }

        } catch (e: Exception) {
            println("problem at block $lastBlock " + e.message)
        }
    }

}

fun processBlockNumber(newBlock: String) {
    val response = okhttp.newCall(buildBlockByNumberRequest(newBlock)).execute().body()
    response.use { blockInfoAdapter.fromJson(it.source()) }.result.transactions.forEach {
        println(it.from + " > " + it.to)
        val tokensForFrom = pushMappingStore.getTokensForAddress(it.from)
        if (tokensForFrom.isNotEmpty()) {
            notifyTokens(tokensForFrom, it.from)
        } else {
            if (it.to != null) {
                val tokensForTo = pushMappingStore.getTokensForAddress(it.to)
                if (tokensForTo.isNotEmpty()) {
                    notifyTokens(tokensForTo, it.to)
                }
            }
        }
    }
}

private fun notifyTokens(tokens: List<String>, address: String) {

    tokens.forEach { it ->
        val json = pushMessageAdapter.toJson(PushMessage(to = it, data = PushMessageData(address)))
        val request = Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .header("Authorization", "key=" + ConfigProvider.config.fcm_api_key)
                .post(RequestBody.create(JSONMediaType, json))
                .build()
        val resultString = okhttp.newCall(request).execute().body().use { it.string() }
        println("send notification " + resultString)
    }
}
