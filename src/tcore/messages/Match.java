package tcore.messages;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.jetbrains.annotations.Nullable;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.HashMap;

/**
 * A match between a {@link tcore.Model} and a {@link tcore.LHS}.
 *
 * @author Pierre-Olivier Talbot
 * @author Sebastien Ehouan
 * @version 0.5
 * @since 2017-12-08
 */
public class Match {

    /**
     * Mapping between pattern labels and model objects.
     */
    private HashMap<String, EObject> labelMappings;

    /**
     * Mapping between objects and their dirty status.
     */
    private HashMap<EObject, Boolean> dirtyMap;


    public Match() {
        labelMappings = new HashMap<>();
        dirtyMap = new HashMap<>();
    }

    /**
     * Constructor which immediately adds a mapping within the match.
     *
     * @param label  Pattern label.
     * @param object Model object.
     */
    public Match(String label, EObject object) {
        this();
        addMapping(label, object);
    }

    /**
     * Copy constructor.
     *
     * @param match The match to copy.
     */
    public Match(Match match) {
        this();
        for (String k : match.labelMappings.keySet()) {
            EObject o = match.labelMappings.get(k);
            labelMappings.put(k, o);
            dirtyMap.put(o, match.dirtyMap.getOrDefault(o, false));
        }
    }

    public void setNodeToDirty(EObject node) {
        dirtyMap.put(node, true);
    }

    public boolean checkIfDirty(EObject node) {
        return dirtyMap.getOrDefault(node, false);
    }

    public boolean hasDirtyNodes() {
        return dirtyMap.values().contains(true);
    }

    /**
     * Adds a mapping between a pattern label and a model object.
     *
     * @param label  The pattern label.
     * @param object The model object.
     */
    public void addMapping(String label, EObject object) {
        labelMappings.put(label, object);
    }

    /**
     * Getter for specific object mapped to passed label.
     *
     * @param label The label.
     * @return The object associated with passed label, {@code null} if no object is found.
     */
    @Nullable
    public EObject getObject(String label) {
        return labelMappings.get(label);
    }


    public HashMap<String, EObject> getLabelMappings() {
        return labelMappings;
    }


    // FIXME: 2017-12-08 Temporary toString override for debugging purposes.
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (String s : labelMappings.keySet()) {
            res.append(s).append(": {");
            EObject o = labelMappings.get(s);
            res.append("Class: ").append(o.eClass().getName()).append(", ");
            for (EStructuralFeature f : o.eClass().getEAllStructuralFeatures()) {
                if (!(f instanceof EReference)) {
                    res.append(f.getName()).append(" --> ").append((o.eGet(f) == null) ? "" : o.eGet(f).toString());
                }
            }
            res.append("} ; ");
        }
        return res.toString();
    }
    
    
    /**
     * Overriden equal function.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Match match = (Match) o;

        /**
         * Pseudo-code for the comparison.
         */
        //unique e core id for comparison
        //for loop on the keys of label Mapping
        //  if k not in o.labelmapping
        //    return false
        //  else if labelmapping.get(k).id != o.labelmapping.get(k).id
        //    return false
        //return true
        
        for (String k : match.labelMappings.keySet()) {
            EObject o2 = this.labelMappings.get(k);
            if (!this.labelMappings.containsKey(k))
            	return false;
            if (!EcoreUtil.getID(o2).equals(EcoreUtil.getID(match.labelMappings.get(k)))) //comparing eObject IDs
            	return false;  	
        }
        
        return true;
    }

    /**
     * Removes the dirty status of all nodes inside this match.
     */
    public void clean() {
        dirtyMap.clear();
    }
}
