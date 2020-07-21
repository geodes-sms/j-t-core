package rules;

import tcore.*;
import tcore.strategy.Matcher;

/**
 * @author Pierre-Olivier Talbot
 * @version 0.5
 * @since 2017-12-08
 */
public abstract class BasicRule extends Composer {

    protected String name;
    protected LHS lhs;
    protected RHS rhs;
    protected Matcher matcher;
    protected Iterator iterator;
    protected Rewriter rewriter;
    protected Resolver resolver;

    public String getName() {
        return name;
    }

    public LHS getLhs() {
        return lhs;
    }

    public RHS getRhs() {
        return rhs;
    }
}
