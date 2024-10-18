package io.github.hyscript7.projectfusion.keycards2.data.impl;

import io.github.hyscript7.projectfusion.keycards2.config.interfaces.Config;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.*;

import java.util.ArrayList;
import java.util.List;

public class PluginDataImpl implements PluginData {
    private final Config config;
    private final KeyCardAggregator keycardAggregator;
    private final CardReaderAggregator cardReaderAggregator;
    private List<KeyCard> keyCards;
    private List<CardReader> cardReaders;

    public PluginDataImpl(Config config) {
        this.config = config;
        this.keycardAggregator = new KeycardAggregatorImpl(this);
        this.cardReaderAggregator = new CardReaderAggregatorImpl(this);
        this.keyCards = null;
        this.cardReaders = null;
    }

    @Override
    public void save() {
        config.getConfig().set("keycards", keyCards);
        config.getConfig().set("cardreaders", cardReaders);
        // config.save();
    }

    @Override
    public void load() {
        this.keyCards = (List<KeyCard>) config.getConfig().getList("keycards", new ArrayList<>());
        this.cardReaders = (List<CardReader>) config.getConfig().getList("cardreaders", new ArrayList<>());
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
