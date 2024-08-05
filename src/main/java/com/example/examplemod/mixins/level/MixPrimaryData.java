package com.example.examplemod.mixins.level;

import com.example.examplemod.difficulty.DifficultyGeneral;
import com.example.examplemod.intrtfaces.ILevelSettings;
import com.example.examplemod.intrtfaces.IPrimaryLevelData;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.*;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;

import javax.annotation.Nullable;
import java.util.Set;

@Mixin(PrimaryLevelData.class)
public class MixPrimaryData implements IPrimaryLevelData {


    @Shadow(remap = false)
    @Final
    private static Logger LOGGER;
    @Shadow
    private LevelSettings settings;

    @Shadow(remap = false)
    public EndDragonFight.Data endDragonFightData() {return null;}

    @Shadow(remap = false)
    private static ListTag stringCollectionToTag(Set<String> p_277880_) {
        ListTag listtag = new ListTag();
        p_277880_.stream().map(StringTag::valueOf).forEach(listtag::add);
        return listtag;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private void setTagData(RegistryAccess p_78546_, CompoundTag p_78547_, @Nullable CompoundTag p_78548_) {
        PrimaryLevelData primaryLevelData = (PrimaryLevelData) (Object) this;
        p_78547_.put("ServerBrands", stringCollectionToTag(primaryLevelData.getKnownServerBrands()));
        p_78547_.putBoolean("WasModded", primaryLevelData.wasModded());
        if (!primaryLevelData.getRemovedFeatureFlags().isEmpty()) {
            p_78547_.put("removed_features", stringCollectionToTag(primaryLevelData.getRemovedFeatureFlags()));
        }

        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString("Name", SharedConstants.getCurrentVersion().getName());
        compoundtag.putInt("Id", SharedConstants.getCurrentVersion().getDataVersion().getVersion());
        compoundtag.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().isStable());
        compoundtag.putString("Series", SharedConstants.getCurrentVersion().getDataVersion().getSeries());
        p_78547_.put("Version", compoundtag);
        NbtUtils.addCurrentDataVersion(p_78547_);
        DynamicOps<Tag> dynamicops = RegistryOps.create(NbtOps.INSTANCE, p_78546_);
        WorldGenSettings.encode(dynamicops, primaryLevelData.worldGenOptions(), p_78546_).resultOrPartial(Util.prefix("WorldGenSettings: ", LOGGER::error)).ifPresent((p_78574_) -> {
            p_78547_.put("WorldGenSettings", p_78574_);
        });
        p_78547_.putInt("GameType", primaryLevelData.getLevelSettings().gameType().getId());
        p_78547_.putInt("SpawnX", primaryLevelData.getSpawnPos().getX());
        p_78547_.putInt("SpawnY", primaryLevelData.getSpawnPos().getY());
        p_78547_.putInt("SpawnZ", primaryLevelData.getSpawnPos().getZ());
        p_78547_.putFloat("SpawnAngle", primaryLevelData.getSpawnAngle());
        p_78547_.putLong("Time", primaryLevelData.getGameTime());
        p_78547_.putLong("DayTime", primaryLevelData.getGameTime());
        p_78547_.putLong("LastPlayed", Util.getEpochMillis());
        p_78547_.putString("LevelName", primaryLevelData.getLevelName());
        p_78547_.putInt("version", 19133);
        p_78547_.putInt("clearWeatherTime", primaryLevelData.getClearWeatherTime());
        p_78547_.putInt("rainTime", primaryLevelData.getRainTime());
        p_78547_.putBoolean("raining", primaryLevelData.isRaining());
        p_78547_.putInt("thunderTime", primaryLevelData.getThunderTime());
        p_78547_.putBoolean("thundering", primaryLevelData.isThundering());
        p_78547_.putBoolean("hardcore", primaryLevelData.isHardcore());
        p_78547_.putBoolean("allowCommands", primaryLevelData.getLevelSettings().allowCommands());
        p_78547_.putBoolean("initialized", primaryLevelData.isInitialized());
        primaryLevelData.getWorldBorder().write(p_78547_);
        p_78547_.putByte("Difficulty", (byte) primaryLevelData.getLevelSettings().difficulty().getId());
        p_78547_.putBoolean("DifficultyLocked", primaryLevelData.isDifficultyLocked());
        p_78547_.put("GameRules", primaryLevelData.getLevelSettings().gameRules().createTag());
        DifficultyGeneral my_difficult = ((ILevelSettings) (Object) primaryLevelData.getLevelSettings()).getDifficultyGen();
        System.out.println("SAVE DATA = " + my_difficult);
        p_78547_.putInt("difficulty_gen", my_difficult.getId());
        p_78547_.put("DragonFight", EndDragonFight.Data.CODEC.encodeStart(NbtOps.INSTANCE, this.endDragonFightData()).getOrThrow());
        if (p_78548_ != null) {
            p_78547_.put("Player", p_78548_);
        }

        WorldDataConfiguration.CODEC
                .encodeStart(NbtOps.INSTANCE, primaryLevelData.getLevelSettings().getDataConfiguration())
                .ifSuccess(p_248505_ -> p_78547_.merge((CompoundTag)p_248505_))
                .ifError(p_327545_ -> LOGGER.warn("Failed to encode configuration {}", p_327545_.message()));
        if (primaryLevelData.getCustomBossEvents() != null) {
            p_78547_.put("CustomBossEvents", primaryLevelData.getCustomBossEvents());
        }

        p_78547_.put("ScheduledEvents", primaryLevelData.getScheduledEvents().store());
        p_78547_.putInt("WanderingTraderSpawnDelay", primaryLevelData.getWanderingTraderSpawnDelay());
        p_78547_.putInt("WanderingTraderSpawnChance", primaryLevelData.getWanderingTraderSpawnChance());
        if (primaryLevelData.getWanderingTraderId() != null) {
            p_78547_.putUUID("WanderingTraderId", primaryLevelData.getWanderingTraderId());
        }
        System.err.println("SOME");

        p_78547_.putString("forgeLifecycle", net.minecraftforge.common.ForgeHooks.encodeLifecycle(primaryLevelData.getLevelSettings().getLifecycle()));
        p_78547_.putBoolean("confirmedExperimentalSettings", primaryLevelData.hasConfirmedExperimentalWarning());
    }

    @Override
    @Unique
    public void setDifficultyGeneral(DifficultyGeneral difficultyGeneral) {
        System.err.println("HERE'S");
        PrimaryLevelData primaryLevelData = (PrimaryLevelData) (Object) this;
        settings = ((ILevelSettings) (Object) primaryLevelData.getLevelSettings()).withDifficultGen(difficultyGeneral);
    }
}
