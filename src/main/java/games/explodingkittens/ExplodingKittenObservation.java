package games.explodingkittens;

import core.AbstractGameState;
import core.components.Deck;
import core.components.PartialObservableDeck;
import core.interfaces.IPrintable;
import games.explodingkittens.cards.ExplodingKittenCard;
import core.interfaces.IObservation;

import java.util.List;

public class ExplodingKittenObservation implements IPrintable, IObservation {

    private List<PartialObservableDeck<ExplodingKittenCard>> playerHandCards;
    private PartialObservableDeck<ExplodingKittenCard> drawPile;
    private Deck<ExplodingKittenCard> discardPile;
    private int currentPlayer;

    public ExplodingKittenObservation(List<PartialObservableDeck<ExplodingKittenCard>> playerDecks,
                                      PartialObservableDeck<ExplodingKittenCard> drawPile,
                                      Deck<ExplodingKittenCard> discardPile,
                                      int currentPlayer){
        playerHandCards = playerDecks;
        this.drawPile = drawPile;
        this.discardPile = discardPile;
        this.currentPlayer = currentPlayer;
    }

    @Override
    public void printToConsole(AbstractGameState gameState) {
        for (int i = 0; i < playerHandCards.size(); i++){
            if (currentPlayer == i)
                System.out.print(">>> Player " + i + ":");
            else
                System.out.print("Player " + i + ":");
            printDeck(playerHandCards.get(i).getVisibleComponents(currentPlayer));
        }

        System.out.print("DrawPile" + ":");
        printDeck(drawPile.getVisibleComponents(currentPlayer));

        System.out.print("DiscardPile" + ":");
        printDeck(discardPile.getComponents());
    }

    private void printDeck(List<ExplodingKittenCard> cards){
        StringBuilder sb = new StringBuilder();

        for (ExplodingKittenCard card : cards){
            if (card == null)
                sb.append("UNKNOWN");
            else
                sb.append(card.cardType.toString());
            sb.append(",");
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length()-1);
        System.out.println(sb.toString());
    }

}
