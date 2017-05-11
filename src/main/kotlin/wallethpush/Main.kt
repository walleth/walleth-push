package wallethpush

import okhttp3.*
import java.io.IOException

val blockRequest = Request.Builder().url("http://192.168.5.42:9003")
        .method("POST", RequestBody.create(MediaType.parse("application/json"), "{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":83}"))
        .build()!!

fun getBlockByNumberRequest(number: String) = Request.Builder().url("http://192.168.5.42:9003")
        .method("POST", RequestBody.create(MediaType.parse("application/json"), "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBlockByNumber\",\"params\":[\"$number\", true],\"id\":1}}"))
        .build()!!

fun main(args: Array<String>) {

    var lastBlock = "0x0"

    while (true) {
        okhttp.newCall(blockRequest).enqueue(object : Callback {
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
    okhttp.newCall(getBlockByNumberRequest(newBlock)).enqueue(object : Callback {
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

