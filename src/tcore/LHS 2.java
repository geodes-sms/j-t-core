package tcore;

import java.util.ArrayList;

/**
 * The left hand side for a rule. <br>
 * Comprised of a pre-condition pattern and optionnaly a list of negative application conditions (NACs).
 *
 * @author Pierre-Olivier Talbot
 * @version 0.5
 * @since 2017-12-08
 */
public class LHS {

    /**
     * The pre-condition pattern to positively match.
     */
    private Pattern preconditionPattern;

    /**
     * A list of negative application conditions to negatively match.
     */
    private ArrayList<Pattern> nacs;

    public LHS(Pattern preconditionPattern, ArrayList<Pattern> nacs) {
        this.preconditionPattern = preconditionPattern;
        this.nacs = nacs;
    }

    public Pattern getPreconditionPattern() {
        return preconditionPattern;
    }

    public void setPreconditionPattern(Pattern preconditionPattern) {
        this.preconditionPattern = preconditionPattern;
    }

    public ArrayList<Pattern> getNacs() {
        return nacs;
    }

    public void setNacs(ArrayList<Pattern> nacs) {
        this.nacs = nacs;
    }
}
