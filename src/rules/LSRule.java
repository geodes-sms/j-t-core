package rules;

import tcore.Composer;
import tcore.LHS;

/**
 * Applies an inner rule for each application of the outer rule as long as matches can be found.
 * // FIXME: 2017-12-13 Does not work with current "random" iterator...
 *
 * @author Pierre-Olivier Talbot
 * @author An Li
 */

public class LSRule extends LRule {

    /**
     * @param name
     * @param innerRule
     * @param lhs
     */
    public LSRule(String name, Composer innerRule, LHS lhs, String nameAlgo) {
        super(name, innerRule, lhs, nameAlgo);
    }
}