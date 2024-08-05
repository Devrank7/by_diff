package com.example.examplemod.mixins.debug;

import com.example.examplemod.intrtfaces.IDifficultyInstance;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.network.Connection;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(DebugScreenOverlay.class)
public class MixDebugOverlay {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Nullable
    private ChunkPos lastPos;

    @Nullable
    @Shadow
    private ServerLevel getServerLevel() {
        throw new RuntimeException("We are asking a debug overlay for a server level");
    }

    @Shadow
    private LevelChunk getClientChunk() {
        throw new RuntimeException("We are asking a debug overlay for a client chunk");
    }

    @Shadow
    private static String printBiome(Holder<Biome> p_205375_) {
        throw new RuntimeException("We are asking a debug overlay for a biome");
    }


    @Shadow
    private String getServerChunkStats() {
        throw new RuntimeException("We are asking a debug overlay for server chunk stats");
    }

    @Shadow
    private Level getLevel() {
        throw new RuntimeException("We are asking a debug overlay for a level");
    }

    @Nullable
    @Shadow
    private LevelChunk getServerChunk() {
        throw new RuntimeException("We are asking a debug overlay for a server chunk");
    }

    @Shadow
    @Final
    private static Map<Heightmap.Types, String> HEIGHTMAP_NAMES;

    /**
     * @author N/A
     * @reason We don't need this
     */
    @Overwrite
    protected List<String> getGameInformation() {
        DebugScreenOverlay self = (DebugScreenOverlay) (Object) this;
        IntegratedServer integratedserver = this.minecraft.getSingleplayerServer();
        ClientPacketListener clientpacketlistener = this.minecraft.getConnection();
        Connection connection = clientpacketlistener.getConnection();
        float f = connection.getAverageSentPackets();
        float f1 = connection.getAverageReceivedPackets();
        TickRateManager tickratemanager = this.getLevel().tickRateManager();
        String s1;
        if (tickratemanager.isSteppingForward()) {
            s1 = " (frozen - stepping)";
        } else if (tickratemanager.isFrozen()) {
            s1 = " (frozen)";
        } else {
            s1 = "";
        }

        String s;
        if (integratedserver != null) {
            ServerTickRateManager servertickratemanager = integratedserver.tickRateManager();
            boolean flag = servertickratemanager.isSprinting();
            if (flag) {
                s1 = " (sprinting)";
            }

            String s2 = flag ? "-" : String.format(Locale.ROOT, "%.1f", tickratemanager.millisecondsPerTick());
            s = String.format(Locale.ROOT, "Integrated server @ %.1f/%s ms%s, %.0f tx, %.0f rx", integratedserver.getCurrentSmoothedTickTime(), s2, s1, f, f1);
        } else {
            s = String.format(Locale.ROOT, "\"%s\" server%s, %.0f tx, %.0f rx", clientpacketlistener.serverBrand(), s1, f, f1);
        }

        BlockPos blockpos = this.minecraft.getCameraEntity().blockPosition();
        if (this.minecraft.showOnlyReducedInfo()) {
            return Lists.newArrayList(
                    "Minecraft " + SharedConstants.getCurrentVersion().getName() + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
                    this.minecraft.fpsString,
                    s,
                    this.minecraft.levelRenderer.getSectionStatistics(),
                    this.minecraft.levelRenderer.getEntityStatistics(),
                    "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(),
                    this.minecraft.level.gatherChunkSourceStats(),
                    "",
                    String.format(Locale.ROOT, "Chunk-relative: %d %d %d", blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15)
            );
        } else {
            Entity entity = this.minecraft.getCameraEntity();
            Direction direction = entity.getDirection();

            String $$21 = switch (direction) {
                case NORTH -> "Towards negative Z";
                case SOUTH -> "Towards positive Z";
                case WEST -> "Towards negative X";
                case EAST -> "Towards positive X";
                default -> "Invalid";
            };
            ChunkPos chunkpos = new ChunkPos(blockpos);
            if (!Objects.equals(this.lastPos, chunkpos)) {
                lastPos = chunkpos;
                self.clearChunkCache();
            }

            Level level = this.getLevel();
            LongSet longset = (LongSet) (level instanceof ServerLevel ? ((ServerLevel) level).getForcedChunks() : LongSets.EMPTY_SET);
            List<String> list = Lists.newArrayList(
                    "Minecraft "
                            + SharedConstants.getCurrentVersion().getName()
                            + " ("
                            + this.minecraft.getLaunchedVersion()
                            + "/"
                            + ClientBrandRetriever.getClientModName()
                            + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType())
                            + ")",
                    this.minecraft.fpsString,
                    s,
                    this.minecraft.levelRenderer.getSectionStatistics(),
                    this.minecraft.levelRenderer.getEntityStatistics(),
                    "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(),
                    this.minecraft.level.gatherChunkSourceStats()
            );
            String s4 = getServerChunkStats();
            if (s4 != null) {
                list.add(s4);
            }

            list.add(this.minecraft.level.dimension().location() + " FC: " + longset.size());
            list.add("");
            list.add(
                    String.format(
                            Locale.ROOT,
                            "XYZ: %.3f / %.5f / %.3f",
                            this.minecraft.getCameraEntity().getX(),
                            this.minecraft.getCameraEntity().getY(),
                            this.minecraft.getCameraEntity().getZ()
                    )
            );
            list.add(
                    String.format(
                            Locale.ROOT,
                            "Block: %d %d %d [%d %d %d]",
                            blockpos.getX(),
                            blockpos.getY(),
                            blockpos.getZ(),
                            blockpos.getX() & 15,
                            blockpos.getY() & 15,
                            blockpos.getZ() & 15
                    )
            );
            list.add(
                    String.format(
                            Locale.ROOT,
                            "Chunk: %d %d %d [%d %d in r.%d.%d.mca]",
                            chunkpos.x,
                            SectionPos.blockToSectionCoord(blockpos.getY()),
                            chunkpos.z,
                            chunkpos.getRegionLocalX(),
                            chunkpos.getRegionLocalZ(),
                            chunkpos.getRegionX(),
                            chunkpos.getRegionZ()
                    )
            );
            list.add(
                    String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, $$21, Mth.wrapDegrees(entity.getYRot()), Mth.wrapDegrees(entity.getXRot()))
            );
            LevelChunk levelchunk = this.getClientChunk();
            if (levelchunk.isEmpty()) {
                list.add("Waiting for chunk...");
            } else {
                int i = this.minecraft.level.getChunkSource().getLightEngine().getRawBrightness(blockpos, 0);
                int j = this.minecraft.level.getBrightness(LightLayer.SKY, blockpos);
                int k = this.minecraft.level.getBrightness(LightLayer.BLOCK, blockpos);
                list.add("Client Light: " + i + " (" + j + " sky, " + k + " block)");
                LevelChunk levelchunk1 = this.getServerChunk();
                StringBuilder stringbuilder = new StringBuilder("CH");

                for (Heightmap.Types heightmap$types : Heightmap.Types.values()) {
                    if (heightmap$types.sendToClient()) {
                        stringbuilder.append(" ")
                                .append(HEIGHTMAP_NAMES.get(heightmap$types))
                                .append(": ")
                                .append(levelchunk.getHeight(heightmap$types, blockpos.getX(), blockpos.getZ()));
                    }
                }

                list.add(stringbuilder.toString());
                stringbuilder.setLength(0);
                stringbuilder.append("SH");

                for (Heightmap.Types heightmap$types1 : Heightmap.Types.values()) {
                    if (heightmap$types1.keepAfterWorldgen()) {
                        stringbuilder.append(" ").append(HEIGHTMAP_NAMES.get(heightmap$types1)).append(": ");
                        if (levelchunk1 != null) {
                            stringbuilder.append(levelchunk1.getHeight(heightmap$types1, blockpos.getX(), blockpos.getZ()));
                        } else {
                            stringbuilder.append("??");
                        }
                    }
                }

                list.add(stringbuilder.toString());
                if (blockpos.getY() >= this.minecraft.level.getMinBuildHeight() && blockpos.getY() < this.minecraft.level.getMaxBuildHeight()) {
                    list.add("Biome: " + MixDebugOverlay.printBiome(this.minecraft.level.getBiome(blockpos)));
                    if (levelchunk1 != null) {
                        float f2 = level.getMoonBrightness();
                        long l = levelchunk1.getInhabitedTime();
                        DifficultyInstance difficultyinstance = new DifficultyInstance(level.getDifficulty(), level.getDayTime(), l, f2);
                        ((IDifficultyInstance) difficultyinstance).preCalculateDifficulty(this.getLevel());
                        list.add(
                                String.format(
                                        Locale.ROOT,
                                        "Local Difficulty: %.2f // %.2f (Day %d)",
                                        difficultyinstance.getEffectiveDifficulty(),
                                        difficultyinstance.getSpecialMultiplier(),
                                        this.minecraft.level.getDayTime() / 24000L
                                )
                        );
                    } else {
                        list.add("Local Difficulty: ??");
                    }
                }

                if (levelchunk1 != null && levelchunk1.isOldNoiseGeneration()) {
                    list.add("Blending: Old");
                }
            }

            ServerLevel serverlevel = this.getServerLevel();
            if (serverlevel != null) {
                ServerChunkCache serverchunkcache = serverlevel.getChunkSource();
                ChunkGenerator chunkgenerator = serverchunkcache.getGenerator();
                RandomState randomstate = serverchunkcache.randomState();
                chunkgenerator.addDebugScreenInfo(list, randomstate, blockpos);
                Climate.Sampler climate$sampler = randomstate.sampler();
                BiomeSource biomesource = chunkgenerator.getBiomeSource();
                biomesource.addDebugInfo(list, blockpos, climate$sampler);
                NaturalSpawner.SpawnState naturalspawner$spawnstate = serverchunkcache.getLastSpawnState();
                if (naturalspawner$spawnstate != null) {
                    Object2IntMap<MobCategory> object2intmap = naturalspawner$spawnstate.getMobCategoryCounts();
                    int i1 = naturalspawner$spawnstate.getSpawnableChunkCount();
                    list.add(
                            "SC: "
                                    + i1
                                    + ", "
                                    + Stream.of(MobCategory.values())
                                    .map(p_94068_ -> Character.toUpperCase(p_94068_.getName().charAt(0)) + ": " + object2intmap.getInt(p_94068_))
                                    .collect(Collectors.joining(", "))
                    );
                } else {
                    list.add("SC: N/A");
                }
            }

            PostChain postchain = this.minecraft.gameRenderer.currentEffect();
            if (postchain != null) {
                list.add("Shader: " + postchain.getName());
            }

            list.add(this.minecraft.getSoundManager().getDebugString() + String.format(Locale.ROOT, " (Mood %d%%)", Math.round(this.minecraft.player.getCurrentMood() * 100.0F)));
            return list;
        }
    }
}
