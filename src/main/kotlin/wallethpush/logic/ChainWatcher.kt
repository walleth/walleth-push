package wallethpush.logic

import okhttp3.Request
import okhttp3.RequestBody
import org.kethereum.functions.getTokenTransferTo
import org.kethereum.functions.isTokenTransfer
import org.kethereum.rpc.EthereumRPC
import org.kethereum.rpc.JSONMediaType
import org.kethereum.rpc.toKethereumTransaction
import wallethpush.model.ConfigProvider
import wallethpush.model.PushMessage
import wallethpush.model.PushMessageData
import wallethpush.okhttp
import wallethpush.pushMappingStore
import wallethpush.pushMessageAdapter
import java.math.BigInteger

class StatefulChain(val name: String, val ethereumRPC: EthereumRPC, var lastBlock: String)


fun watchChain() {


    val statefulChains = ConfigProvider.config.chains.map {
        StatefulChain(it.name, EthereumRPC(it.eth_rpc_url, okhttp = okhttp), "0x0")
    }

    while (true) {

        Thread.sleep(1000)

        for (statefulChain in statefulChains) {
            try {
                val newBlock = statefulChain.ethereumRPC.getBlockNumberString()

                if (newBlock != null && newBlock != statefulChain.lastBlock) {
                    statefulChain.lastBlock = newBlock
                    println("New Block " + BigInteger(newBlock.replace("0x",""), 16) + " on " + statefulChain.name)
                    processBlockNumber(newBlock, statefulChain.ethereumRPC)
                }
            } catch (e: Exception) {
                println("problem at block ${statefulChain.lastBlock} " + e.message)
            }
        }


    }

}

fun processBlockNumber(newBlock: String, ethereumRPC: EthereumRPC) {
    ethereumRPC.getBlockByNumber(newBlock)?.transactions?.forEach {
        val pushTokensToNotify = pushMappingStore.getTokensForAddress(it.from).toMutableList()

        if (it.toKethereumTransaction().isTokenTransfer()) {
            val to = it.toKethereumTransaction().getTokenTransferTo()
            pushTokensToNotify.addAll(pushMappingStore.getTokensForAddress(to.hex))
        } else {

            it.to?.let { address ->
                pushTokensToNotify.addAll(pushMappingStore.getTokensForAddress(address))
            }
        }

        if (pushTokensToNotify.isNotEmpty()) {
            notifyTokens(pushTokensToNotify, it.from)
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
