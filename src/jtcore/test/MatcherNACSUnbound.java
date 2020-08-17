package jtcore.test;

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

class MatcherNACSUnbound {
	
	@Before
	public void setUp() {
	}

	@Test
	public void isSuccess() throws Exception {
		utils.Utils.initialize();

        // Imports
		MetaModel OracleMM = new MetaModel("Oracle", "/Users/sebastien.ehouan/eclipse-workspace2/jtcore/Ramifier_New/Model/Oracle.ecore"); //Oracle MetaModel
        MetaModel Oracle_augmented = new MetaModel("OracleRoot", "/Users/sebastien.ehouan/eclipse-workspace2/jtcore/Ramifier_New/Model/Oracle_augmented.ecore"); //Ramified Oracle
        
        Model oracle = new Model("Oracle", "/Users/sebastien.ehouan/eclipse-workspace2/jtcore/res/Instance1.xmi", OracleMM); //Dynamic Instance from Oracle
        
        Pattern pre_A = new Pattern("pre_A", "/Users/sebastien.ehouan/eclipse-workspace2/jtcore/Ramifier_New/Model/pre_A.xmi", Oracle_augmented); //
        Pattern Oracle_NAC = new Pattern("assignTables_NAC", "/Users/sebastien.ehouan/eclipse-workspace2/jtcore/Ramifier_New/Model/Instance_NACS_Unbound_1.xmi", Oracle_augmented);
        ArrayList<Pattern> oracle_NACS = new ArrayList<>();
        oracle_NACS.add(Oracle_NAC);

        Packet p = new Packet(oracle);
        LHS lhs = new LHS(pre_A, oracle_NACS);
        
        //Testing
        Matcher tester = new Matcher(lhs, 1);  //max=1
        
		Packet result = tester.packetIn(p);
		
		Match expectedMatch = new Match();
		
		for(EObject o : oracle.getObjects()){
			switch(EcoreUtil.getID(o)) {	
				case "1" : expectedMatch.addMapping("1", o);      //NAC Unbound case
					break;
				case "2" : expectedMatch.addMapping("2", o);      //NAC Unbound case
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
