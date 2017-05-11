package wallethpush

import okhttp3.*
import java.io.IOException

val JSONMediaType = MediaType.parse("application/json")!!

fun buildBlockRequest() = buildRequest(RequestBody.create(JSONMediaType, "{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":1}"))

fun buildBlockByNumberRequest(number: String)
        = buildRequest(RequestBody.create(JSONMediaType, "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBlockByNumber\",\"params\":[\"$number\", true],\"id\":1}}"))

fun buildRequest(body: RequestBody) = Request.Builder().url("http://192.168.5.42:9003")
        .method("POST", body)
        .build()!!

fun main(args: Array<String>) {

    var lastBlock = "0x0"

    while (true) {
        okhttp.newCall(buildBlockRequest()).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response) {
                val newBlock = blockNumberAdapter.fromJson(response.body().source()).result
                if (newBlock != lastBlock) {
                    lastBlock = newBlock
                    println("New Block" + newBlock)
                    processBlockNumber(newBlock)
                }
            }

            override fun onFailure(call: Call?, e: IOException) {
                println("problem getting block number " + e.message)
            }

        })

        Thread.sleep(1000)
    }

}

fun processBlockNumber(newBlock: String) {
    okhttp.newCall(buildBlockByNumberRequest(newBlock)).enqueue(object : Callback {
        override fun onFailure(call: Call?, e: IOException) {
            println("problem getting block information " + e.message)
        }

        override fun onResponse(call: Call?, response: Response) {
            val gasLimit = blockInfoAdapter.fromJson(response.body().source()).result.transactions
            gasLimit.forEach {
                println(it.from + " > " + it.to)
            }
        }

    })
}

