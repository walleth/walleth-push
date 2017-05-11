package wallethpush.model.eth_jsonrpc

data class BlockInformationResponse(val jsonrpc: String, val id: String, val result: BlockInformation)