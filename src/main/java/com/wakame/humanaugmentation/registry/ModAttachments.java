package com.wakame.humanaugmentation.registry;

import com.wakame.humanaugmentation.HumanAugmentation;
import com.wakame.humanaugmentation.body.BodyData;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
    private static final DeferredRegister<AttachmentType<?>> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, HumanAugmentation.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<BodyData>> BODY = TYPES.register("body", () ->
            AttachmentType.serializable(BodyData::new)
                    .copyOnDeath()
                    .sync((holder, player) -> holder == player, StreamCodec.of((buffer, data) -> data.write(buffer), BodyData::read))
                    .build());

    private ModAttachments() {}

    public static void register(IEventBus bus) {
        TYPES.register(bus);
    }
}
