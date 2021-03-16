package games.terraformingmars.rules;

import games.terraformingmars.TMTypes;
import games.terraformingmars.components.TMCard;

import java.util.Objects;

public class TagOnCardRequirement implements Requirement<TMCard> {

    final TMTypes.Tag tag;

    public TagOnCardRequirement(TMTypes.Tag t) {
        this.tag = t;
    }

    @Override
    public boolean testCondition(TMCard card) {
        if (card == null) return false;
        for (TMTypes.Tag t: card.tags) {
            if (t == tag) return true;
        }
        return false;
    }

    public TagOnCardRequirement copy() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagOnCardRequirement)) return false;
        TagOnCardRequirement that = (TagOnCardRequirement) o;
        return tag == that.tag;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }
}