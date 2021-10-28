# Nave
[English](README.md) | Español | [Português brasileiro](LEIAME.md)

## Descripción
Nave es una modificación del ejemplo Tappy Defender, del libro 'ANDROID GAME
Programming by Example', por John Horton, 2015, Packt Pub., ISBN
139781785280122 . La modificación consiste en una corrección de un bug existente en
el ejemplo, lo que hace que el juego finalmente deje de ejecutarse en, además de
cambios en la dificultad del juego para que se pueda jugar en teléfonos móviles
con pantallas más pequeñas que las sugeridas en el ejemplo.

## El bug
Un error en el loop principal del juego hace que la cantidad de píxeles utilizados
simular estrellas en el fondo doble cada vez que se reinicia el juego, causando
se acumulan más objetos en la memoria de los que deberían, aparte de la contaminación visual
que este error causa. Eventualmente el juego se detiene, dependiendo del dispositivo,
reclamando la excepción de memoria. En esta modificación, el error se resuelve con el
liberación y reubicación correcta de estos objetos.
