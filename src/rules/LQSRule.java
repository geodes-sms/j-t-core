package rules;

import tcore.Composer;
import tcore.LHS;

/**
 * Applies an inner rule for each match of the LHS as long as matches can be found.
 * // FIXME: 2017-12-13 Does not work with current "random" iterator...
 *
 * @author Pierre-Olivier Talbot
 * @author An Li
 */

public class LQSRule extends LRule {
    /**
     * @param name
     * @param innerRule
     * @param lhs
     */
    public LQSRule(String name, Composer innerRule, LHS lhs, boolean useVF2) {
        super(name, innerRule, lhs, useVF2);
    }
}
