package theGome.one_turn_optimal;

import theGome.Game;
import theGome.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by fbarth on 11/08/16.
 */
public class OneTurnSearchPlayer extends Player {
    private List<Game.GameStack> stacks;
    /*
    Hand hand;
	int cardsPerMove;
     */


    public OneTurnSearchPlayer(String name) {
        super(name);
    }

    List<WorldState.Move> myMoves = new ArrayList<>();


    @Override
    public void prepareMove(List<Game.GameStack> stacks, int remainingCardsCount) {

        if (myMoves.size() > 0)
            throw new RuntimeException("could not execute all my moves");

        this.stacks = stacks;
        WorldState currentState = new WorldState(stacks, this.hand);

        log("Got new world state (decksize: "+remainingCardsCount+"): " + currentState);

        int minDrops = (remainingCardsCount > 0)?cardsPerMove:1;

        final int finalMinDrops = minDrops;
        List<WorldState> moves = generateAllCardPlacements(currentState)
                .stream()
                .filter(move -> move.getHistory().size() >= finalMinDrops)
                .collect(Collectors.toList());

        int minValue = Integer.MAX_VALUE;
        WorldState best = null;
        for (WorldState move : moves) {
            int value = evalBoard(move);
            if (value < minValue) {
                minValue = value;
                best = move;
            }
        }
        if (best != null) {
            log("best: " + minValue + " - " + best);
            myMoves = best.getHistory();
        }else {
            log("no turn, Game Over, Mr Anderson!");
            myMoves = new ArrayList<>();
        }


    }

    private void log(String s) {
        System.out.println(this.name+": "+s);
    }

    private List<WorldState> generateValidCardPlacements(WorldState currentState) {
        List<WorldState> futureStates = new ArrayList<>();
        for (Integer cardValue : currentState.getHand()) {
            for (int stackId = 0; stackId < currentState.getStacks().size(); stackId++) {
                WorldState.GameStack stack = currentState.getStacks().get(stackId);
                if (cardValue < stack.getCurrentValue() && stack.getDirection() == Game.GameStack.DIRECTION.DOWN)
                    futureStates.add(currentState.generateNewWorldState(cardValue, stackId));
                if (cardValue > stack.getCurrentValue() && stack.getDirection() == Game.GameStack.DIRECTION.UP) {
                    futureStates.add(currentState.generateNewWorldState(cardValue, stackId));
                }
                if (cardValue == (stack.getCurrentValue() + 10) && stack.getDirection() == Game.GameStack.DIRECTION.DOWN) {
                    futureStates.add(currentState.generateNewWorldState(cardValue, stackId));
                }
                if (cardValue == (stack.getCurrentValue() - 10) && stack.getDirection() == Game.GameStack.DIRECTION.UP) {
                    futureStates.add(currentState.generateNewWorldState(cardValue, stackId));
                }

            }
        }

        return futureStates;
    }


    private List<WorldState> generateAllCardPlacements(WorldState currentState) {
        List<WorldState> placements = generateValidCardPlacements(currentState);
        if (placements.size() == 0) return Collections.singletonList(currentState);

        List<WorldState> result = new ArrayList<>();
        for (WorldState worldState : placements) {
            result.addAll(generateAllCardPlacements(worldState));
        }
        result.add(currentState);
        return result;
    }

    @Override
    public Game.Move getMove() {
        if (myMoves.isEmpty()) return null;
        WorldState.Move move = myMoves.get(0);
        myMoves.remove(0);


        Game.GameStack stack = stacks.get(move.getStackId());

        Game.Card card = hand.getCards().stream()
                .filter(gameCard -> gameCard.getNumber() == move.getCardValue())
                .findFirst()
                .get();

        if (card == null)
            throw new RuntimeException("did not find card to play");


        Game.Move gameMove = new Game.Move(stack, card);

        return gameMove;

    }

    private int evalBoard(WorldState state) {
        int value = 0;
        for (WorldState.GameStack gameStack : state.getStacks()) {
            if (gameStack.getDirection() == Game.GameStack.DIRECTION.UP) {
                value += gameStack.getCurrentValue() - 1;
            }
            if (gameStack.getDirection() == Game.GameStack.DIRECTION.DOWN) {
                value += 100 - gameStack.getCurrentValue();
            }
        }
        return value;

    }

}
