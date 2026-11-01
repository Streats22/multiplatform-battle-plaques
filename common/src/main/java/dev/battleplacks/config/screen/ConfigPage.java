package dev.battleplacks.config.screen;

import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Consumer;

/** One tab of the config screen. Every row is one call here, so the screen class only lists options. */
final class ConfigPage {
    private final ConfigCategory category;
    private final ConfigEntryBuilder entries;

    ConfigPage(ConfigCategory category, ConfigEntryBuilder entries) {
        this.category = category;
        this.entries = entries;
    }

    ConfigPage toggle(String label, String tip, boolean current, boolean dflt, Consumer<Boolean> save) {
        category.addEntry(entries.startBooleanToggle(Component.literal(label), current)
                .setDefaultValue(dflt).setTooltip(Component.literal(tip)).setSaveConsumer(save).build());
        return this;
    }

    <E extends Enum<E>> ConfigPage choice(String label, String tip, Class<E> type, E current, E dflt, Consumer<E> save) {
        category.addEntry(entries.startEnumSelector(Component.literal(label), type, current)
                .setDefaultValue(dflt).setTooltip(Component.literal(tip)).setSaveConsumer(save).build());
        return this;
    }

    /** A whole-number slider shown with a suffix, e.g. "92%". */
    ConfigPage slider(String label, String tip, int current, int dflt, int min, int max, String suffix, Consumer<Integer> save) {
        category.addEntry(entries.startIntSlider(Component.literal(label), current, min, max)
                .setDefaultValue(dflt).setTooltip(Component.literal(tip))
                .setTextGetter(v -> Component.literal(v + suffix)).setSaveConsumer(save).build());
        return this;
    }

    ConfigPage strings(String label, String tip, List<String> current, List<String> dflt, Consumer<List<String>> save) {
        category.addEntry(entries.startStrList(Component.literal(label), current)
                .setDefaultValue(dflt).setTooltip(Component.literal(tip)).setSaveConsumer(save).build());
        return this;
    }

    /** A text field; {@code error} returns a message to block saving, or empty when the text is fine. */
    ConfigPage text(String label, String tip, String current, Function<String, Optional<Component>> error, Consumer<String> save) {
        category.addEntry(entries.startStrField(Component.literal(label), current)
                .setDefaultValue("").setTooltip(Component.literal(tip))
                .setErrorSupplier(error).setSaveConsumer(save).build());
        return this;
    }

    /** An ARGB colour; 0 means "use the theme's colour". */
    ConfigPage colour(String label, String tip, int current, Consumer<Integer> save) {
        category.addEntry(entries.startAlphaColorField(Component.literal(label), current)
                .setDefaultValue(0).setTooltip(Component.literal(tip)).setSaveConsumer(save).build());
        return this;
    }
}
