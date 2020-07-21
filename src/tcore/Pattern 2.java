package tcore;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import tcore.messages.exceptions.LabelNotUniqueException;
import tcore.messages.exceptions.MissingLabelException;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * File:
 * Description:
 * <p>
 * Package: tcore
 * Project: JET-Core
 * <p>
 * Date: 2017-11-26
 *
 * @author Pierre-Olivier Talbot
 * @version 1.0
 * @since 1.8.0_121
 */
public class Pattern extends Model {

    private HashMap<String, ArrayList<String>> subclassesMap;
    private ArrayList<String> labels;
    private HashMap<String, EObject> labelsMapping;


    public Pattern(String name, String modelPath, MetaModel metaModel) {
        super(name, modelPath, metaModel);
        deriveSubclasses();
        try {
            computeLabels();
        } catch (LabelNotUniqueException | MissingLabelException e) {
            //TODO: implement exceptions
        }
    }

    private void computeLabels() throws LabelNotUniqueException, MissingLabelException {
        labels = new ArrayList<>();
        labelsMapping = new HashMap<>();
        EStructuralFeature labelFeature = rootObject.eClass().getEStructuralFeature(Utils.MT_LABEL);
        TreeIterator i = resource.getAllContents();
        while (i.hasNext()) {
            Object o = i.next();
            if (o instanceof EObjectImpl) {
                Object label = ((EObjectImpl) o).eGet(labelFeature);
                if (label == null || !(label instanceof String)) {
                    throw new MissingLabelException();
                }
                if (labels.contains(label)) {
                    throw new LabelNotUniqueException();
                }
                if (!labelsMapping.containsValue(o)) {
                    labelsMapping.put((String) label, (EObject) o);
                }
                labels.add((String) label);
            }
        }
    }

    private void deriveSubclasses() {
        subclassesMap = new HashMap<>();
        TreeIterator i = metaModel.getRootPackage().eAllContents();
        while (i.hasNext()) {
            EObject o = (EObject) i.next();
            if (!(o instanceof EClassImpl)) continue;
            EList<EClass> supertypes = ((EClassImpl) o).getEAllSuperTypes();
            for (EClass ancestor : supertypes) {
                ArrayList<String> subtypesList = subclassesMap.computeIfAbsent(ancestor.getName(), k -> new ArrayList<>());
                if (!subtypesList.contains(((EClassImpl) o).getName())) {
                    subtypesList.add(((EClassImpl) o).getName());
                }
            }
        }
    }

    public HashMap<String, ArrayList<String>> getSubclassesMap() {
        return subclassesMap;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public HashMap<String, EObject> getLabelsMapping() {
        return labelsMapping;
    }
}
