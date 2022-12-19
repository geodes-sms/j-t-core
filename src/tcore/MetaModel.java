package tcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
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

		// Registering model root package
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

	/**
	 * Gets the components of a given Object, from the Metamodel.
	 * 
	 * @param targetNodeIndex targetNode
	 * @return An HashMap of the className (key) with an ArrayList of the classes
	 *         with a composition link, uses parsing
	 **/
	public HashMap<String, ArrayList<String>> getComposed(int targetNodeIndex) {
		EObject MMContent = resource.getContents().get(0).eContents().get(targetNodeIndex);
		HashMap<String, ArrayList<String>> MMContains = new HashMap<String, ArrayList<String>>();
		ArrayList<String> contained = new ArrayList<String>();
		String Container = MMContent.toString().split(" ")[2].split("")[0];

		for (EObject object : MMContent.eContents()) {
			if (object.eClass().getName().equals("EReference")) {
				contained.add(object.eContents().get(0).toString().split(" ")[2].split("")[0]);
			}
			MMContains.put(Container, contained);
		}

		return MMContains;
	}

	/**
	 * Gets the subclasses of a given Object, from the Metamodel.
	 * 
	 * @return An HashMap of the className (key) with an ArrayList of its
	 *         subclasses, uses parsing
	 **/
	public HashMap<String, ArrayList<String>> getSubclasses() {
		List<EObject> MMContent = resource.getContents().get(0).eContents();
		HashMap<String, ArrayList<String>> MMSubclasses = new HashMap<String, ArrayList<String>>();
		ArrayList<String> subclasses = new ArrayList<String>();
		for (EObject object : MMContent) {
			if (object.eContents().get(0).eClass().getName().equals("EGenericType")) {
				subclasses.add(object.toString().split(" ")[2].split("")[0]);
				MMSubclasses.put(object.eContents().get(0).toString().split(" ")[2].split("")[0], subclasses);
			}
		}
		return MMSubclasses;
	}

	/**
	 * Gets the abstracts classes, from the Metamodel.
	 * 
	 * @return An ArrayList of the abstract classes, from the MetaModel.
	 **/
	public ArrayList<String> Abstracts() {
		List<EObject> MMContent = resource.getContents().get(0).eContents();
		ArrayList<String> abstracts = new ArrayList<String>();
		for (EObject object : MMContent) {
			if (object.toString().split(" ")[6].split("")[0].equals("t")) {
				abstracts.add(object.toString().split(" ")[2].split("")[0]);
			}
		}
		return abstracts;
	}

}
