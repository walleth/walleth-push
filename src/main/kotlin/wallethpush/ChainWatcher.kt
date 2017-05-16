package wallethpush

import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody

val JSONMediaType = MediaType.parse("application/json")!!

fun buildBlockRequest() = buildRequest(RequestBody.create(JSONMediaType, "{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":1}"))

fun buildBlockByNumberRequest(number: String)
        = buildRequest(RequestBody.create(JSONMediaType, "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBlockByNumber\",\"params\":[\"$number\", true],\"id\":1}}"))

fun buildRequest(body: RequestBody) = Request.Builder().url("http://192.168.5.42:9003")
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
    }
}

