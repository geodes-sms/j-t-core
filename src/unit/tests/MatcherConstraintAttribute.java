package unit.tests;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import tcore.LHS;
import tcore.MetaModel;
import tcore.Model;
import tcore.Pattern;
import tcore.messages.MatchSet;
import tcore.messages.Packet;
import tcore.strategy.Matcher;
import tcore.messages.Match;

/**
 * Unit test for a case with constraints.
 *
 * @author Sebastien EHouan
 * @since 2020-07-30
 */

class MatcherConstraintAttribute {
	
	@Before
	public void setUp() {
	}

	@Test
	public void isSuccess() throws Exception {
		utils.Utils.initialize();

        // Imports
		MetaModel OracleMM = new MetaModel("Oracle", "Ramifier_New/Model/Oracle.ecore"); //Oracle MetaModel
        MetaModel Oracle_ramified = new MetaModel("OracleRoot", "Ramifier_New/Model/Oracle_augmented.ecore"); //Ramified Oracle
        
        Model oracle = new Model("Oracle", "Ramifier_New/Model/Oracle.xmi", OracleMM); //Dynamic Instance from Oracle
     
        Pattern ConstraintAttribute_pre = new Pattern("ConstraintAttribute_pre", "Ramifier_New/Model/SingleMatch_pre.xmi", Oracle_ramified); //precondition
        Pattern Oracle_NAC = new Pattern("assignTables_NAC", "Ramifier_New/Model/ConstraintAttribute_pre.xmi", Oracle_ramified); //NAC
        ArrayList<Pattern> oracle_NACS = new ArrayList<>();
        oracle_NACS.add(Oracle_NAC);

        Packet p = new Packet(oracle);
        LHS lhs = new LHS(ConstraintAttribute_pre, oracle_NACS);
        
        //Testing
        Matcher tester = new Matcher(lhs, 5);
        
		@SuppressWarnings("unused")
		Packet result = tester.packetIn(p);
		
		Match expectedMatch = new Match();
		
		for(EObject o : oracle.getObjects()){
			switch(EcoreUtil.getID(o)) {	
				case "1" : expectedMatch.addMapping("1", o);
					break;
				default: break;	
			}
		}
		
		//Array of matches expected to be found
		ArrayList<Match> expectedMatchArray = new ArrayList<Match>();
		expectedMatchArray.add(expectedMatch);
		
		//Expected MatchSet to find
        MatchSet ms = new MatchSet(expectedMatchArray,lhs);
        
        assertTrue(tester.isSuccess(),"Matcher failed");
        assertTrue(ms.equals(p.getCurrentMatchSet()),"Wrong match found");
	}
}
