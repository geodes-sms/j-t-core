package tcore;


import org.eclipse.emf.ecore.EObject;
import tcore.messages.Match;
import tcore.messages.Packet;

import java.util.ArrayList;
import java.util.InputMismatchException;

public class Rewriter extends RulePrimitive {

    private RHS rhs;

    public Rewriter(RHS rhs) {
        super();
        this.rhs = rhs;
    }


    @Override
    public Packet packetIn(Packet p) {
        if (p.getCurrentMatchSet().getLhs() != rhs.getLhs()) {
            throw new InputMismatchException("The RHS doesn't correspond to the correct LHS.");
        }
        Model model = p.getModel();
        try {
            rhs.execute(p.getCurrentMatchSet().getMatchToRewrite());
        } catch (Exception e) {
            e.printStackTrace();
        }

        p.getModel().actualizeObjects();
        ArrayList<EObject> dirtyNodes = new ArrayList<>(p.getCurrentMatchSet().getMatchToRewrite().getLabelMappings().values());
        for (Match m : p.getCurrentMatchSet().getMatches()) {
            for (EObject o : dirtyNodes) {
                m.setNodeToDirty(o);
            }
        }

        p.getCurrentMatchSet().getMatches().remove(p.getCurrentMatchSet().getMatchToRewrite());
        isSuccess = true;
        exception = null;
        return p;
    }
}
