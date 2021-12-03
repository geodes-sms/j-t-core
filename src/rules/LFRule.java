package rules;

import tcore.*;

/**
 * Applies an inner rule for each application of the outer rule.
 * // FIXME: 2017-12-13 Does not work with the current "random" iterator...
 *
 * @author Pierre-Olivier Talbot
 */
class LFRule extends LRule {

    @SuppressWarnings("unused")
	private boolean outerFirst;
    
    @SuppressWarnings("unused")
	private Iterator iterator;
    
    @SuppressWarnings("unused")
	private Rewriter rewriter;

    public LFRule(String name, Composer innerRule, LHS lhs, RHS rhs, boolean outerFirst, boolean useVF2) {
        super(name, innerRule, lhs, useVF2);
        this.outerFirst = outerFirst;
        rewriter = new Rewriter(rhs);
        iterator = new Iterator(Integer.MAX_VALUE);
    }
}
