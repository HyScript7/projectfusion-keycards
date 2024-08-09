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
