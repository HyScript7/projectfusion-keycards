package io.github.hyscript7.projectfusion.keycards2.config.interfaces;

import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReader;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.KeyCard;
import net.kyori.adventure.text.Component;

import java.util.List;

/*
 * This interface describes the Message Configuration file. In specific, this allows us to get messages.
 */
public interface MessageProvider {
    /**
     * This method returns the prefix that will be added to all messages.
     * @return Component
     */
    Component getPrefix();

    /**
     * This method returns the missing permissions message.
     * @return Component
     */
    Component getNoPermission();

    /**
     * This method returns the message shown when a keycard is successfully created.
     * @param keyCard - The new keycard instance
     * @return Component
     */
    Component getKeyCardAdded(KeyCard keyCard);

    /**
     * This method returns the message shown when a keycard is successfully deleted.
     * @param keyCard - The keycard that will be deleted
     * @return Component
     */
    Component getKeyCardDeleted(KeyCard keyCard);

    /**
     * This method returns the message shown when an invalid keycard ID is passed.
     * @return Component
     */
    Component getKeyCardNotFound();

    /**
     * This method returns a message showing the provided keycards
     * @param keyCards - The keycards to display
     * @return Component
     */
    Component getKeyCardList(List<KeyCard> keyCards);

    /**
     * This method returns the message that is shown when the key card list is empty.
     * @return Component
     */
    Component getKeyCardListEmpty();

    /**
     * This method returns the help message for the /keycards command
     * @return Component
     */
    Component getKeyCardHelp();

    /**
     * This method returns a message saying that a card reader was successfully created.
     * @param cardReader - The created card reader instance
     * @return Component
     */
    Component getCardReaderAdded(CardReader cardReader);

    /**
     * This method returns a message saying that a card reader was deleted.
     * @param cardReader - The card reader that will be deleted
     * @return Component
     */
    Component getCardReaderDeleted(CardReader cardReader);

    /**
     * This method returns a message saying that a card reader was destroyed.
     * @param cardReader - The card reader that will be deleted
     * @return Component
     */
    Component getCardReaderDestroyed(CardReader cardReader);

    /**
     * This method returns an error message saying that a cardreader with the specified ID was not found.
     * @return Component
     */
    Component getCardReaderNotFound();

    /**
     * This method returns a single line component describing information about the specified card reader
     * @param cardReader - The card reader to describe
     * @return Component (Single line)
     */
    Component getCardReaderInformation(CardReader cardReader);

    /**
     * This method returns the list of card readers
     * @param cardReaders - The list card readers to display
     * @return Component
     */
    Component getCardReaderList(List<CardReader> cardReaders);

    /**
     * This method returns the message listing readers when there are no readers
     * @return Component
     */
    Component getCardReaderListEmpty();

    /**
     * This method returns a help message saying how to use the readers command
     * @return Component
     */
    Component getCardReaderHelp();

    /**
     * This method returns an access-denied message saying that the card reader requires level A while the users keycard is level B
     * @param cardReader - The card reader we tried to open
     * @param keyCard - The key card that was used to verify access
     * @return Component
     */
    Component getCardReaderLevelMismatch(CardReader cardReader, KeyCard keyCard);

    /**
     * This method returns an access-denied message saying that the card reader requires level A while the users keycard is only level B
     * @param cardReader - The card reader we tried to open
     * @param keyCard - The key card that was used to verify access
     * @return Component
     */
    Component getCardReaderLevelHigher(CardReader cardReader, KeyCard keyCard);

    /**
     * THis method returns an access-granted message.
     * @return Component
     */
    Component getCardReaderLevelMatch();
}
