local interval = 5
local WebhookURL = "a"

local friendly_players = {
    Riii3393 = true,
    TPparu = true,
    wakame_00 = true,
    rat495 = true,
    PEIN9391 = true,
    Tanuk1 = true,
    pan468213 = true,
    SuD_uki_mark2 = true,
    Re_Nameco = true,
    seto4979 = true,
    Norinorinoriri = true, 
    pto_Aive2836 = true,   
    Kanpan13279 = true,  
}

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
    local friend_info = ""
    local enemy_info = ""
    local players = player_info.getOnlinePlayers()
    
    for i, player_name in next, players do
        local player = player_info.getPlayerPos(player_name)
        local info = player_name .. "   nil"
        if player ~= nil then
            info = player_name .. " X=" .. math.floor(player.x) .. " Y=" .. math.floor(player.y) .. " Z=" .. math.floor(player.z)
        end
        
        if friendly_players[player_name] then
            friend_info = friend_info .. "\n" .. info
        else
            enemy_info = enemy_info .. "\n" .. info
        end
    end
    
    local all_info = ""
    if friend_info ~= "" then
        all_info = all_info .. "\n[Friendly Team]" .. friend_info
    end
    if enemy_info ~= "" then
        all_info = all_info .. "\n[Enemy Team]" .. enemy_info
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
