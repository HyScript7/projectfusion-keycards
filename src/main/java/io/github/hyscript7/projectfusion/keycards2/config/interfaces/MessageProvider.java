package io.github.hyscript7.projectfusion.keycards2.config.interfaces;

import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReader;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.KeyCard;
import net.kyori.adventure.text.Component;

import java.util.List;

/*
 * This interface describes the Message Configuration file. In specific, this allows us to get messages.
 */
public interface MessageProvider {
    Component getPrefix();
    Component getNoPermission();
    Component getKeyCardAdded(KeyCard keyCard);
    Component getKeyCardDeleted(KeyCard keyCard);
    Component getKeyCardNotFound();
    Component getKeyCardList(List<KeyCard> keyCards);
    Component getKeyCardListEmpty();
    Component getKeyCardHelp();
    Component getCardReaderAdded(CardReader cardReader);
    Component getCardReaderDeleted(CardReader cardReader);
    Component getCardReaderNotFound();
    Component getCardReaderList(List<CardReader> cardReaders);
    Component getCardReaderListEmpty();
    Component getCardReaderHelp();
}
