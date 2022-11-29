import rules.*;
import tcore.*;
import tcore.constant.JTCoreConstant;
import tcore.messages.Packet;

import java.util.ArrayList;

/**
 * Basic test for defining rules
 *
 * Currently unstable with the new algorithm selection strategy
 * 
 * @author Pierre-Olivier Talbot
 */
public class MainTest {
	 

    @SuppressWarnings("javadoc")
	public static void main(String[] args) throws Exception {
        utils.Utils.initialize();

        // Imports
        MetaModel tablesMM = new MetaModel("tables", "../res/tables/tables.ecore");
        MetaModel tables_ramified = new MetaModel("RamRoot", "../res/tables/tables_ramified.ecore");

        Model tables = new Model("tables", "../res/tables/tables.xmi", tablesMM);

        Pattern fireMimi_pre = new Pattern("fireMimi_pre", "../res/tables/fireMimi_pre.xmi", tables_ramified);
        Pattern fireMimi_pos = new Pattern("fireMimi_pos", "../res/tables/fireMimi_pos.xmi", tables_ramified);

        Pattern assignTables_pre = new Pattern("assignTables_pre", "../res/tables/assignTables_pre.xmi", tables_ramified);
        Pattern assignTables_NAC = new Pattern("assignTables_NAC", "../res/tables/assignTables_NAC.xmi", tables_ramified);
        ArrayList<Pattern> assignTables_NACS = new ArrayList<>();
        assignTables_NACS.add(assignTables_NAC);
        Pattern assignTables_pos = new Pattern("assignTables_pos", "../res/tables/assignTables_pos.xmi", tables_ramified);

        System.out.println(assignTables_pre.serialize());

        // Rules definitions
        BasicRule fireMimi = RuleFactory.createARule("FireMimi", fireMimi_pre, null, fireMimi_pos, true, JTCoreConstant.SIMPLEMATCH_ALGORITHM);
        BasicRule assignTables = RuleFactory.createFRule("AssignTables", assignTables_pre, assignTables_NACS, assignTables_pos, true, JTCoreConstant.SIMPLEMATCH_ALGORITHM);

        // Execution
        Packet p = new Packet(tables);

        p = fireMimi.packetIn(p);
        if (!fireMimi.isSuccess()) throw fireMimi.getException();

        assignTables.packetIn(p);
        if (!assignTables.isSuccess()) throw assignTables.getException();

        // Save model
        tables.getResource().setURI(org.eclipse.emf.common.util.URI.createFileURI("res/tables/tables_RESULT.xmi"));
        tables.getResource().save(null);
    }
}

