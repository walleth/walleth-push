package wallethpush.logic.push_mapping

import okio.Okio
import wallethpush.model.PushMapping
import wallethpush.pushMappingAdapter
import java.io.File
import java.nio.charset.Charset

class FileBasedPushMappingStore : BasePushMappingStore() {

    private val databaseDirectory = File("db")

    init {
        if (!databaseDirectory.exists()) {
            databaseDirectory.mkdir()
        } else {
            databaseDirectory.listFiles().forEach {
                val pushMapping = pushMappingAdapter.fromJson(Okio.buffer(Okio.source(it)))!!
                println("importing $pushMapping")
                setPushMappingInternal(pushMapping)
            }
        }
    }

    override fun setPushMapping(pushMapping: PushMapping) {
        super.setPushMapping(pushMapping)

        val file = File(databaseDirectory, pushMapping.uid)
        Okio.buffer(Okio.sink(file)).use {
            it.writeString(pushMappingAdapter.toJson(pushMapping), Charset.forName("utf-8"))
        }

    }

}