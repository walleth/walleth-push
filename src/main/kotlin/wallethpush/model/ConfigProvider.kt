package wallethpush.model

import okio.Okio
import wallethpush.configAdapter
import wallethpush.configFile

object ConfigProvider {

    val config: Config by lazy {
        configAdapter.fromJson(Okio.buffer(Okio.source(configFile)))!!
    }

}