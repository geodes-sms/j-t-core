package tcore;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tcore.messages.Match;
import utils.Utils;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.ArrayList;

/**
 * The right hand side for a rule. <br>
 * Comprised of a post-condition pattern. <br>
 * This class is also responsible for computing and executing the CRUD operations necessary for the rewriting process of a rule.
 *
 * @author Pierre-Olivier Talbot
 * @version 0.5
 * @since 2017-12-08
 */
public class RHS {

    private LHS lhs;
    private Pattern postconditionPattern;
    private ScriptEngine js;

    private ArrayList<String> createLabels;
    private ArrayList<String> updateLabels;
    private ArrayList<String> deleteLabels;

    /**
     * RHS 
     * 
     * @param lhs
     * @param postconditionPattern
     */
    public RHS(LHS lhs, Pattern postconditionPattern) {
        this.lhs = lhs;
        this.postconditionPattern = postconditionPattern;
        js = Utils.js;
        computeExecution();
    }

    private void computeExecution() {
        createLabels = new ArrayList<>(postconditionPattern.getLabels());
        createLabels.removeAll(lhs.getPreconditionPattern().getLabels());

        updateLabels = new ArrayList<>(lhs.getPreconditionPattern().getLabels());
        updateLabels.retainAll(postconditionPattern.getLabels());

        deleteLabels = new ArrayList<>(lhs.getPreconditionPattern().getLabels());
        deleteLabels.removeAll(postconditionPattern.getLabels());
    }

    /**
     * Get LHS.
     * 
     * @return
     */
    public LHS getLhs() {
        return lhs;
    }

    /**
     * Set LHS.
     * 
     * @param lhs
     */
    public void setLhs(LHS lhs) {
        this.lhs = lhs;
    }

    /**
     * Get Post condition pattern.
     * 
     * @return
     */
    public Model getPostconditionPattern() {
        return postconditionPattern;
    }

    /**
     * Set Post condition pattern.
     * 
     * @param postconditionPattern
     */
    public void setPostconditionPattern(Pattern postconditionPattern) {
        this.postconditionPattern = postconditionPattern;
    }

    /**
     * @param matchToRewrite
     * @throws Exception
     */
    public void execute(Match matchToRewrite) throws Exception {

        for (String deleteLabel : deleteLabels) {
            EObject toDelete = matchToRewrite.getObject(deleteLabel);
            deleteNode(toDelete);
        }

        for (String createLabel : createLabels) {
            EObject patternObject = postconditionPattern.getLabelsMapping().get(createLabel);
            EObject patternObjectParent = patternObject.eContainer();
            if (patternObjectParent == null) {
                throw new Exception(); // TODO: exception... you can't create another root
            }
            EReference containmentFeature = patternObject.eContainmentFeature();
            EStructuralFeature label = patternObject.eClass().getEStructuralFeature(Utils.MT_LABEL);
            String parentLabel = patternObjectParent.eGet(label).toString();
            EObject modelObjectParent = matchToRewrite.getObject(parentLabel);
            if (modelObjectParent == null) throw new Exception(); // FIXME: 2017-12-08 Replace with better suited exception.
            createNode(modelObjectParent, patternObject, containmentFeature, matchToRewrite);
        }

        for (String updateLabel : updateLabels) {
            EObject modelObject = matchToRewrite.getObject(updateLabel);
            EObject patternObject = postconditionPattern.getLabelsMapping().get(updateLabel);
            updateNode(modelObject, patternObject, matchToRewrite);
        }

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void createNode(EObject modelObjectParent, EObject patternObject, EReference containmentFeature, Match match) {
        EStructuralFeature label = patternObject.eClass().getEStructuralFeature(Utils.MT_LABEL);

        String className = Utils.getOriginalName(patternObject.eClass().getName(), Utils.POST_);
        EClass clazz = (EClass) modelObjectParent.eClass().getEPackage().getEClassifier(className);

        EFactory factory = modelObjectParent.eClass().getEPackage().getEFactoryInstance();
        EObject newNode = factory.create(clazz);

        String originalContainmentName = Utils.getOriginalName(containmentFeature.getName(), Utils.POST_);
        EStructuralFeature originalContainment = modelObjectParent.eClass().getEStructuralFeature(originalContainmentName);


        Object oldChildren = modelObjectParent.eGet(originalContainment);
        if (oldChildren instanceof EList) {
            ((EList) oldChildren).add(newNode);
        }
        if (oldChildren == null) {
            modelObjectParent.eSet(originalContainment, newNode);
        }

        modelObjectParent.eResource().getContents().add(newNode);

        String labelName = patternObject.eGet(label).toString();
        match.addMapping(labelName, newNode);

        updateLabels.add(labelName);
    }

    // FIXME: Barf...
    private static Object toObject(@SuppressWarnings("rawtypes") Class c, String s) {
        if (Boolean.class.isAssignableFrom(c)) return Boolean.parseBoolean(s);
        if (Byte.class.isAssignableFrom(c)) return Byte.parseByte(s);
        if (Short.class.isAssignableFrom(c)) return Short.parseShort(s);
        if (Integer.class.isAssignableFrom(c)) return Integer.parseInt(s);
        if (Long.class.isAssignableFrom(c)) return Long.parseLong(s);
        if (Float.class.isAssignableFrom(c)) return Float.parseFloat(s);
        if (Double.class.isAssignableFrom(c)) return Double.parseDouble(s);
        return s;
    }

    private static String toWrapperClassName(String s) {
        switch (s) {
            case "int":
                s = Integer.class.getName();
                break;
            case "char":
                s = Character.class.getName();
                break;
            case "byte":
                s = Byte.class.getName();
                break;
            case "boolean":
                s = Boolean.class.getName();
                break;
            case "float":
                s = Float.class.getName();
                break;
            case "long":
                s = Long.class.getName();
                break;
            case "short":
                s = Short.class.getName();
                break;
            case "double":
                s = Double.class.getName();
                break;
        }
        return s;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void updateNode(EObject modelObject, EObject patternObject, Match match) {
        EStructuralFeature label = patternObject.eClass().getEStructuralFeature(Utils.MT_LABEL);

        EList<EAttribute> attributes = patternObject.eClass().getEAllAttributes();
        for (EAttribute a : attributes) {
            if (a.getName().startsWith(Utils.MT_)) continue;
            String originalAttributeName = Utils.getOriginalName(a.getName(), Utils.POST_);
            EStructuralFeature originalAttribute = modelObject.eClass().getEStructuralFeature(originalAttributeName);
            String action = (patternObject.eGet(a) == null) ? "" : patternObject.eGet(a).toString();
            String value = (modelObject.eGet(originalAttribute) == null) ? "" : modelObject.eGet(originalAttribute).toString();
            String script = action.replaceAll("getAttr\\(\\)", "'" + value + "'");
            Object result;
            try {
                js.eval(script);
                result = js.get("result");
                String s = EcoreUtil.toJavaInstanceTypeName(originalAttribute.getEGenericType());
                s = toWrapperClassName(s);
                Class T = Class.forName(s);
                modelObject.eSet(originalAttribute, toObject(T, (String) result));
            } catch (ScriptException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        EList<EReference> references = patternObject.eClass().getEReferences();
        for (EReference reference : references) {
            Object patternChild = patternObject.eGet(reference);
            EList<EObject> list = new BasicEList<>();
            if (patternChild instanceof EList) {
                list.addAll((EList) patternChild);
            } else {
                list.add((EObject) patternChild);
            }
            for (EObject pc : list) {
                if (patternChild == null) continue;
                String childLabel = (String) pc.eGet(label);
                EObject modelChild = match.getObject(childLabel);
                EStructuralFeature originalReference = modelObject.eClass().getEStructuralFeature(Utils.getOriginalName(reference.getName(), Utils.POST_));
                Object oldChildren = modelObject.eGet(originalReference);
                if (oldChildren instanceof EList) {
                    ((EList) oldChildren).add(modelChild);
                } else {
                    modelObject.eSet(originalReference, modelChild);
                }
            }
        }

    }

    private void deleteNode(EObject modelObject) {
        EcoreUtil.delete(modelObject);
    }


}
