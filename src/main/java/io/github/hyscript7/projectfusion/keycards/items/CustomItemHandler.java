package io.github.hyscript7.projectfusion.keycards.items;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CustomItemHandler implements Listener {
    private final CustomItemService customItemService;

    public CustomItemHandler(CustomItemService customItemService) {
        this.customItemService = customItemService;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!e.hasItem()) return;
        ItemStack itemStack = e.getItem();
        if (itemStack == null) {
            return;
        }
        if (e.getHand() == null) {
            return;
        }
        CustomItem customItem = customItemService.getCustomItem(itemStack);
        if (customItem == null) {
            return;
        }
        e.setCancelled(true);
        boolean isOffHand = e.getHand().equals(EquipmentSlot.OFF_HAND);
        if (e.getAction().isLeftClick()) {
            customItem.onLeftClick(itemStack, e.getPlayer(), e.getClickedBlock(), e.getInteractionPoint(), isOffHand);
        } else if (e.getAction().isRightClick()) {
            customItem.onRightClick(itemStack, e.getPlayer(), e.getClickedBlock(), e.getInteractionPoint(), isOffHand);
        }
    }
}
