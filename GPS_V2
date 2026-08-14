local interval = 30
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

local last_data_content = ""

while true do
    local friend_info = ""
    local enemy_info = ""
    local raw_friend_info = ""
    local raw_enemy_info = ""
    
    local players = player_info.getOnlinePlayers()
    
    local time_str = string.format("[%s]", os.date("%H:%M:%S"))
    
    for i, player_name in next, players do
        local player = player_info.getPlayerPos(player_name)
        local info_without_time = player_name .. "   nil"
        
        if player ~= nil then
            local hp_str = "HP=" .. (player.health and math.floor(player.health) or "unknown")
            
            local respawn_str = "Respawn=nil"
            if player.respawnPosition ~= nil then
                respawn_str = "Respawn(X=" .. math.floor(player.respawnPosition.x) .. " Y=" .. math.floor(player.respawnPosition.y) .. " Z=" .. math.floor(player.respawnPosition.z) .. ")"
            end
            
            info_without_time = player_name .. " X=" .. math.floor(player.x) .. " Y=" .. math.floor(player.y) .. " Z=" .. math.floor(player.z) .. " " .. hp_str .. " " .. respawn_str
        end
        
        if friendly_players[player_name] then
            friend_info = friend_info .. "\n" .. info_without_time
            raw_friend_info = raw_friend_info .. "\n" .. info_without_time
        else
            enemy_info = enemy_info .. "\n" .. info_without_time
            raw_enemy_info = raw_enemy_info .. "\n" .. info_without_time
        end
    end
    
    local all_info = ""
    if friend_info ~= "" then
        all_info = all_info .. "\n[Friendly Team]" .. friend_info
    end
    if enemy_info ~= "" then
        all_info = all_info .. "\n[Enemy Team]" .. enemy_info
    end
    
    local current_data_content = raw_friend_info .. "|||" .. raw_enemy_info
    
    if all_info ~= "" and current_data_content ~= last_data_content then
        local payload = textutils.serialiseJSON({
            content = "[Radar Log] " .. time_str .. all_info
        })
        
        local response = http.post(WebhookURL, payload, { ["Content-Type"] = "application/json" })
        if response then
            print("Success")
            response.close()
            last_data_content = current_data_content
        else
            print("Failed")
        end
    end
    
    sleep(interval)
end
