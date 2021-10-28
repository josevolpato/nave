# Nave
English | [Español](LEEME.md) | [Português brasileiro](LEIAME.md)

## Description
Nave is a modification of the Tappy Defender example, from the book 'ANDROID GAME
Programming by Example', by John Horton, 2015, Packt Pub., ISBN
139781785280122 . This modification consists of a fix for a bug present in the
example, which causes the game to eventually stop running, in addition to
changes in the game's difficulty to make it playable on mobile phones
with smaller screens than suggested in the example.

## The bug
A bug in the game's main loop causes the number of pixels used to
simulate stars in the background double each time the game restarts, causing
more and more objects accumulate in memory than they should, apart from visual pollution
that this bug causes. Eventually, the game stops (depending on the device),
due exception of lack of memory. In this modification the bug is fixed with the
correct release and relocation of these objects.
