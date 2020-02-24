# Packeteer
A simple Packet Listener for Bukkit/Spigot Servers
# How to use
```java
Packeteer.registerListener(new PacketListener() {            
    @PacketHandler(type = PacketType.INCOMING, packet = "UpdateSign")
    public void onPlayInSignUpdate(PacketEvent event) {
        Packet packet = event.getPacket();
        FieldModifier<String[]> modify = packet.modify("b", String[].class);
        String[] lines = modify.read();
        for (int i = 0; i < lines.length; i++) {
            lines[i] = "nope";
        }
    }
});
```
# More to come. 
1. Send packets with ```PacketPlayer#sendPacket(Packet)```
2. Create packets with ```Packeteer.createPacket(String)``` -- ex:```Packeteer.createPacket("UpdateTime")```
