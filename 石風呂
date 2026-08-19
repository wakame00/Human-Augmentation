local TARGET_BLOCK = "minecraft:deepslate"

local function checkAndReplace(inspectFunc, digFunc, placeFunc)
    local success, data = inspectFunc()
    
    if success then
        if data.name ~= TARGET_BLOCK then
            digFunc()
            placeFunc()
        end
    else
        placeFunc()
    end
end

for i = 1, 10 do
    if turtle.getFuelLevel() == 0 then
        print("No Fuel!")
        break
    end
    
    checkAndReplace(turtle.inspect, turtle.dig, turtle.place)
    
    if turtle.forward() then
        turtle.select(1)
        checkAndReplace(turtle.inspectUp, turtle.digUp, turtle.placeUp)
        checkAndReplace(turtle.inspectDown, turtle.digDown, turtle.placeDown)
    end
end

print("Done")
