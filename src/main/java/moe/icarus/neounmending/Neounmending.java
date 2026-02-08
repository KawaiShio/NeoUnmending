package moe.icarus.neounmending;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod(Neounmending.MODID)
public class Neounmending {

    public static final String MODID = "neounmending";
    public static final ModConfigSpec CONFIG;
    public static final ModConfigSpec.BooleanValue TOOLTIPS;
    public static final ModConfigSpec.ConfigValue<Integer> COLOR;
    static {
        final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("NeoUnmending");
        TOOLTIPS = builder
                .comment("If true, display tooltips after using an anvil; if false, do not display.")
                .define("Tooltips", false);
        COLOR = builder
                .comment("Set the color of tooltips.")
                .defineInRange("Color", 0xFFFF55, 0x0, 0xFFFFFF);
        builder.pop();
        CONFIG = builder.build();
    }

    public Neounmending(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, CONFIG);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

      }
    }
}
