package rules;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import tcore.Composer;
import tcore.LHS;
import tcore.Pattern;
import tcore.RHS;

import java.util.ArrayList;

/**
 * @author Pierre-Olivier Talbot
 * @version 0.5
 * @since 2017-12-08
 */
public class RuleFactory {

    @NotNull
    public static ARule createARule(String name, Pattern preconditionPattern, ArrayList<Pattern> nacs, Pattern postconditionPattern, boolean withResolver) {
        LHS lhs = new LHS(preconditionPattern, nacs);
        RHS rhs = new RHS(lhs, postconditionPattern);

        return new ARule(name, lhs, rhs, withResolver);
    }

    @NotNull
    public static BRule createBRule(ArrayList<ARule> branches) {
        return new BRule(branches);
    }

    @NotNull
    public static BSRule createBSRule(ArrayList<ARule> branches) {
        return new BSRule(branches);
    }

    @NotNull
    public static FRule createFRule(String name, Pattern preconditionPattern, ArrayList<Pattern> nacs, Pattern postconditionPattern, boolean withResolver) {
        LHS lhs = new LHS(preconditionPattern, nacs);
        RHS rhs = new RHS(lhs, postconditionPattern);

        return new FRule(name, lhs, rhs, withResolver);
    }

    // TODO: 2017-12-08 Not implemented (see class).
    @Contract(" -> fail")
    @NotNull
    public static LFRule createLFRule() {
        return null;
    }

    // TODO: 2017-12-08 Not implemented (see class).
    @Contract(" -> fail")
    @NotNull
    public static LQSRule createLQSRule() {
        return null;
    }

    @NotNull
    public static LRule createLRule(String name, Composer innerRule, Pattern preConditionPattern, ArrayList<Pattern> nacs) {
        LHS lhs = new LHS(preConditionPattern, nacs);

        return new LRule(name, innerRule, lhs);
    }

    // TODO: 2017-12-08 Not implemented (see class).
    @Contract(" -> fail")
    @NotNull
    public static LSRule createLSRule() {
        return null;
    }

    @NotNull
    public static Query createQuery(String name, Pattern preConditionPattern, ArrayList<Pattern> nacs) {
        LHS lhs = new LHS(preConditionPattern, nacs);

        return new Query(name, lhs);
    }

    @NotNull
    public static Sequence createSequence(String name, ArrayList<Composer> rules) {
        return new Sequence(name, rules);
    }

    @NotNull
    public static SRule createSRule(String name, Pattern preconditionPattern, ArrayList<Pattern> nacs, Pattern postconditionPattern, boolean withResolver) {
        LHS lhs = new LHS(preconditionPattern, nacs);
        RHS rhs = new RHS(lhs, postconditionPattern);

        return new SRule(name, lhs, rhs, withResolver);
    }

    @NotNull
    public static XFRule createXFRule(String name, Pattern preconditionPattern, ArrayList<Pattern> nacs, Pattern postconditionPattern, boolean withResolver) {
        LHS lhs = new LHS(preconditionPattern, nacs);
        RHS rhs = new RHS(lhs, postconditionPattern);

        return new XFRule(name, lhs, rhs, withResolver);
    }

    @NotNull
    public static XRule createXRule(String name, Pattern preconditionPattern, ArrayList<Pattern> nacs, Pattern postconditionPattern, boolean withResolver) {
        LHS lhs = new LHS(preconditionPattern, nacs);
        RHS rhs = new RHS(lhs, postconditionPattern);

        return new XRule(name, lhs, rhs, withResolver);
    }

    @NotNull
    public static XSRule createXSRule(String name, Pattern preconditionPattern, ArrayList<Pattern> nacs, Pattern postconditionPattern, boolean withResolver) {
        LHS lhs = new LHS(preconditionPattern, nacs);
        RHS rhs = new RHS(lhs, postconditionPattern);

        return new XSRule(name, lhs, rhs, withResolver);
    }
}
