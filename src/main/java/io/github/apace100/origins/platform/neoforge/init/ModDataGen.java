package io.github.apace100.origins.platform.neoforge.init;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

public final class ModDataGen {
    private static final Logger LOGGER = LogUtils.getLogger();

    private ModDataGen() {
    }

    public static void gatherData(GatherDataEvent event) {
        LOGGER.info("Origins data generation hook invoked. Include providers when ready.");
    }
}
