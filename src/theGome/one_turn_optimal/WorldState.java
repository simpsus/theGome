package theGome.one_turn_optimal;

import theGome.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fbarth on 11/08/16.
 */
public class WorldState {
    List<GameStack> stacks = new ArrayList<>(4);
    List<Integer> hand;
    List<Move> history;

    public WorldState(List<Game.GameStack> stacks, Game.Hand hand) {
        this.stacks = stacks.stream()
                .map(stack -> new GameStack(stack.getDirection(), stack.peek().getNumber()))
                .collect(Collectors.toList());

        this.hand = hand.getCards().stream()
                .map(Game.Card::getNumber)
                .collect(Collectors.toList());
        this.history = new ArrayList<>();
    }

    public WorldState(List<GameStack> newStacks, List<Integer> newHand, List<Move> history) {
        this.stacks = newStacks;
        this.hand = newHand;
        this.history = history;
    }


    public List<GameStack> getStacks() {
        return stacks;
    }

    public List<Integer> getHand() {
        return hand;
    }

    public List<Move> getHistory() {
        return history;
    }

    public WorldState generateNewWorldState(Integer cardValue, int stackId) {
        List<GameStack> newStacks = new ArrayList<>(this.stacks);
        newStacks.set(stackId, new GameStack(newStacks.get(stackId).getDirection(), cardValue));

        List<Integer> newHand = new ArrayList<>(this.hand);
        newHand.remove(cardValue);


        Move move = new Move(stackId, cardValue);
        ArrayList<Move> newHistory = new ArrayList<>(history);
        newHistory.add(move);

        return new WorldState(newStacks, newHand, newHistory);
    }


    public static class Move {
        int stackId;
        int cardValue;

        public Move(int stackId, int cardValue) {
            this.stackId = stackId;
            this.cardValue = cardValue;
        }

        public int getStackId() {
            return stackId;
        }

        public int getCardValue() {
            return cardValue;
        }

        @Override
        public String toString() {
            return "Move{" +
                    "sid=" + stackId +
                    ", v=" + cardValue +
                    '}';
        }
    }

    public static class GameStack {
        Game.GameStack.DIRECTION direction;
        int currentValue;

        public GameStack(Game.GameStack.DIRECTION direction, int currentValue) {
            this.direction = direction;
            this.currentValue = currentValue;
        }

        public Game.GameStack.DIRECTION getDirection() {
            return direction;
        }

        public int getCurrentValue() {
            return currentValue;
        }

        @Override
        public String toString() {
            return "[" + currentValue + "]" + direction;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "stacks=" + stacks +
                ", hand=" + hand +
                ", history=" + history +
                '}';
    }
}
