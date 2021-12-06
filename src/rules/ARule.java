package rules;

import tcore.*;
import tcore.messages.Packet;
import tcore.strategy.Matcher;

/**
 * Applies the transformation on a single match.
 *
 * @author Pierre-Olivier Talbot
 * @author An Li
 */
public class ARule extends BasicRule {

    ARule(String name, LHS lhs, RHS rhs, boolean withResolver, boolean useVF2) {
        this.name = name;
        this.lhs = lhs;
        this.rhs = rhs;
        matcher = new Matcher(lhs, 1, useVF2);
        iterator = new Iterator(1);
        rewriter = new Rewriter(rhs);
        if (withResolver) resolver = new Resolver(false, null);
    }

    @Override
    public Packet packetIn(Packet p) {

    	// set match algo in the test class with the factory
    	
        p = matcher.packetIn(p);
        if (checkModuleForFailure(matcher)) return p;

        p = iterator.packetIn(p);
        if (checkModuleForFailure(iterator)) return p;

        p = rewriter.packetIn(p);
        if (checkModuleForFailure(rewriter)) return p;

        p = resolveOrClean(p);

        isSuccess = true;
        return p;
    }

    @Override
    public Packet nextIn(Packet p) {
        return packetIn(p);
    }

    protected Packet resolveOrClean(Packet p) {
        if (resolver != null) {
            p = resolver.packetIn(p);
            if (checkModuleForFailure(rewriter)) return p;
        } else {
            p.clean();
        }
        return p;
    }

}
