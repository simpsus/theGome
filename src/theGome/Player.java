package theGome;

import java.util.List;

import theGome.Game.Card;
import theGome.Game.GameStack;
import theGome.Game.Hand;
import theGome.Game.Move;

public abstract class Player {

	protected Hand hand;
	protected int cardsPerMove;
	protected String name;
	int debug_lvl = 6;

	void debug(int level, String message) {
		if (level >= debug_lvl) {
			System.out.println(name+ ":" +message);
		}
	}

	protected void debug(String message){
		debug(1, message);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCardsPerMove() {
		return cardsPerMove;
	}

	public void setCardsPerMove(int cardsPerMove) {
		this.cardsPerMove = cardsPerMove;
	}

	public Player(String name) {
		this.name = name;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public String toString() {
		return name + hand;
	}

	public boolean hasCards() {
		return !hand.isEmpty();
	}

	public void acceptCard(Card card) {
		hand.receive(card);
	}

	public abstract void prepareMove(List<GameStack> stacks, int remainingCardsInDeck);

	public abstract Move getMove();

}
