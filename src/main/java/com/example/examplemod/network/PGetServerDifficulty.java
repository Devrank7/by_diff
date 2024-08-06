package com.example.examplemod.network;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class PGetServerDifficulty {

    public PGetServerDifficulty() {
    }

    // Метод для декодирования пакета
    public static PGetServerDifficulty decode(FriendlyByteBuf buffer) {
        return new PGetServerDifficulty();
    }

    // Метод для кодирования пакета
    public void encode(FriendlyByteBuf buffer) {
    }

    // Обработчик пакета на сервере
    public void handle(CustomPayloadEvent.Context event) {
        if (event.isServerSide()) {
            ServerPlayer player = event.getSender();
            DifficultyGeneral difficultyGeneral = ((ILevel) player.level()).getDifficultyGen();
            DifficultyHandler.setDifficultyGeneral(difficultyGeneral);
        } else {
            event.setPacketHandled(false);
        }
    }
}
