package com.falcinspire.wc;

import org.bukkit.Bukkit;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class SwearListener implements Listener {
    @EventHandler
     public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().toLowerCase().contains("bedrock edition") || event.getMessage().contains("education edition")) {
            event.setMessage(
                event.getMessage()
                    .replaceAll(
                        "[Bb][Ee][Dd][Rr][Oo][Cc][Kk] [Ee][Dd][Ii][Tt][Ii][Oo][Nn]", 
                        "******* ******"
                    )
                    .replaceAll(
                        "[Ed][Dd][Uu][Cc][Aa][Tt][Ii][Oo][Nn] [Ee][Dd][Ii][Tt][Ii][Oo][Nn]",
                        "********* *******"
                    )
            );
        }       
    }
}