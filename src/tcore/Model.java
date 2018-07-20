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

import java.util.ArrayList;

public class Model implements EcoreSerializable {
    protected String name;
    protected Resource resource;
    protected ArrayList<EObject> objects;
    protected EObject rootObject;
    protected MetaModel metaModel;
    private String modelPath;
    private MetaModel metamodel;

    public Model(String name, String modelPath, MetaModel metaModel) {
        this.name = name;
        this.metaModel = metaModel;
        resource = Utils.getResourceSet().getResource(URI.createFileURI(modelPath), true);

        //Registering model root package
        rootObject = resource.getContents().get(0);
        Utils.getResourceSet().getPackageRegistry().put(name, rootObject);

        actualizeObjects();
    }

    public void actualizeObjects() {
        objects = new ArrayList<>(rootObject.eContents());
        objects.add(rootObject);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public ArrayList<EObject> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<EObject> objects) {
        this.objects = objects;
    }

    public EObject getRootObject() {
        return rootObject;
    }

    public String getModelPath() {
        return modelPath;
    }

    public MetaModel getMetamodel() {
        return metamodel;
    }

    /**
     * Serializer for Models into a file readable easily by C++.
     * NOTE: Format follows this structure.
     * <code>
     * [Number of nodes]
     * <p>
     * N_[Node URI] [Class name] [Number of attributes]
     * [Attribute name] [Attribute type] [Attribute value]
     * ...
     * <p>
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
                s.append(a.getName()).append(" ").append(a.getEAttributeType().getName()).append(" ").append(node.eGet(a).toString()).append("\n"); // [@name] [@type] [@value]
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
}
