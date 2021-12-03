package tcore;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import utils.EcoreSerializable;
import utils.Utils;

import graph.Edge;
import graph.Graph;
import graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Model.
 * 
 * @author sebastien.ehouan
 *
 */
public class Model implements EcoreSerializable {
    protected String name;
    protected Resource resource;
    protected ArrayList<EObject> objects;
    protected EObject rootObject;
    protected MetaModel metaModel;
    
    /**
     * Object-to-node mapping
     * Used to get the node associated to the EObject when adding edges
     */
    protected HashMap<EObject, Node> nodesByObjectMapping;
    
    /**
     * Node-to-object mapping
     * Used to get the object associated to the node to build the match set in VF2
     */
    protected HashMap<Node, EObject> objectsByNodeMapping;
    
    /**
     * Graph representation of model
     */
    protected Graph graph;

    private String modelPath;
    

    /**
     * @param name
     * @param modelPath
     * @param metaModel
     */
    public Model(String name, String modelPath, MetaModel metaModel) {
        this.name = name;
        this.metaModel = metaModel;
        
        resource = Utils.getResourceSet().getResource(URI.createFileURI(modelPath), true);
        
        nodesByObjectMapping = new HashMap<EObject, Node>();
        objectsByNodeMapping = new HashMap<Node, EObject>();

        //Registering model root package
        rootObject = resource.getContents().get(0);
        Utils.getResourceSet().getPackageRegistry().put(name, rootObject);

        actualizeObjects();
        generateGraph();
    }

    /**
     * 
     */
    public void actualizeObjects() {
        objects = new ArrayList<>(rootObject.eContents());
        objects.add(rootObject);
    }


    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * @param resource
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * @return
     */
    public ArrayList<EObject> getObjects() {
        return objects;
    }

    /**
     * @param objects
     */
    public void setObjects(ArrayList<EObject> objects) {
        this.objects = objects;
    }

    /**
     * @return
     */
    public EObject getRootObject() {
        return rootObject;
    }

    /**
     * @return
     */
    public String getModelPath() {
        return modelPath;
    }

    /**
     * @return
     */
    public MetaModel getMetamodel() {
        return metaModel;
    }
    
    /**
     * @return
     */
    public Graph getGraph() {
        return graph;
    }
    
    /**
     * @return List of objects associated to each node 
     *     Used to build the match set in VF2
     */
    public HashMap<Node, EObject> getObjectsByNode() {
    	return objectsByNodeMapping;
    }

    /**
     * Serializer for Models into a file readable easily by C++.
     * NOTE: Format follows this structure.
     * <code>
     * [Number of nodes]
     * <br>
     * N_[Node URI] [Class name] [Number of attributes]
     * [Attribute name] [Attribute type] [Attribute value]
     * ...
     * <br>
     * E_[Starting Node URI] [Number of edges]
     * [Ending Node URI] [Edge label]
     * ...
     * </code>
     *
     * @return String representing the serialized Model.
     */
    @Override
    public String serialize() {
        StringBuilder s = new StringBuilder(String.valueOf(objects.size()) + "\n"); // Number of nodes
        for (int i = 0; i < objects.size(); i++) {
            EObject node = objects.get(i);
            EList<EAttribute> attributeEList = node.eClass().getEAttributes();
            s.append("N_").append(EcoreUtil.getURI(node)).append(" ").append(node.eClass().getName()).append(" ").append(attributeEList.size()).append("\n"); // N[node_id] [class] [number of attributes]
            for (EAttribute a : attributeEList) {
                s.append(a.getName()).append(" ").append(a.getEType().getName()).append(" ").append("\n"); // [@name] [@type] [@value]
                if (node.eGet(a) != null) {
                	s.append(node.eGet(a).toString());
        		}
            }
        }
        for (int i = 0; i < objects.size(); i++) {
            EObject node = objects.get(i);
            EList<EReference> referenceEList = node.eClass().getEReferences();
            s.append("E_").append(EcoreUtil.getURI(node)).append(" "); // E_[node_id] [number of links (defined below)]
            int linkCounter = 0;
            StringBuilder linksSerialized = new StringBuilder();
            for (EReference ref : referenceEList) {
                Object links = node.eGet(ref);
                if (links instanceof EList) {
                    @SuppressWarnings("rawtypes")
					EList linksList = (EList) links;
                    linkCounter += linksList.size();
                    for (Object o : linksList) {
                        if (o instanceof EObject) {
                            linksSerialized.append(EcoreUtil.getURI((EObject) o)).append(" ").append(ref.getName()).append("\n");
                        }
                    }
                } else if (links instanceof EObject) {
                    linkCounter++;
                    linksSerialized.append(EcoreUtil.getURI((EObject) node.eGet(ref))).append(" ").append(ref.getName()).append("\n");
                } else {
                    //throw new Exception();
                }
            }
            s.append(linkCounter).append("\n").append(linksSerialized.toString());
        }

        return s.toString();
    }
    
    /**
     * Generate a graph representation of the model so it can be used in VF2
     * 
     * Every object is represented as a node with:
     *     id = index between 0 and (number of objects - 1)
     *     label = value of identifier attribute
     * Every relation is represented as an edge with:
     *     source node = source object of relation
     *     target node = target object of relation
     *     label = label of source object -> label of target object
     */
    @SuppressWarnings("unchecked")
	protected void generateGraph() {
        graph = new Graph(name);
        
        int index = 0;
        for (EObject object: objects) {
            String label = EcoreUtil.getURI(object).fragment(); // Get string representation of identifier attribute
            if (label != null && (label instanceof String)) { // Add a node only if the label is defined
	            Node node = new Node(graph, index, label);
	            graph.addNode(node);
	            nodesByObjectMapping.put(object, node);
	            objectsByNodeMapping.put(node, object);
	            index++;
            }
        }
        
        addEdgesToGraph();
    }
    
    /**
     * Add edges representing relations in the model to its graph representation
     * 
     * Every relation is represented as an edge with:
     *     source node = source object of relation
     *     target node = target object of relation
     *     label = label of source object -> label of target object
     */
    protected void addEdgesToGraph() {
        for (Node gn: graph.nodes) {
        	EObject node = objects.get(gn.id);
            EList<EReference> referenceEList = node.eClass().getEReferences();
            for (EReference ref : referenceEList) {
                Object links = node.eGet(ref);
                
                // The link can either be an EList (it has more references under it) or an EObject (it is the only linked object)
                if (links instanceof EList) {
                    @SuppressWarnings("rawtypes")
					EList linksList = (EList) links;
                    for (Object o : linksList) {
                        if (o instanceof EObject) {
                        	Node targetNode = nodesByObjectMapping.get(o); // Get node represented by object
                        	if (targetNode != null)
                        		graph.addEdge(gn, targetNode, gn.label + " -> " + targetNode.label);
                        }
                    }
                } else if (links instanceof EObject) {
                	Node targetNode = nodesByObjectMapping.get(links);
                	if (targetNode != null)
                		graph.addEdge(gn, targetNode, gn.label + " -> " + targetNode.label);
                }
            }
        }
    }
}
