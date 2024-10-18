Hello future me, hoping you read this before trying to reverse engineer this madness again.
The plugin mostly functions. The only things left to implement are the commands, which are in 
`io.github.hyscript7.projectfusion.keycards2.commands` in the single class KeyCardsCommand.
- Consider moving door tool things to a separate command.

Secondly, there are a ton of TODOs. Search for them either using sonarlint or using the IDE's search function.
They should be mostly self documenting, but there are a few lalas here and there.
For clarification:
```
// TODO: blah blah blah
// ...
```
The three dots mean the code is supposed to be there, but I could be wrong with these assumptions,
so just put it wherever it needs to be.

After finishing that, you can implement these extra features:
- An editor for keycards that already exist (Commands to change displaynames, lore, custom model data) without needing
  to dig in configuration files
- Card readers command:
  - readers copy (doorid - by default the block we're looking at) -> Gives the player a door tool with the settings of the specified door
  - readers list (default in the world we're in, otherwise the world we specify) (page - by default 0, paginate by 10 or so, make it look nice)
  - readers delete <doorid>
  - readers tp <doorid>
- Feature: Support for trapdoors
- Feature: Don't open door when shift right-clicking it, unless holding the card itself
  - If in creative mode, show information about the door

--------- Solved by making keycards2 (Editted: 18-October-2024)
Hi future Script, 
basically, the plugin in theory could be finished like this, but I made a slight mistake in the implementation as I 
mindlessly accepted AI generated code.

Right now, it's checky for the exact itemstack of the keycard. This has many issues, but mainly the reason I don't like
this implementation is that I'd like to check an explicit "Keycard Level" NBT key on any possible itemstack.
Saves us the trouble of reducing the itemstack to 1, removing any weird data it might've gotten, etc... You know how
items are, sometimes they just refuse to stack & stuff. Either way, it's up to you to implement this the right way.
For the most part, you should only keep the data and utils package, with both needing a major makeover.

We could still keep an itemstack in the keycard so that we have the exact item to give when
using the keycard give command.

Also implement a messages configuration, it can be in the main file, basically just what messages to send when we
ie.: create a keycard reader (door), add a keycard, remove a keycard, etc...

Oh and lastly, plugin.yml needs to be remade completly from scratch. Lesson learned: Write permissions & commands AFTER
the implementation :p Oops. Silly me.
