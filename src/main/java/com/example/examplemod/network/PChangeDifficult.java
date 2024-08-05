package com.example.examplemod.network;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevelSettings;
import com.example.examplemod.intrtfaces.IPrimaryLevelData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class PChangeDifficult {
    private DifficultyGeneral my_difficult;

    // Конструктор пакета
    public PChangeDifficult(DifficultyGeneral my_difficult) {
        this.my_difficult = my_difficult;
    }

    // Метод для декодирования пакета
    public static PChangeDifficult decode(FriendlyByteBuf buffer) {
        return new PChangeDifficult(DifficultyGeneral.byId(buffer.readInt()));
    }

    // Метод для кодирования пакета
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(my_difficult.getId());
    }

    // Обработчик пакета на сервере
    public void handle(CustomPayloadEvent.Context event) {
        if (event.isServerSide()) {
            ServerPlayer player = event.getSender();
            DifficultyGeneral difficulty = ((ILevelSettings) (Object) player.level().getServer().getWorldData().getLevelSettings()).getDifficultyGen();
            System.out.println("before | PChangeDifficult: " + difficulty);
            ((IPrimaryLevelData) (Object) player.level().getServer().getWorldData()).setDifficultyGeneral(my_difficult);
            event.setPacketHandled(true);
        } else {
            event.setPacketHandled(false);
        }
    }
}
