package theGome;

import java.util.List;
import java.util.Random;

import theGome.Game.GameStack;
import theGome.Game.Move;

public class RandomPlayer extends Player {

	List<GameStack> stacks;
	int movesPlayed;

	public RandomPlayer(String name) {
		super(name);
	}

	@Override
	public void prepareMove(List<GameStack> stacks) {
		this.stacks = stacks;
		movesPlayed = 0;
	}

	@Override
	public Move getMove() {
		if (movesPlayed >= cardsPerMove) {
			return null;
		}
		Random random = new Random();
		Move move = null;
		boolean found = false;
		int tries = 0;
		int max = hand.size * stacks.size();
		while (!found && tries < max) {
			tries++;
			move = new Game.Move(stacks.get(random.nextInt(stacks.size())),
					hand.cards.get(random.nextInt(hand.cards.size())));
			if (move.isValid()) {
				debug(1, "Move " + move + " is valid.");
				found = true;
			}
		}
		movesPlayed++;
		if (found) {
			return move;
		} else {
			return null;
		}
	}

}
