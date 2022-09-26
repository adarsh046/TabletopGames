package games.cantstop.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import games.cantstop.CantStopGameState;

public class RollDice extends AbstractAction {
    @Override
    public boolean execute(AbstractGameState gs) {
        CantStopGameState state = (CantStopGameState) gs;
        state.rollDice();
        return true;
    }

    @Override
    public AbstractAction copy() {
        return this; // immutable
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RollDice;
    }

    @Override
    public int hashCode() {
        return 929134894;
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Roll Dice";
    }
}
