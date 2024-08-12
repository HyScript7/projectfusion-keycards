package io.github.hyscript7.projectfusion.keycards2.data.impl;

import io.github.hyscript7.projectfusion.keycards2.config.interfaces.Config;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.*;

import java.util.List;

public class PluginDataImpl implements PluginData {
    private final Config config;
    private final KeyCardAggregator keycardAggregator;
    private final CardReaderAggregator cardReaderAggregator;
    private final List<KeyCard> keyCards;
    private final List<CardReader> cardReaders;

    public PluginDataImpl(Config config) {
        this.config = config;
        this.keycardAggregator = new KeycardAggregatorImpl(this);
        this.cardReaderAggregator = new CardReaderAggregatorImpl(this);
        this.keyCards = null;
        this.cardReaders = null;
    }

    @Override
    public void save() {
        config.save();
    }

    @Override
    public void load() {
        config.load();
    }

    @Override
    public List<KeyCard> getKeyCards() {
        return keyCards;
    }

    @Override
    public List<CardReader> getCardReaders() {
        return cardReaders;
    }

    @Override
    public KeyCardAggregator getKeyCardAggregator() {
        return keycardAggregator;
    }

    @Override
    public CardReaderAggregator getCardReaderAggregator() {
        return cardReaderAggregator;
    }
}
