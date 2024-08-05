package com.example.examplemod.network;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import com.example.examplemod.intrtfaces.ILevelSettings;
import com.example.examplemod.intrtfaces.IPrimaryLevelData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class PGetDifficulty {

    // Конструктор пакета
    public PGetDifficulty() {
    }

    // Метод для декодирования пакета
    public static PGetDifficulty decode(FriendlyByteBuf buffer) {
        return new PGetDifficulty();
    }

    // Метод для кодирования пакета
    public void encode(FriendlyByteBuf buffer) {
    }

    // Обработчик пакета на сервере
    public void handle(CustomPayloadEvent.Context event) {
        if (event.isServerSide()) {
            ServerPlayer player = event.getSender();
            DifficultyGeneral difficultyGeneral = ((ILevel) player.level()).getDifficultyGen();
            player.sendSystemMessage(Component.literal("Current difficulty: " + difficultyGeneral.getName()));
            player.sendSystemMessage(Component.literal("Current minecraft difficulty id: " + player.level().getDifficulty()));
        } else {
            event.setPacketHandled(false);
        }
    }
}
