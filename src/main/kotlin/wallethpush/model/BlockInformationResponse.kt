package wallethpush.model

import wallethpush.model.BlockInformation

data class BlockInformationResponse(val jsonrpc: String, val id: String, val result: BlockInformation)