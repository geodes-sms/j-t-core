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
 * @author Sebastien Ehouan
 * @version 0.5
 * @since 2017-12-08
 * @author An Li
 */
public class RuleFactory {

    /**
     * Create an ARule.
     * 
     * @param name
     * @param preconditionPattern
     * @param nacs
     * @param postconditionPattern
     * @param withResolver
     * @param useVF2
     * @return
     */
    @NotNull
    public static ARule createARule(String name, Pattern preconditionPattern, ArrayList<Pattern> nacs, Pattern postconditionPattern, boolean withResolver, boolean useVF2) {
        LHS lhs = new LHS(preconditionPattern, nacs);
        RHS rhs = new RHS(lhs, postconditionPattern);

        return new ARule(name, lhs, rhs, withResolver, useVF2);
    }

    /**
     * Create a BRule.
     * 
     * @param branches
     * @return
     */
    @NotNull
    public static BRule createBRule(ArrayList<ARule> branches) {
        return new BRule(branches);
    }

    /**
     * Create a BSRule.
     * 
     * @param branches
     * @return
     */
    @NotNull
    public static BSRule createBSRule(ArrayList<ARule> branches) {
        return new BSRule(branches);
    }

    /**
     * Create an FRule.
     * 
     * @param name
     * @param preconditionPattern
     * @param nacs
     * @param postconditionPattern
     * @param withResolver
     * @param useVF2
     * @return
     */
    @NotNull
    public static FRule createFRule(String name, Pattern preconditionPattern, ArrayList<Pattern> nacs, Pattern postconditionPattern, boolean withResolver, boolean useVF2) {
        LHS lhs = new LHS(preconditionPattern, nacs);
        RHS rhs = new RHS(lhs, postconditionPattern);

        return new FRule(name, lhs, rhs, withResolver, useVF2);
    }

    // TODO: 2017-12-08 Not implemented (see class).
    /**
     * Create a LFRule.
     * 
     * @return
     */
    @Contract(" -> fail")
    @NotNull
    public static LFRule createLFRule() {
        return null;
    }

    /**
     * Create a LQSRule.
     * 
     * @return
     */
    // TODO: 2017-12-08 Not implemented (see class).
    @Contract(" -> fail")
    @NotNull
    public static LQSRule createLQSRule() {
        return null;
    }

    /**
     * Create a LRule.
     * 
     * @param name
     * @param innerRule
     * @param preConditionPattern
     * @param nacs
     * @param useVF2
     * @return
     */
    @NotNull
    public static LRule createLRule(String name, Composer innerRule, Pattern preConditionPattern, ArrayList<Pattern> nacs, boolean useVF2) {
        LHS lhs = new LHS(preConditionPattern, nacs);

        return new LRule(name, innerRule, lhs, useVF2);
    }

    /**
     * Create a LSRule.
     * 
     * @return
     */
    // TODO: 2017-12-08 Not implemented (see class).
    @Contract(" -> fail")
    @NotNull
    public static LSRule createLSRule() {
        return null;
    }

    /**
     * Create a QRule.
     * 
     * Method renamed from createQuery to createQRule.
     * 
     * @param name
     * @param preConditionPattern
     * @param nacs
     * @param useVF2
     * @return
     */
    @NotNull
    public static QRule createQRule(String name, Pattern preConditionPattern, ArrayList<Pattern> nacs, boolean useVF2) {
        LHS lhs = new LHS(preConditionPattern, nacs);

        return new QRule(name, lhs, useVF2);
    }

    /**
     * Create a Sequence.
     * 
     * @param name
     * @param rules
     * @return
     */
    @NotNull
    public static Sequence createSequence(String name, ArrayList<Composer> rules) {
        return new Sequence(name, rules);
    }

    /**
     * Create a SRule.
     * 
     * @param name
     * @param preconditionPattern
     * @param nacs
     * @param postconditionPattern
     * @param withResolver
     * @param useVF2
     * @return
     */
    @NotNull
    public static SRule createSRule(String name, Pattern preconditionPattern, ArrayList<Pattern> nacs, Pattern postconditionPattern, boolean withResolver, boolean useVF2) {
        LHS lhs = new LHS(preconditionPattern, nacs);
        RHS rhs = new RHS(lhs, postconditionPattern);

        return new SRule(name, lhs, rhs, withResolver, useVF2);
    }

    /**
     * Create a XFRule.
     * 
     * @param name
     * @param preconditionPattern
     * @param nacs
     * @param postconditionPattern
     * @param withResolver
     * @param useVF2
     * @return
     */
    @NotNull
    public static XFRule createXFRule(String name, Pattern preconditionPattern, ArrayList<Pattern> nacs, Pattern postconditionPattern, boolean withResolver, boolean useVF2) {
        LHS lhs = new LHS(preconditionPattern, nacs);
        RHS rhs = new RHS(lhs, postconditionPattern);

        return new XFRule(name, lhs, rhs, withResolver, useVF2);
    }

    /**
     * Create a XRule.
     * 
     * @param name
     * @param preconditionPattern
     * @param nacs
     * @param postconditionPattern
     * @param withResolver
     * @param useVF2
     * @return
     */
    @NotNull
    public static XRule createXRule(String name, Pattern preconditionPattern, ArrayList<Pattern> nacs, Pattern postconditionPattern, boolean withResolver, boolean useVF2) {
        LHS lhs = new LHS(preconditionPattern, nacs);
        RHS rhs = new RHS(lhs, postconditionPattern);

        return new XRule(name, lhs, rhs, withResolver, useVF2);
    }

    /**
     * Create a XSRule.
     * 
     * @param name
     * @param preconditionPattern
     * @param nacs
     * @param postconditionPattern
     * @param withResolver
     * @param useVF2
     * @return
     */
    @NotNull
    public static XSRule createXSRule(String name, Pattern preconditionPattern, ArrayList<Pattern> nacs, Pattern postconditionPattern, boolean withResolver, boolean useVF2) {
        LHS lhs = new LHS(preconditionPattern, nacs);
        RHS rhs = new RHS(lhs, postconditionPattern);

        return new XSRule(name, lhs, rhs, withResolver, useVF2);
    }
}
