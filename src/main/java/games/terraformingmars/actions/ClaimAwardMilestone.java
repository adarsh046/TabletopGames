package games.terraformingmars.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import games.terraformingmars.TMGameState;
import games.terraformingmars.rules.Award;
import games.terraformingmars.rules.Milestone;

import java.util.Objects;

public class ClaimAwardMilestone extends TMAction {
    final Award toClaim;

    public ClaimAwardMilestone(Award toClaim) {
        this.toClaim = toClaim;
    }

    @Override
    public boolean execute(AbstractGameState gs) {
        if (toClaim.claim((TMGameState) gs)) {
            if (toClaim instanceof Milestone) {
                ((TMGameState)gs).getnMilestonesClaimed().increment(1);
            } else {
                ((TMGameState)gs).getnAwardsFunded().increment(1);
            }
            return true;
        }
        return false;
    }

    @Override
    public AbstractAction copy() {
        return new ClaimAwardMilestone(toClaim.copy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClaimAwardMilestone)) return false;
        if (!super.equals(o)) return false;
        ClaimAwardMilestone that = (ClaimAwardMilestone) o;
        return Objects.equals(toClaim, that.toClaim);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), toClaim);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Claim " + toClaim.name;
    }

    @Override
    public String toString() {
        return "Claim " + toClaim.name;
    }
}