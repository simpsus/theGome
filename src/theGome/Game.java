package theGome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Game {

	public static class Card implements Comparable {

		int number;

		public Card(int number) {
			this.number = number;
		}

		@Override
		public int compareTo(Object o) {
			return new Integer(number).compareTo(((Card) o).number);
		}

		public boolean isBigger(Card c) {
			return compareTo(c) > 0;
		}

		public String toString() {
			return "" + number;
		}

		public int difference(Card c) {
			return number - c.number;
		}
	}

	public class Deck {

		Stack<Card> base;
		List<Card> shuffle;

		Deck() {
			base = new Stack<Card>();
			shuffle = new ArrayList<Card>();
			for (int i = 2; i <= 99; i++) {
				shuffle.add(new Card(i));
			}
			Collections.shuffle(shuffle);
		}

		Hand deal(int size) {
			Hand hand = new Hand(size);
			for (int i = 0; i < size; i++) {
				hand.receive(shuffle.remove(0));
			}
			return hand;
		}

		void prepare() {
			Collections.reverse(shuffle);
			while (!shuffle.isEmpty()) {
				base.push(shuffle.remove(0));
			}
		}

		Card draw() {
			return base.pop();
		}

		boolean hasMore() {
			return base.size() > 0;
		}

		int size() {
			return base.size();
		}

		String getBaseString() {
			String result = "[";
			for (Card card : base) {
				result += card + ",";
			}
			return result.substring(0, result.length() - 1) + "]";
		}

	}

	static class GameStack {

		enum DIRECTION {
			UP, DOWN
		}

		DIRECTION direction;
		Stack<Card> cards;

		GameStack(DIRECTION direction) {
			this.direction = direction;
			cards = new Stack<Card>();
		}

		Card peek() {
			if (cards.size() > 0) {
				return cards.peek();
			}
			if (direction == DIRECTION.UP)
				return Game.ONE;
			return Game.HUNDRET;
		}

		int size() {
			return cards.size();
		}

		void accept(Card c) {
			cards.push(c);
		}

		int getValue() {
			if (cards.size() == 0) {
				if (direction == DIRECTION.DOWN) {
					return 100;
				} else {
					return 1;
				}
			}
			return cards.peek().number;
		}

		boolean isValidMove(Card c) {
			if (cards.size() == 0) {
				return true;
			}
			int value = getValue();
			if (direction == DIRECTION.DOWN) {
				if (c.number < value) {
					return true;
				} else if (value + 10 == c.number) {
					return true;
				}
			} else {
				if (c.number > value) {
					return true;
				} else if (c.number + 10 == value) {
					return true;
				}
			}
			return false;
		}

		public String toString() {
			return "[" + getValue() + "]" + direction;
		}

		String getHistory() {
			String result = "";
			if (direction == DIRECTION.DOWN) {
				result += "100->";
			} else {
				result += "1->";
			}
			for (Card card : cards) {
				result += card + "->";
			}
			return result.substring(0, result.length() - 2);
		}
	}

	public static class Move {

		GameStack stack;
		Card card;

		public Move(GameStack stack, Card card) {
			super();
			this.stack = stack;
			this.card = card;
		}

		public boolean isValid() {
			return stack.isValidMove(card);
		}

		public void execute() {
			stack.accept(card);
		}

		public String toString() {
			return card.toString() + " -> " + stack;
		}

	}

	public class Hand {

		List<Card> cards = new ArrayList<Card>();
		int size;

		public Hand(int size) {
			this.size = size;
		}

		void receive(Card card) {
			cards.add(card);
			Collections.sort(cards);
		}

		boolean isEmpty() {
			return cards.size() == 0;
		}

		void remove(Card card) {
			cards.remove(card);
		}

		public String toString() {
			String result = "[ ";
			for (Card c : cards) {
				result += c + " ";
			}
			result += "]";
			return result;
		}
	}

	enum GAMEOVERREASON {
		invalidMove, won, noMoreMoves
	}

	static class GameResult {

		Game game;
		boolean won;
		int playedMoves;
		int remainingCards;

		GameResult(Game game) {
			this.game = game;
			won = (game.reason == GAMEOVERREASON.won);
			playedMoves = game.playedMoves;
			remainingCards = game.totalCardsLeft;
		}

	}

	static class GameStatistics {

		Set<GameResult> results;

		GameStatistics() {
			results = new HashSet<GameResult>();
		}

		void addResult(GameResult result) {
			results.add(result);
		}

		int getCountOfWonGames() {
			int result = 0;
			for (GameResult res : results) {
				if (res.won) {
					result++;
				}
			}
			return result;
		}

		float getAverageRemainingCards() {
			int totalLeft = 0;
			for (GameResult res : results) {
				totalLeft += res.remainingCards;
			}
			return (float) totalLeft / results.size();
		}

	}

	static Card HUNDRET = new Card(100);
	static Card ONE = new Card(1);
	Deck deck;
	int cardsPerPlayer;
	int cardsPerMove;
	List<Player> players;
	List<GameStack> stacks = new ArrayList<GameStack>();
	int debug_lvl;
	GAMEOVERREASON reason;
	int playedMoves, totalCardsLeft;

	Game(int cardsPerPlayer, int cardsPerMove) {
		this.cardsPerPlayer = cardsPerPlayer;
		this.cardsPerMove = cardsPerMove;
		deck = new Deck();
		players = new ArrayList<Player>();
		boolean flick = true;
		for (int i = 0; i < 4; i++) {
			if (flick) {
				stacks.add(new GameStack(GameStack.DIRECTION.UP));
			} else {
				stacks.add(new GameStack(GameStack.DIRECTION.DOWN));
			}
			flick = !flick;
		}
	}

	void addPlayer(Player player) {
		Hand hand = deck.deal(cardsPerPlayer);
		player.setHand(hand);
		player.setCardsPerMove(cardsPerMove);
		players.add(player);
	}

	void start(int debug) {
		this.debug_lvl = debug;
		debug(1, "Starting _The Game_");
		debug(0, "#Players " + players.size());
		debug(0, "#CardsPerPlayer " + cardsPerPlayer);
		debug(0, "#CardsPerMove " + cardsPerMove);
		for (Player p : players) {
			debug(1, p.toString());
		}
		deck.prepare();
		playLoop();
	}

	void debug(int level, String message) {
		if (level >= debug_lvl) {
			System.out.println(message);
		}
	}

	void playLoop() {
		boolean gameOver = false;
		boolean deckEmpty = false;
		playedMoves = 0;
		debug(2, "Starting Main Loop");
		while (!gameOver) {
			boolean allPlayersBlank = true;
			for (Player p : players) {
				p.prepareMove(stacks);
				debug(1, "Prepared " + p);
				int moves = 0;
				if (p.hasCards()) {
					allPlayersBlank = false;
					for (int i = 0; i < cardsPerPlayer; i++) {
						debug(1, "Polling Player for Move " + p);
						Move m = p.getMove();
						debug(1, "Received Move " + m);
						if (m == null) {
							if (i < cardsPerMove) {
								gameOver = true;
								reason = GAMEOVERREASON.noMoreMoves;
							}
							break;
						} else if (!m.isValid()) {
							debug(5, "INVALID MOVE! " + m);
							reason = GAMEOVERREASON.invalidMove;
							gameOver = true;
						} else {
							String message = "Move #" + (++playedMoves) + ": "
									+ m + " ==> ";
							m.execute();
							p.hand.remove(m.card);
							debug(3, message + stacks);
							// debug(2, "Executed move " + m + ". " + p);
							moves++;
						}
						if (gameOver) {
							break;
						}
					}
					if (gameOver) {
						break;
					}
					if (!deckEmpty) {
						for (int i = 0; i < moves; i++) {
							Card c = deck.draw();
							debug(2, "Dealing Card " + c);
							p.acceptCard(c);
							if (!deck.hasMore()) {
								deckEmpty = true;
								processEmptyDeck();
								break;
							}
						}
					}
				}
			}
			if (allPlayersBlank) {
				reason = GAMEOVERREASON.won;
				gameOver = true;
			}
		}
		debug(3, "Game Over. Reason: " + reason);
		for (GameStack stack : stacks) {
			debug(3,
					stack.direction + "[" + stack.size() + "] "
							+ stack.getHistory());
		}
		if (reason == GAMEOVERREASON.noMoreMoves) {
			totalCardsLeft = 0;
			totalCardsLeft += deck.size();
			if (deck.size() > 0) {
				debug(3, "Deck: " + deck.getBaseString());
			} else {
				debug(3, "Deck empty");
			}
			for (Player p : players) {
				totalCardsLeft += p.hand.size;
				debug(3, "" + p);
			}
			debug(3, "Total Cards Left: " + totalCardsLeft);
		}
	}

	void processEmptyDeck() {
		cardsPerMove = 1;
		for (Player player : players) {
			player.setCardsPerMove(cardsPerMove);
		}
	}

	public static void main(String[] args) {
		int games = 1000;
		GameStatistics stats = new GameStatistics();
		for (int i = 0; i < games; i++) {
			Game game = new Game(6, 2);
			game.addPlayer(new GreedyActionPlayer("Simulator"));
			game.start(5);
			stats.addResult(new GameResult(game));
		}
		System.out.println("Games Won: " + stats.getCountOfWonGames());
		System.out.println("Average Cards Left "
				+ stats.getAverageRemainingCards());
	}

}
