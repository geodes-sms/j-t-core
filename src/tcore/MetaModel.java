package tcore;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import utils.Utils;

public class MetaModel {
    private String name;
    private Resource resource;
    private EPackage rootPackage;

    public MetaModel(String name, String modelPath) {
        this.name = name;
        resource = Utils.getResourceSet().getResource(URI.createFileURI(modelPath), true);

        //Registering model root package
        rootPackage = (EPackage) resource.getContents().get(0);
        Utils.getResourceSet().getPackageRegistry().put(name, rootPackage);
    }

    public EPackage getRootPackage() {
        return rootPackage;
    }

    public String getName() {
        return name;
    }

    public Resource getResource() {
        return resource;
    }
}

