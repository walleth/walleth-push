package wallethpush.logic

import kontinuum.ConfigProvider
import okhttp3.Request
import okhttp3.RequestBody
import org.kethereum.rpc.EthereumRPC
import org.kethereum.rpc.JSONMediaType
import wallethpush.model.PushMessage
import wallethpush.model.PushMessageData
import wallethpush.okhttp
import wallethpush.pushMappingStore
import wallethpush.pushMessageAdapter

val ethereumRPC = EthereumRPC(ConfigProvider.config.eth_rpc_url, okhttp = okhttp)

fun watchChain() {

    var lastBlock = "0x0"

    while (true) {
        try {
            Thread.sleep(1000)

            val newBlock = ethereumRPC.getBlockNumberString()

            if (newBlock != null && newBlock != lastBlock) {
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
    ethereumRPC.getBlockByNumber(newBlock)?.transactions?.forEach {
        println(it.from + " > " + it.to)
        val tokensForFrom = pushMappingStore.getTokensForAddress(it.from)
        if (tokensForFrom.isNotEmpty()) {
            notifyTokens(tokensForFrom, it.from)
        } else {
            it.to?.let { address ->
                val tokensForTo = pushMappingStore.getTokensForAddress(address)
                if (tokensForTo.isNotEmpty()) {
                    notifyTokens(tokensForTo, address)
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
        val resultString = okhttp.newCall(request).execute().body().use { it?.string() }
        println("send notification " + resultString)
    }
}
