package theGome;

import java.util.List;

import theGome.Game.Card;
import theGome.Game.GameStack;
import theGome.Game.GameStack.DIRECTION;
import theGome.Game.Move;

public class GreedyActionPlayer extends Player {

	Move nextMove;
	List<GameStack> stacks;
	int counter;

	public GreedyActionPlayer(String name) {
		super(name);
		debug_lvl = 2;
	}

	@Override
	public void prepareMove(List<GameStack> stacks) {
		this.stacks = stacks;
		counter = 0;
	}

	void debug(int level, String message) {
		if (level >= debug_lvl) {
			System.out.println(name + ": " + message);
		}
	}

	void prepareNextMove() {
		// Is there a -10 Card?
		for (Card card : hand.cards) {
			for (GameStack stack : stacks) {
				if (stack.direction == DIRECTION.DOWN) {
					if (card.number == stack.getValue() + 10) {
						Move move = new Move(stack, card);
						debug(1, "discovered Move " + move);
						nextMove = move;
						return;
					} else if (card.number == stack.getValue() - 10) {
						Move move = new Move(stack, card);
						debug(1, "discovered Move " + move);
						nextMove = move;
						return;
					}
				}
			}
		}
		// Play the card with the lowest Delta
		Card c = null;
		GameStack s = null;
		int best = 101;
		for (Card card : hand.cards) {
			for (GameStack stack : stacks) {
				int current;
				if (stack.direction == DIRECTION.DOWN) {
					current = stack.getValue() - card.number;
				} else {
					current = card.number - stack.getValue();
				}
				if (current > 0 && current < best) {
					best = current;
					c = card;
					s = stack;
				}
			}
		}
		if (best < 101) {
			Move move = new Move(s, c);
			debug(1, "discovered Move " + move);
			nextMove = move;
		}
	}

	@Override
	public Move getMove() {
		if (counter == cardsPerMove) {
			return null;
		}
		nextMove = null;
		prepareNextMove();
		debug(1, "chosing Move " + nextMove);
		counter++;
		return nextMove;
	}

}
