package es.degrassi.mmreborn.common.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import lombok.Setter;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class CycleTimer {
    /* the amount of time in ms to display one thing before cycling to the next one */
    private final Supplier<Integer> cycleTime;
    private long startTime;
    private long drawTime;
    private long pausedDuration = 0;

    @Setter
    private boolean ignoreShift = false;

    public CycleTimer(Supplier<Integer> cycleTime) {
        this.cycleTime = cycleTime;
        long time = System.currentTimeMillis();
        this.startTime = time - cycleTime.get();
        this.drawTime = time;
    }

    @Nullable
    public <T> T get(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        long index = ((drawTime - startTime) / cycleTime.get()) % list.size();
        return list.get(Math.toIntExact(index));
    }

    public <T> T getOrDefault(List<T> list, T defaultObject) {
        return Optional.ofNullable(get(list)).orElse(defaultObject);
    }

    public void onDraw() {
        if (!Screen.hasShiftDown() || ignoreShift) {
            if (pausedDuration > 0) {
                startTime += pausedDuration;
                pausedDuration = 0;
            }
            drawTime = System.currentTimeMillis();
        } else {
            pausedDuration = System.currentTimeMillis() - drawTime;
        }
    }
}
