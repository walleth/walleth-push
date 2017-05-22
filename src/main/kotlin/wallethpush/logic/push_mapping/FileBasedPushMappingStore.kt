package wallethpush.logic.push_mapping

import okio.Okio
import wallethpush.model.PushMapping
import wallethpush.pushMappingAdapter
import java.io.File
import java.nio.charset.Charset

class FileBasedPushMappingStore : BasePushMappingStore() {

    val db_dir = File("db")

    init {
        if (!db_dir.exists()) {
            db_dir.mkdir()
        } else {
            db_dir.listFiles().forEach {
                val pushMapping = pushMappingAdapter.fromJson(Okio.buffer(Okio.source(it)))!!
                println("importing $pushMapping")
                setPushMappingInternal(pushMapping)
            }
        }
    }

    override fun setPushMapping(pushMapping: PushMapping) {
        super.setPushMapping(pushMapping)

        val file = File(db_dir, pushMapping.uid)
        Okio.buffer(Okio.sink(file)).use {
            it.writeString(pushMappingAdapter.toJson(pushMapping), Charset.forName("utf-8"))
        }

    }

}