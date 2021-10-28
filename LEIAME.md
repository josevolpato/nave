# Nave
[English](README.md) | [Español](LEEME.md) | Português brasileiro

## Descrição
Nave é uma modificação do exemplo Tappy Defender, do livro 'ANDROID GAME
Programming by Example', por John Horton, 2015, Packt Pub., ISBN
139781785280122 . A modificação consiste em uma correção de um bug presente no
exemplo, que faz com que o jogo eventualmente pare sua execução, além de
modificações na dificuldade do jogo para que o mesmo seja jogável em celulares
com telas menores do que a sugerida no exemplo.

## O bug
Um bug no loop principal do jogo faz com que o número de pixels usados para
simular estrelas ao fundo dobre a cada vez que o jogo recomeça, fazendo com que
se acumulem na memória objetos a mais do que deveriam, fora a poluição visual
que esse bug causa. Eventualmente o jogo para, dependendo do dispositivo,
alegando exceção por falta de memória. Nessa modificação o bug é resolvido com a
correta liberação e realocação desses objetos.
