package wallethpush

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import wallethpush.model.Config
import wallethpush.model.PushMapping
import wallethpush.model.eth_jsonrpc.BlockInformationResponse
import wallethpush.model.eth_jsonrpc.BlockNumberResponse
import java.io.File

val configFile = File("config.json")

val okhttp = OkHttpClient.Builder().build()!!
val moshi = Moshi.Builder().build()

val blockNumberAdapter: JsonAdapter<BlockNumberResponse> = moshi.adapter(BlockNumberResponse::class.java)
val blockInfoAdapter: JsonAdapter<BlockInformationResponse> = moshi.adapter(BlockInformationResponse::class.java)

val pushConfigRequestPayloadAdapter: JsonAdapter<PushMapping> = moshi.adapter(PushMapping::class.java)
val configAdapter: JsonAdapter<Config> = moshi.adapter(Config::class.java)

val pushMappingStore: PushMappingStore = FileBasedPushMappingStore()