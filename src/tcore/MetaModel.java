package tcore;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import utils.Utils;

/**
 * Metamodel.
 * 
 * @author Pierre-Olivier Talbot.
 */
public class MetaModel {
    private String name;
    private Resource resource;
    private EPackage rootPackage;

    /**
     * @param name
     * @param modelPath
     */
    public MetaModel(String name, String modelPath) {
        this.name = name;
        resource = Utils.getResourceSet().getResource(URI.createFileURI(modelPath), true);

        //Registering model root package
        rootPackage = (EPackage) resource.getContents().get(0);
        Utils.getResourceSet().getPackageRegistry().put(rootPackage.getNsURI(), rootPackage);
    }

    /**
     * @return
     */
    public EPackage getRootPackage() {
        return rootPackage;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public Resource getResource() {
        return resource;
    }
}

