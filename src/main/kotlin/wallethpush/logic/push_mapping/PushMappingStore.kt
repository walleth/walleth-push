package wallethpush.logic.push_mapping

import wallethpush.model.PushMapping

interface PushMappingStore {
    fun getTokensForAddress(address: String): List<String>
    fun setPushMapping(pushMapping: PushMapping)
}
