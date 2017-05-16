package kontinuum

import okio.Okio
import wallethpush.configAdapter
import wallethpush.configFile
import wallethpush.model.Config

object ConfigProvider {

    val config: Config by lazy {
        configAdapter.fromJson(Okio.buffer(Okio.source(configFile)))!!
    }

}