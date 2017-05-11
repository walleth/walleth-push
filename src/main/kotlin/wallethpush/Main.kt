package wallethpush

import com.squareup.moshi.Moshi
import okhttp3.*
import java.io.IOException

data class BlockNumberResponse(val jsonrpc: String, val id: String, val result: String)

fun main(args: Array<String>) {

    val okhttp = OkHttpClient.Builder().build()
    val blockNumberAdapter = Moshi.Builder().build().adapter(BlockNumberResponse::class.java)

    val blockRequest = Request.Builder().url("http://192.168.5.42:9003")
            .method("POST", RequestBody.create(MediaType.parse("application/json"), "{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":83}"))
            .build()

    var lastBlock = "0x0"

    while (true) {
        okhttp.newCall(blockRequest).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response) {
                val newBlock = blockNumberAdapter.fromJson(response.body().source()).result
                if (newBlock != lastBlock) {
                    lastBlock = newBlock
                    println("New Block" + newBlock)
                }

            }

            override fun onFailure(call: Call?, e: IOException) {
                println("problem getting block number " + e.message)
            }

        })

        Thread.sleep(1000)
    }
}

