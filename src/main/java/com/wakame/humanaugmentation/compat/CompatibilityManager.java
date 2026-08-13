package com.wakame.humanaugmentation.compat;

import com.wakame.humanaugmentation.HumanAugmentation;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import net.neoforged.fml.ModList;

public final class CompatibilityManager {
    private static final Map<CompatTarget, Status> STATUSES = new EnumMap<>(CompatTarget.class);

    public static void initialize() {
        STATUSES.clear();
        for (CompatTarget target : CompatTarget.values()) {
            Status status = detect(target);
            STATUSES.put(target, status);
            if (status.loaded()) {
                HumanAugmentation.LOGGER.info("[Compat] Detected {} ({}, version {})",
                        target.displayName(), status.modId(), status.version());
            }
        }
    }

    public static boolean isLoaded(CompatTarget target) {
        return status(target).loaded();
    }

    public static Status status(CompatTarget target) {
        return STATUSES.getOrDefault(target, Status.absent());
    }

    public static Map<CompatTarget, Status> statuses() {
        return Map.copyOf(STATUSES);
    }

    public static boolean initializeIsolated(CompatTarget target, String implementationClass) {
        Status detected = status(target);
        if (!detected.loaded()) return false;
        try {
            Class<?> type = Class.forName(implementationClass, true, CompatibilityManager.class.getClassLoader());
            type.getMethod("initialize").invoke(null);
            STATUSES.put(target, detected.asEnabled());
            HumanAugmentation.LOGGER.info("[Compat] Enabled {} integration", target.displayName());
            return true;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
                 | InvocationTargetException | LinkageError exception) {
            STATUSES.put(target, detected.asFailed(exception.getClass().getSimpleName()));
            HumanAugmentation.LOGGER.error("[Compat] Disabled {} integration after an isolated initialization failure",
                    target.displayName(), exception);
            return false;
        }
    }

    private static Status detect(CompatTarget target) {
        for (String modId : target.modIds()) {
            Optional<? extends net.neoforged.fml.ModContainer> container = ModList.get().getModContainerById(modId);
            if (container.isPresent()) {
                String version = container.get().getModInfo().getVersion().toString();
                return new Status(true, false, modId, version, "detected");
            }
        }
        return Status.absent();
    }

    public record Status(boolean loaded, boolean enabled, String modId, String version, String detail) {
        private static Status absent() { return new Status(false, false, "", "", "absent"); }
        private Status asEnabled() { return new Status(true, true, modId, version, "enabled"); }
        private Status asFailed(String reason) { return new Status(true, false, modId, version, reason); }
    }

    private CompatibilityManager() {}
}
