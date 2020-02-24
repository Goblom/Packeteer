# Packeteer
A super lightweight Packet Listener for Bukkit/Spigot Servers with the ability to create and send packets with ease.
# How to use

1. Use a `PacketListener` paired with a `@PacketHandler` to listener to packets. Use `packet = "ALL"` to listen to EVERY packet. 
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
2. Create packets with `Packeteer.createPacket(String)`
```java
Packet pong = Packeteer.createPacket("OutChat");
```

3. Send packets with `PacketPlayer#sendPacket`
```java
Class craftChatMessage = Reflection.getClass(Reflection.ClassType.OBC, "util.CraftChatMessage");
MethodInvoker wrapOrEmpty = new MethodInvoker(craftChatMessage, "wrapOrEmpty", String.class);
Object chatComponent = wrapOrEmpty.invoke("Pong!");
pong.write("a", chatComponent);
pong.write("b", Reflection.getClass(Reflection.ClassType.NMS, "ChatMessageType").getEnumConstants()[0]);
event.getPlayer().sendPacket(pong);
```
