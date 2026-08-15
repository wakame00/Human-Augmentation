local interval = 30
local WebhookURL = "ここにWebhookのURLを入力"

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

if WebhookURL == "" or WebhookURL == "a" then
    error("WebhookURL is empty")
end

local success, message = http.checkURL(WebhookURL)
if not success then
    error("Invalid URL: " .. (message or ""))
end

print("System started with Dimension tracking...")

local last_data_content = ""

while true do
    local friend_info = ""
    local enemy_info = ""
    
    local players = player_info.getOnlinePlayers()
    local time_str = string.format("[%s]", os.date("%H:%M:%S"))
    
    for i, player_name in next, players do
        local player = player_info.getPlayerPos(player_name)
        local info_without_time = player_name .. "   nil"
        
        if player ~= nil then
            local hp_str = "HP=" .. (player.health and math.floor(player.health) or "unknown")
            
"minecraft:the_nether"）
            local dim_str = player.dimension or "unknown"
            dim_str = string.gsub(dim_str, "minecraft:", "") 
            
            local respawn_str = "Respawn=nil"
            if player.respawnPosition ~= nil then
                local respawn_dim = player.respawnPosition.dimension or ""
                respawn_dim = string.gsub(respawn_dim, "minecraft:", "")
                respawn_dim = respawn_dim ~= "" and (" Dim=" .. respawn_dim) or ""
                
                respawn_str = "Respawn(X=" .. math.floor(player.respawnPosition.x) .. " Y=" .. math.floor(player.respawnPosition.y) .. " Z=" .. math.floor(player.respawnPosition.z) .. respawn_dim .. ")"
            end
            
            info_without_time = player_name .. " [" .. dim_str .. "] X=" .. math.floor(player.x) .. " Y=" .. math.floor(player.y) .. " Z=" .. math.floor(player.z) .. " " .. hp_str .. " " .. respawn_str
        end
        
        if friendly_players[player_name] then
            friend_info = friend_info .. "\n" .. info_without_time
        else
            enemy_info = enemy_info .. "\n" .. info_without_time
        end
    end
    
    local current_data_content = "--- Player Report " .. time_str .. " ---\n"
    current_data_content = current_data_content .. "【Friends】" .. (friend_info == "" and "\nNone" or friend_info) .. "\n\n"
    current_data_content = current_data_content .. "【Enemies】" .. (enemy_info == "" and "\nNone" or enemy_info)
    
    if current_data_content ~= last_data_content then
        print("Sending update...")
        local payload = textutils.serialiseJSON({
            content = "```" .. current_data_content .. "```"
        })
        http.post(WebhookURL, payload, {["Content-Type"] = "application/json"})
        last_data_content = current_data_content
    end
    
    os.sleep(interval)
end
