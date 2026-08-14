local WebhookURL = "url" -- URL of discord webhook

local function decodeUtf8(bytes)
    if not bytes then return "" end
    local status, result = pcall(utf8.char, table.unpack(bytes))
    if status then
        return result
    else
        return "Window_Error: Decode Failed"
    end
end

while true do
    local event, username, message, uuid, isHidden, messageUtf8 = os.pullEvent("chat")
    
    local cleanMessage = decodeUtf8(messageUtf8)
    
    local payload = textutils.serialiseJSON({
        content = "<" .. username .. "> " .. cleanMessage
    })
    
    local response = http.post(WebhookURL, payload, { ["Content-Type"] = "application/json" })
    
    if response then
        response.close()
    end
end
