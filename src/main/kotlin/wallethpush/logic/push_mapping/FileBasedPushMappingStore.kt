package wallethpush.logic.push_mapping

import wallethpush.model.PushMapping

class FileBasedPushMappingStore : PushMappingStore {

    var addressToUIDMap = mutableMapOf<String, MutableList<String>>()
    val uidToTokenMap = mutableMapOf<String, String>()

    override fun getTokensForAddress(address: String) = if (addressToUIDMap.containsKey(address)) {
        addressToUIDMap[address]!!.map { uidToTokenMap[it]!! }
    } else {
        emptyList<String>()
    }

    override fun setPushMapping(pushMapping: PushMapping) {
        uidToTokenMap[pushMapping.uid] = pushMapping.pushToken

        addressToUIDMap = addressToUIDMap.map {
            it.key to it.value.filter { uid -> uid != pushMapping.uid }.toMutableList()
        }.toMap().toMutableMap()

        pushMapping.addresses.forEach {
            if (addressToUIDMap[it] != null) {
                addressToUIDMap[it]!!.add(pushMapping.uid)
            } else {
                addressToUIDMap[it] = mutableListOf(pushMapping.uid)
            }
        }
    }
}