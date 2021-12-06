package tcore;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import graph.Graph;
import graph.Node;
import tcore.messages.exceptions.LabelNotUniqueException;
import tcore.messages.exceptions.MissingLabelException;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * File:
 * Description:
 * <p>
 * Package: tcore
 * Project: J-T-Core
 * <p>
 * Date: 2021-12-19
 *
 * @author Pierre-Olivier Talbot
 * @author An Li
 * @version 1.2
 * @since 1.8.0_121
 */
public class Pattern extends Model {

    private HashMap<String, ArrayList<String>> subclassesMap;
    private ArrayList<String> labels;
    private HashMap<String, EObject> labelsMapping;

    /**
     * @param name
     * @param modelPath
     * @param metaModel
     */
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
        @SuppressWarnings("rawtypes")
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
        @SuppressWarnings("rawtypes")
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

    /**
     * Get subclasses map.
     * 
     * @return
     */
    public HashMap<String, ArrayList<String>> getSubclassesMap() {
        return subclassesMap;
    }

    /**
     * Get labels.
     * 
     * @return
     */
    public ArrayList<String> getLabels() {
        return labels;
    }

    /**
     * Get label mappings.
     * 
     * @return
     */
    public HashMap<String, EObject> getLabelsMapping() {
        return labelsMapping;
    }
    
    /**
     * Generate a graph representation of the model so it can be used in VF2
     * 
     * <p>
     * Every object is represented as a node with:
     *     id = index between 0 and (number of objects - 1)
     *     label = value of label associated to object
     *     className = name of class representing the object
     * Every relation is represented as an edge with:
     *     source node = source object of relation
     *     target node = target object of relation
     *     label = label of source object -> label of target object
     * </p>
     */
	@Override
    protected void generateGraph() {    
        graph = new Graph(name);
    	
        int index = 0;
        for (EObject object: objects) {
            EStructuralFeature labelFeature = object.eClass().getEStructuralFeature(Utils.MT_LABEL); // Get the label of object
            Object label = ((EObjectImpl) object).eGet(labelFeature);
            String className = object.eClass().getName().replaceAll(Utils.PRE_, ""); // Get the class name and remove the "MTpre__" pattern to get the 'actual' class name of object to match on
            if (label != null && (label instanceof String)) { // Add a node only if the label is defined
            	Node node = new Node(graph, index, (String) label, className);
                graph.addNode(node);
                nodesByObjectMapping.put(object, node);
                objectsByNodeMapping.put(node, object);
                index++;
            }
        }
        
        addEdgesToGraph();
    }
}
