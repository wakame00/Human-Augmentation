local interval = 5
local WebhookURL = "ここにあなたのDiscordのURL"

local player_info = peripheral.find("playerDetector") or peripheral.find("player_detector")

if not player_info then 
    error("Device not found") 
end

if WebhookURL == "" then
    error("WebhookURL is empty")
end

local success, message = http.checkURL(WebhookURL)
if not success then
    error("Invalid URL: " .. (message or ""))
end

print("System started...")

while true do
    local all_info = ""
    local players = player_info.getOnlinePlayers()
    
    for i, player_name in next, players do
        local player = player_info.getPlayerPos(player_name)
        local info = player_name .. "   nil"
        if player ~= nil then
            info = player_name .. " X=" .. math.floor(player.x) .. " Y=" .. math.floor(player.y) .. " Z=" .. math.floor(player.z)
        end
        all_info = all_info .. "\n" .. info
    end
    
    if all_info ~= "" then
        local payload = textutils.serialiseJSON({
            content = "[Radar Log]" .. all_info
        })
        
        local response = http.post(WebhookURL, payload, { ["Content-Type"] = "application/json" })
        if response then
            print("Success")
            response.close()
        else
            print("Failed")
        end
    end
    
    sleep(interval)
end
