package moe.icarus.neounmending;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

@EventBusSubscriber
public class EventHandler {

    @SubscribeEvent
    public static void killMending(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        ExperienceOrb orb = event.getOrb();

        player.takeXpDelay = 2;
        player.take(orb, 1);
        if (orb.value > 0) player.giveExperiencePoints(orb.value);

        orb.remove(Entity.RemovalReason.KILLED);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        ItemStack out = event.getOutput();
        RegistryAccess registryAccess = event.getPlayer().level().registryAccess();

        if (out.isEmpty() && (left.isEmpty() || right.isEmpty())) return;

        boolean isMended = false;

        ItemEnchantments enchLeft = EnchantmentHelper.getEnchantmentsForCrafting(left);
        ItemEnchantments enchRight = EnchantmentHelper.getEnchantmentsForCrafting(right);

        if (enchLeft.keySet().contains(getHolder(registryAccess,Enchantments.MENDING)) || enchRight.keySet().contains(getHolder(registryAccess,Enchantments.MENDING))) {
            if (left.getItem() == right.getItem()) isMended = true;
            if (right.getItem() == Items.ENCHANTED_BOOK) isMended = true;
        }

        if (!isMended) return;
        if (out.isEmpty()) out = left.copy();
       // if (!out.hasTag()) out.setTag(new CompoundTag());

        ItemEnchantments.Mutable mutableEnchantments = new ItemEnchantments.Mutable(enchLeft);
        for (var entry : enchRight.entrySet()) {
            Holder<Enchantment> enchantment = entry.getKey();
            int level = entry.getIntValue();
            mutableEnchantments.upgrade(enchantment, level);
        }

        mutableEnchantments.removeIf(holder -> holder == getHolder(registryAccess,Enchantments.MENDING));

        ItemEnchantments finalEnchantments = mutableEnchantments.toImmutable();
        out.set(DataComponents.ENCHANTMENTS, finalEnchantments);

        out.set(DataComponents.REPAIR_COST,0);
        if (out.isDamageableItem()) out.setDamageValue(0);

        event.setOutput(out);
        if (event.getCost() == 0) event.setCost(1);
    }

    public static Holder<Enchantment> getHolder(RegistryAccess registryAccess, ResourceKey<Enchantment> key) {
        return registryAccess
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolder(key)
                .orElseThrow();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onTooltip(ItemTooltipEvent event) {
        if(Neounmending.TOOLTIPS.get()){
            TextColor color = TextColor.fromRgb(Neounmending.COLOR.get());
            Style style = Style.EMPTY.withColor(color);
            Component itemGotModified = Component.translatable("unmending.repaired").setStyle(style);
            Integer repairCost = event.getItemStack().get(DataComponents.REPAIR_COST);
            if (repairCost != null && repairCost > 0) event.getToolTip().add(itemGotModified);
        }
    }
}