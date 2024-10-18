# Docs

The package `io.github.hyscript7.projectfusion.keycards2.items` holds classes required to work with CardReader tools - 
Items that can create card readers.
We do not have classes for keycards themselves here, because that is already handled for us in the keycard aggregator.
Same with card readers themselves. We only need this for the items that create card readers.

The class `CardReaderBlock` is a wrapper for the block that represents a CardReader. The `CardReader` class holds the
information about the card reader itself. In a sense, a CardReaderBlock is the frontend for a CardReader.
