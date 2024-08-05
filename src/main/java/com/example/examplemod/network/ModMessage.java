package com.example.examplemod.network;

import com.example.examplemod.ExampleMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class ModMessage {
    public static final SimpleChannel INSTANCE = ChannelBuilder.named(ResourceLocation.tryBuild(ExampleMod.MODID, "main"))
            .clientAcceptedVersions(((status, version) -> true))
            .serverAcceptedVersions(((status, version) -> true))
            .networkProtocolVersion(1)
            .simpleChannel();


    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        INSTANCE.messageBuilder(PChangeDifficult.class,id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(PChangeDifficult::encode)
                .decoder(PChangeDifficult::decode)
                .consumerMainThread(PChangeDifficult::handle)
                .add();

        INSTANCE.messageBuilder(PGetDifficulty.class,id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(PGetDifficulty::encode)
                .decoder(PGetDifficulty::decode)
                .consumerMainThread(PGetDifficulty::handle)
                .add();
        INSTANCE.messageBuilder(PGetServerDifficulty.class,id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(PGetServerDifficulty::encode)
                .decoder(PGetServerDifficulty::decode)
                .consumerMainThread(PGetServerDifficulty::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.send(msg, PacketDistributor.SERVER.noArg());
    }

    public static void sendToPlayer(Object msg, ServerPlayer player) {
        INSTANCE.send(msg, PacketDistributor.PLAYER.with(player));
    }

}
