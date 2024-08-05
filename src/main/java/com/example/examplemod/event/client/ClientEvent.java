package com.example.examplemod.event.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.network.ModMessage;
import com.example.examplemod.network.PGetDifficulty;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class ClientEvent {
    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        if (event.getKey() == GLFW.GLFW_KEY_X) {
            ModMessage.sendToServer(new PGetDifficulty());
        }
    }
}
