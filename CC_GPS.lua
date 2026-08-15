print("GPS Host Setup")

write("X: ")
local x = tonumber(read())

write("Y: ")
local y = tonumber(read())

write("Z: ")
local z = tonumber(read())

if not x or not y or not z then
  error("Invalid coordinates")
end

local file = fs.open("startup.lua", "w")
file.writeLine('shell.run("gps", "host", ' .. x .. ", " .. y .. ", " .. z .. ")")
file.close()

print("startup.lua created")
print("Rebooting...")
sleep(2)
os.reboot()
