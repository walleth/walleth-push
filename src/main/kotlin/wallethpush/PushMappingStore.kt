package wallethpush

import wallethpush.model.PushMapping

class FileBasedPushMappingStore : PushMappingStore {

    val map = mutableMapOf<String, MutableList<String>>()

    override fun getTokensForAddress(address: String)
            = map[address] ?: mutableListOf()

    override fun setPushMapping(pushMapping: PushMapping) {
        pushMapping.addresses.forEach {
            if (map[it] != null) {
                map[it]!!.add(pushMapping.pushToken)
            } else {
                map[it] = mutableListOf(pushMapping.pushToken)
            }
        }
    }
}

interface PushMappingStore {
    fun getTokensForAddress(address: String): List<String>
    fun setPushMapping(pushMapping: PushMapping)
}
