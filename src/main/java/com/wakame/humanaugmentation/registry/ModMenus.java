package com.wakame.humanaugmentation.registry;

import com.wakame.humanaugmentation.HumanAugmentation;
import com.wakame.humanaugmentation.surgery.SurgeryMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, HumanAugmentation.MOD_ID);
    public static final DeferredHolder<MenuType<?>, MenuType<SurgeryMenu>> SURGERY = MENUS.register("surgery", () -> IMenuTypeExtension.create(SurgeryMenu::new));

    private ModMenus() {}

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
