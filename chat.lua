local WebhookURL = "url" 

-- UTF-8のバイト配列をローマ字に変換する辞書
local romaDict = {
    -- あ行
    [230,129,130]="a", [230,129,132]="i", [230,129,134]="u", [230,129,136]="e", [230,129,138]="o",
    -- か行
    [230,129,139]="ka",[230,129,141]="ki",[230,129,143]="ku",[230,129,145]="ke",[230,129,147]="ko",
    -- さ行
    [230,129,149]="sa",[230,129,151]="shi",[230,129,153]="su",[230,129,155]="se",[230,129,157]="so",
    -- た行
    [230,129,159]="ta",[230,129,161]="chi",[230,129,164]="tsu",[230,129,166]="te",[230,129,168]="to",
    -- な行
    [230,129,170]="na",[230,129,171]="ni",[230,129,172]="nu",[230,129,173]="ne",[230,129,174]="no",
    -- は行
    [230,129,175]="ha",[230,129,178]="hi",[230,129,181]="fu",[230,129,184]="he",[230,129,187]="ho",
    -- ま行
    [230,129,158]="ma",[230,129,191]="mi",[230,129,144]="mu",[230,129,146]="me",[230,129,148]="mo",
    -- や行
    [230,129,150]="ya",[230,129,152]="yu",[230,129,154]="yo",
    -- ら行
    [230,129,156]="ra",[230,129,158]="ri",[230,129,160]="ru",[230,129,162]="re",[230,129,164]="ro",
    -- わ行・ん
    [230,129,166]="wa",[230,129,169]="wo",[230,130,147]="n",
    -- 濁点・半濁点系の主要な文字
    [230,129,140]="ga",[230,129,142]="gi",[230,129,144]="gu",[230,129,146]="ge",[230,129,148]="go",
    [230,129,150]="za",[230,129,152]="ji",[230,129,154]="zu",[230,129,156]="ze",[230,129,158]="zo",
    [230,129,160]="da",[230,129,162]="ji",[230,129,165]="zu",[230,129,167]="de",[230,129,169]="do",
    [230,129,176]="ba",[230,129,179]="bi",[230,129,182]="bu",[230,129,185]="be",[230,129,188]="bo",
    [230,129,177]="pa",[230,129,180]="pi",[230,129,183]="pu",[230,129,186]="pe",[230,129,189]="po",
    [32]=" "
}

local function convertToRomaji(bytes)
    if not bytes or #bytes == 0 then return nil end
    
    local result = ""
    local i = 1
    while i <= #bytes do
        local b1 = bytes[i]
        
        if b1 < 128 then
            result = result .. string.char(b1)
            i = i + 1
        elif b1 == 230 and i + 2 <= #bytes then
            local b2 = bytes[i+1]
            local b3 = bytes[i+2]
            
            local key = b1 .. "," .. b2 .. "," .. b3
            local match = nil
            for k, v in pairs(romaDict) do
                if type(k) == "string" and k == key then
                    match = v
                    break
                end
            end
            
            if match then
                result = result .. match
            else
                result = result .. "?" 
            end
            i = i + 3
        else
            i = i + 1
        end
    end
    return result
end

print("Romaji Chat Forwarder Started.")

while true do
    local event, username, message, uuid, isHidden, messageUtf8 = os.pullEvent("chat")
    
    local finalMessage = message
    
    if message:match("^%?+$") and messageUtf8 and #messageUtf8 > 0 then
        local romaji = convertToRomaji(messageUtf8)
        if romaji and romaji ~= "" then
            finalMessage = romaji
        end
    end
    
    local payload = textutils.serialiseJSON({
        content = "<" .. username .. "> " .. finalMessage
    })
    
    local response = http.post(WebhookURL, payload, { ["Content-Type"] = "application/json" })
    if response then
        response.close()
    end
end
