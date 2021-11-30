package tcore.strategy;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.jetbrains.annotations.NotNull;

import tcore.LHS;
import tcore.Model;
import tcore.Pattern;
import tcore.RulePrimitive;
import tcore.messages.Match;
import tcore.messages.MatchSet;
import tcore.messages.Packet;
import utils.Utils;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * T-Core primitive meant for matching a {@link LHS} with a given {@link Model}.
 *
 * @author Pierre-Olivier Talbot
 * @author Sebastien EHouan
 * @since 2017-12-01
 */

public class SimpleMatch extends RulePrimitive implements IMatchAlgo {
    /**
     * Maximum number of matches authorized.
     */
    private int max;

    /**
     * The left hand side to match.
     */
    private LHS lhs;

    /**
     * The model in which to find a match.
     */
    private Model model;

    /**
     * EMF label attribute.
     */
    private EStructuralFeature label;

    /**
     * @param lhs
     * @param max
     * @param model
     */
    public SimpleMatch(LHS lhs, int max, Model model) {
        super();
        if (max <= 0) {
            throw new IllegalArgumentException("Matcher's maximum number of iterations must be greater than 0.");
        }
        this.max = max;
        this.lhs = lhs;
        this.model = model;
    }


    /**
     * Tries to match the {@link LHS} to the {@link Model}.
     * FIXME: 2017-12-08 The algorithm finds all the matches and filters out the submatches afterwards. Innefficient and costly.
     *
     * @return A list of matches.
     */
    @Override
	public ArrayList<Match> match() {

        Pattern pattern = lhs.getPreconditionPattern();
        ArrayList<Pattern> nacs = (lhs.getNacs() == null) ? new ArrayList<>() : lhs.getNacs();

        ArrayList<Match> results = new ArrayList<>();

        ArrayList<EObject> modelObjects = model.getObjects();
        EObject patternRoot = pattern.getRootObject();
        label = patternRoot.eClass().getEStructuralFeature(Utils.MT_LABEL);

        for (EObject o : modelObjects) {
            if (objectEquals(o, patternRoot)) {
                Match submatch = new Match(patternRoot.eGet(label).toString(), o);
                results.addAll(extendMatch(patternRoot, patternRoot.eGet(label).toString(), submatch));
            }
        }

        // Removing algorithm by-products... (main algorithm)
        ArrayList<Match> toRemove = new ArrayList<>();
        for (Match m : results) {
            if (m.getLabelMappings().size() != pattern.getLabels().size()) {
                toRemove.add(m);
            }
        }
        
        for (Match mtr: toRemove) {
        	if (results.contains(mtr)) {
            	results.remove(mtr);
        	}
        }
//        results.removeAll(toRemove);

        ArrayList<Match> finalMatches = new ArrayList<>();
        for (Match m : results) {
            if (!finalMatches.contains(m)) {
                finalMatches.add(m);
            }
        }
        results = finalMatches;

        // Bound NAC processing
        ArrayList<Match> matchedNACS = new ArrayList<>();
        for (Pattern nac : nacs) {
            EObject NACRoot = nac.getRootObject();

            for (EObject o : modelObjects) {
                if (objectEquals(o, NACRoot)) {
                    Match submatch = new Match(NACRoot.eGet(label).toString(), o);
                    matchedNACS.addAll(extendMatch(NACRoot, NACRoot.eGet(label).toString(), submatch));
                }
            }

            // Removing matching by-products... (bound NAC processing)
            ArrayList<Match> NACStoRemove = new ArrayList<>();
            for (Match m : matchedNACS) {
                if (m.getLabelMappings().size() != nac.getLabels().size()) {
                    NACStoRemove.add(m);
                }
            }
            matchedNACS.removeAll(NACStoRemove);

            ArrayList<Match> finalNACMatches = new ArrayList<>();
            for (Match m : matchedNACS) {
                if (!finalNACMatches.contains(m)) {
                    finalNACMatches.add(m);
                }
            }
            matchedNACS = finalNACMatches;
        }

        toRemove = new ArrayList<>();
        for (Match m : results) {
            boolean keep = true;
            for (Match mn : matchedNACS) {
                boolean identical = true;
                for (String label : mn.getLabelMappings().keySet()) {
                    if (!m.getLabelMappings().keySet().contains(label)) continue;
                    identical = identical && m.getLabelMappings().get(label) == mn.getLabelMappings().get(label);
                }
                if (identical) keep = false;
            }
            if (!keep) {
                toRemove.add(m);
            }
        }
        results.removeAll(toRemove);

        System.out.println(results.toString());
        return results;
    }


    /**
     * For a given submatch, recursively finds the matches that contain said submatch
     *
     * @param patternObject The pattern object from which the extension is made
     * @param lastLabel     The label of the last matched pattern object
     * @param match         The actual match containing the current label mappings so far
     * @return A list of matches containing the given submatch.
     */
    @SuppressWarnings("rawtypes")
	ArrayList<Match> extendMatch(@NotNull EObject patternObject, String lastLabel, Match match) {
        ArrayList<Match> currentMatches = new ArrayList<>();
        currentMatches.add(match);
        // Finds the links from the current pattern object to their children
        ArrayList<EReference> links = new ArrayList<>(patternObject.eClass().getEReferences());

        // If there are no links, then the current match is complete.
        if (links.isEmpty()) {
            return currentMatches;
        }

        // For each of those links, find the corresponding children in the pattern
        for (EReference link : links) {
            Object children = patternObject.eGet(link);
            ArrayList<EObject> childrenList = new ArrayList<>();

            if (children == null) {
                continue;
            }

            if (children instanceof EList) {
                for (Object o : (EList) children) {
                    if (o instanceof EObject) {
                        childrenList.add((EObject) o);
                    }
                }
            } else {
                childrenList.add((EObject) children);
            }

            if (childrenList.isEmpty()) {
                continue;
            }

            // For each of these children, try to pair with an object in the actual model
            for (EObject child : childrenList) {
                String nextLabel = child.eGet(label).toString();

                String linkOriginalName = Utils.getOriginalName(link.getName(), Utils.PRE_);
                EObject modelObjectSource = match.getObject(lastLabel);
                if (modelObjectSource == null) continue;
                EStructuralFeature originalLink = modelObjectSource.eClass().getEStructuralFeature(linkOriginalName);

                Object potential = modelObjectSource.eGet(originalLink);
                if (potential == null) {
                    continue;
                }
                if (potential instanceof EList) {
                    for (Object e : (EList) potential) {
                        currentMatches.addAll(refactorMatches(match, currentMatches, child, nextLabel, e));
                    }
                } else {
                    currentMatches.addAll(refactorMatches(match, currentMatches, child, nextLabel, potential));
                }
            }
        }
        return currentMatches;
    }

    // FIXME: 2017-12-08 Automatically factorized method. Give it semantic meaning.
    private ArrayList<Match> refactorMatches(Match match, ArrayList<Match> currentMatches, EObject child, String nextLabel, Object e) {
        if (e instanceof EObject && !match.getLabelMappings().containsValue(e) && objectEquals((EObject) e, child)) {
            ArrayList<Match> newMatches = new ArrayList<>();
            for (Match m : currentMatches) {
                Match tmpMatch = new Match(m);
                if (tmpMatch.getLabelMappings().containsValue(e)) continue;
                tmpMatch.addMapping(nextLabel, (EObject) e);
                newMatches.addAll(extendMatch(child, nextLabel, tmpMatch));
            }
            currentMatches = newMatches;
        }
        return currentMatches;
    }
    
    /**
     * Method to compare class names and check constraints on attributes
     * 
     * @param mObject
     * @param pObject
     * @return
     */
    public boolean objectEquals(@NotNull EObject mObject, @NotNull EObject pObject) {
        ScriptEngine js = Utils.js;
        EStructuralFeature matchSubtypes = pObject.eClass().getEStructuralFeature(Utils.MT_MATCHSUBTYPE);

        // Check if class names are the same
        String mClass = mObject.eClass().getName();
        String pClass = Utils.getOriginalName(pObject.eClass().getName(), Utils.PRE_);
        if (pObject.eGet(matchSubtypes).toString().equals("true")) {
            HashMap<String, ArrayList<String>> subclassesMap = lhs.getPreconditionPattern().getSubclassesMap();
            ArrayList<String> subclasses = subclassesMap.get(pObject.eClass().getName());
            if (!mClass.equals(pClass)) {
                if (subclasses == null || !subclasses.contains(Utils.PRE_ + mClass)) return false;
            }
        } else if (!mClass.equals(pClass)) {
            return false;
        }

        // Check contraints on attributes
        try {
            for (EAttribute ram_constraint : pObject.eClass().getEAllAttributes()) {
                // If attribute is unique to MT, we skip to the next
                String attributeName = Utils.getOriginalName(ram_constraint.getName(), Utils.PRE_);
                if (attributeName.startsWith(Utils.MT_)) continue;
                EStructuralFeature originalAttribute = mObject.eClass().getEStructuralFeature(attributeName);

                // If constraint is empty, we skip to the next
                Object constraint = pObject.eGet(ram_constraint);
                if (constraint == null || constraint.toString().isEmpty()) continue;

                // TODO: 2017-12-08 Add support for other functions. Rewrite the RegEx to be more inclusive.
                // Evaluate constraints with JS engine
                Object value = mObject.eGet(originalAttribute);
                String script = constraint.toString().replaceAll("getAttr\\(\\)", "\"" + (value == null ? "" : value).toString() + "\"");
                js.eval(script);
                if (js.get("result").toString().equals("false")) {
                    return false;
                }
            }
        } catch (ScriptException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // TODO: Add support for match_iter
	@Override
	public ArrayList<Match> match_iter(LHS lhs, int max, Model model) {
		
		return null;
	}


	@Override
	public Packet packetIn(Packet p) {
		isSuccess = false;
        if (p.getModel() == null) {
            throw new IllegalArgumentException("The packet sent to the Matcher must contain a valid model.");
        }

        EObject patternRoot = lhs.getPreconditionPattern().getRootObject();
        model = p.getModel();

        label = patternRoot.eClass().getEStructuralFeature(Utils.MT_LABEL);

        ArrayList<Match> allMatches = match();
        ArrayList<Match> chosenMatches = new ArrayList<>();
        for (int i = 0; i < allMatches.size() && i < max; i++) {
            chosenMatches.add(allMatches.get(i));
        }
        MatchSet ms = new MatchSet(chosenMatches, lhs);
        p.setCurrentMatchSet(ms);
        p.setLhs(lhs);
        isSuccess = true;
        exception = null;

        return p;
	}
}