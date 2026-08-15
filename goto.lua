local targetX = -503
local targetY = 60
local targetZ = -164

local directions = { "north", "east", "south", "west" }
local facing

local function round(n)
  return math.floor(n + 0.5)
end

local function locate()
  local x, y, z = gps.locate(5)

  if not x then
    error("GPS failed")
  end

  return round(x), round(y), round(z)
end

local function refuelAll()
  if turtle.getFuelLevel() == "unlimited" then
    return
  end

  for slot = 1, 16 do
    turtle.select(slot)
    turtle.refuel()
  end

  turtle.select(1)

  if turtle.getFuelLevel() < 1 then
    error("Out of fuel")
  end
end

local function clearUp()
  while turtle.detectUp() do
    local ok, reason = turtle.digUp()

    if not ok then
      error("Cannot dig up: " .. tostring(reason))
    end
  end
end

local function moveForward()
  refuelAll()

  -- Make the current tunnel space 2 blocks high.
  clearUp()

  while turtle.detect() do
    local ok, reason = turtle.dig()

    if not ok then
      error("Cannot dig forward: " .. tostring(reason))
    end
  end

  local ok, reason = turtle.forward()

  if not ok then
    turtle.attack()
    ok, reason = turtle.forward()
  end

  if not ok then
    error("Cannot move forward: " .. tostring(reason))
  end

  -- Clear the upper block at the new position too.
  clearUp()
end

local function moveUp()
  refuelAll()

  while turtle.detectUp() do
    local ok, reason = turtle.digUp()

    if not ok then
      error("Cannot dig up: " .. tostring(reason))
    end
  end

  local ok, reason = turtle.up()

  if not ok then
    error("Cannot move up: " .. tostring(reason))
  end
end

local function moveDown()
  refuelAll()

  while turtle.detectDown() do
    local ok, reason = turtle.digDown()

    if not ok then
      error("Cannot dig down: " .. tostring(reason))
    end
  end

  local ok, reason = turtle.down()

  if not ok then
    error("Cannot move down: " .. tostring(reason))
  end
end

local function findFacing()
  local x0, _, z0 = locate()

  moveForward()

  local x1, _, z1 = locate()

  local ok, reason = turtle.back()

  if not ok then
    error("Cannot move back: " .. tostring(reason))
  end

  if x1 > x0 then
    facing = "east"
  elseif x1 < x0 then
    facing = "west"
  elseif z1 > z0 then
    facing = "south"
  else
    facing = "north"
  end
end

local function turnTo(target)
  local current
  local wanted

  for i, direction in ipairs(directions) do
    if direction == facing then current = i end
    if direction == target then wanted = i end
  end

  while current ~= wanted do
    turtle.turnRight()
    current = current % 4 + 1
  end

  facing = target
end

local function moveX()
  local x = locate()

  while x ~= targetX do
    if x < targetX then
      turnTo("east")
    else
      turnTo("west")
    end

    moveForward()
    x = locate()
  end
end

local function moveZ()
  local _, _, z = locate()

  while z ~= targetZ do
    if z < targetZ then
      turnTo("south")
    else
      turnTo("north")
    end

    moveForward()
    _, _, z = locate()
  end
end

local function moveY()
  local _, y = locate()

  while y ~= targetY do
    if y < targetY then
      moveUp()
    else
      moveDown()
    end

    _, y = locate()
  end
end

print("Refueling...")
refuelAll()

print("Checking GPS...")
findFacing()

print("Moving Y...")
moveY()

print("Moving X...")
moveX()

print("Moving Z...")
moveZ()

print("Arrived")
