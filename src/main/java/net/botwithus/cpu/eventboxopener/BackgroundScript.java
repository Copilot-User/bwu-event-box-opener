package net.botwithus.cpu.eventboxopener;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.script.Script;
import net.botwithus.rs3.script.config.ScriptConfig;

public class BackgroundScript extends Script {

    private static final long COOLDOWN = TimeUnit.SECONDS.toMillis(5);
    private static final Pattern EVENT_BOX_PATTERN = Pattern.compile(".*event.*box.*", Pattern.CASE_INSENSITIVE);

    public BackgroundScript(String name, ScriptConfig config, ScriptDefinition definition) {
        super(name, config, definition);
        this.isBackgroundScript = true;
    }

    @Override
    public void run() {

    }

    @Override
    public boolean initialize() {
        var init = super.initialize();

        if (init) {
            this.subscribe(InventoryUpdateEvent.class, this::updateHandler);
        }

        return init;
    }

    private long lastEventBoxOpen = System.currentTimeMillis() - COOLDOWN - 1; // start off cooldown

    private void updateHandler(InventoryUpdateEvent event) {
        if (!this.isActive() || this.isPaused() || this.lastEventBoxOpen + COOLDOWN < System.currentTimeMillis()) {
            return;
        }

        if (Backpack.countFreeSlots() >= 5 && Backpack.contains(EVENT_BOX_PATTERN)) {
            if (Backpack.interact(EVENT_BOX_PATTERN, "Open")) {
                this.lastEventBoxOpen = System.currentTimeMillis();
                return;
            }

            this.println("Unable to open event box");
        }
    }
}
