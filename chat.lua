local WebhookURL = "url" -- URL of discord webhook

local function cleanText(msg, bytes)
    if type(bytes) == "table" and #bytes > 0 then
        local status, result = pcall(utf8.char, table.unpack(bytes))
        if status then return result end
    end
    
    if type(msg) == "string" then
        return msg
    end
    
    return "(無効な文字データ)"
end

while true do
    local event, username, message, uuid, isHidden, messageUtf8 = os.pullEvent("chat")
    
    local cleanMessage = cleanText(message, messageUtf8)
    
    local payload = textutils.serialiseJSON({
        content = "<" .. username .. "> " .. cleanMessage
    })
    
    local response = http.post(WebhookURL, payload, { ["Content-Type"] = "application/json" })
    
    if response then
        response.close()
    end
end
