package utils;

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Utility class for J-T-Core.
 */
public abstract class Utils {
    // Constants
    public final static String PRE_ = "MTpre__";
    public final static String POST_ = "MTpos__";
    public final static String MT_ = "MT__";
    public final static String MT_LABEL = "MT__label";
    public final static String MT_MATCHSUBTYPE = "MT__matchSubtype";

    // Singleton js engine
    public final static ScriptEngine js = new ScriptEngineManager().getEngineByName("nashorn");

    private final static ResourceSetImpl resourceSet = new ResourceSetImpl();

    /**
     * Initializes the resource factories necessary to import and export EMF files. <br>
     * Must absolutely be called once before any imports / exports.
     */
    public static void initialize() {
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
    }

    /**
     * Removes prefix from augmented name and returns resulting string.
     *
     * @param augmentedName Prefix + Name.
     * @param prefix        Prefix from tcore.utils constants.
     * @return Substring with only the name.
     */
    public static String getOriginalName(@NotNull String augmentedName, String prefix) {
        return augmentedName.startsWith(prefix) ? augmentedName.substring(prefix.length()) : augmentedName;
    }

    @Contract(pure = true)
    public static ResourceSetImpl getResourceSet() {
        return resourceSet;
    }
}
