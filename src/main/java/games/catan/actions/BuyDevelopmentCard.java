package games.catan.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.components.Card;
import core.components.Deck;
import games.catan.CatanConstants;
import games.catan.CatanGameState;
import games.catan.CatanParameters;

public class BuyDevelopmentCard extends AbstractAction {

    @Override
    public boolean execute(AbstractGameState gs) {
        CatanGameState cgs = (CatanGameState)gs;
        if (!CatanGameState.spendResources(cgs, CatanParameters.costMapping.get("developmentCard"))) return false;
        // give a dev card to the player
        Deck<Card> playerDevDeck = (Deck<Card>)cgs.getComponentActingPlayer(CatanConstants.developmentDeckHash);
        Deck<Card> devDeck = (Deck<Card>)cgs.getComponent(CatanConstants.developmentDeckHash);
        Card card = devDeck.pick(0);
        cgs.setBoughtDevCard(card);
        if (card != null) {
            playerDevDeck.add(card);
            //  if card is a Victory Point card and player already has 9 points then use it
            String cardType = card.getProperty(CatanConstants.cardType).toString();
            if (cardType.equals("Victory Point")){
                cgs.addVictoryPoint(cgs.getCurrentPlayer());
            }
            return true;
        }

        return false;
    }

    @Override
    public AbstractAction copy() {
        return new BuyDevelopmentCard();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof BuyDevelopmentCard){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Buy Development Card";
    }
}
