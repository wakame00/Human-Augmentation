local args = { ... }
local tx, ty, tz = tonumber(args[1]), tonumber(args[2]), tonumber(args[3])

if not tx or not ty or not tz then
  error("Usage: goto <x> <y> <z>")
end

local function locate()
  local x, y, z = gps.locate(5)
  if not x then error("GPS failed") end
  return x, y, z
end

local function forward()
  while turtle.detect() do
    if not turtle.dig() then error("Cannot dig forward") end
  end
  if not turtle.forward() then error("Cannot move forward") end
end

local function up()
  while turtle.detectUp() do
    if not turtle.digUp() then error("Cannot dig up") end
  end
  if not turtle.up() then error("Cannot move up") end
end

local function down()
  while turtle.detectDown() do
    if not turtle.digDown() then error("Cannot dig down") end
  end
  if not turtle.down() then error("Cannot move down") end
end

-- Find the turtle's current facing direction with GPS.
local x0, y0, z0 = locate()
forward()
local x1, _, z1 = locate()
turtle.back()

local facing
if x1 > x0 then facing = "east"
elseif x1 < x0 then facing = "west"
elseif z1 > z0 then facing = "south"
else facing = "north"
end

local order = { "north", "east", "south", "west" }

local function turnTo(target)
  local current, wanted
  for i, name in ipairs(order) do
    if name == facing then current = i end
    if name == target then wanted = i end
  end

  while current ~= wanted do
    turtle.turnRight()
    current = current % 4 + 1
  end
  facing = target
end

local function moveAxis(axis, target)
  local x, _, z = locate()
  local current = axis == "x" and x or z

  while current ~= target do
    if axis == "x" then
      turnTo(current < target and "east" or "west")
    else
      turnTo(current < target and "south" or "north")
    end

    forward()
    x, _, z = locate()
    current = axis == "x" and x or z
  end
end

local _, y = locate()
while y ~= ty do
  if y < ty then up() else down() end
  _, y = locate()
end

moveAxis("x", tx)
moveAxis("z", tz)

print("Arrived")
