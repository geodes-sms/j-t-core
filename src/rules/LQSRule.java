package rules;

import tcore.Composer;
import tcore.LHS;

/**
 * Applies an inner rule for each match of the LHS as long as matches can be found.
 * // FIXME: 2017-12-13 Does not work with current "random" iterator...
 *
 * @author Pierre-Olivier Talbot
 */
public class LQSRule extends LRule {
    public LQSRule(String name, Composer innerRule, LHS lhs) {
        super(name, innerRule, lhs);
    }
}
