package unit.tests;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tcore.LHS;
import tcore.MetaModel;
import tcore.Model;
import tcore.Pattern;
import tcore.messages.MatchSet;
import tcore.messages.Packet;
import tcore.strategy.Matcher;
import tcore.messages.Match;

/**
 * Unit test for cases with a single match.
 *
 * @author Sebastien EHouan
 * @since 2020-07-30
 */

class MatcherTest {
	@Before
	public void setUp() {
	}

	@Test
	public void singleMatch1() throws Exception {
		utils.Utils.initialize();

        // Imports
		MetaModel OracleMM = new MetaModel("Oracle", "Ramifier_New/Model/Oracle.ecore"); //Oracle MetaModel
        MetaModel Oracle_ramified = new MetaModel("OracleRoot", "Ramifier_New/Model/Oracle_augmented.ecore"); //Ramified Oracle
        
        Model oracle = new Model("Oracle", "Ramifier_New/Model/Oracle.xmi", OracleMM); //Dynamic Instance from Oracle
        
        Pattern SingleMatch_pre = new Pattern("SingleMatch_pre", "Ramifier_New/Model/SingleMatch_pre.xmi", Oracle_ramified);

        Packet p = new Packet(oracle);
        LHS lhs = new LHS(SingleMatch_pre, null);
        
        //Testing
        Matcher tester = new Matcher(lhs, 5);  //max=1
        
		@SuppressWarnings("unused")
		Packet result = tester.packetIn(p);
		
		Match expectedMatch = new Match();
		
		for(EObject o : oracle.getObjects()){
			switch(EcoreUtil.getID(o)) {
				case "1" : expectedMatch.addMapping("1", o); 	  //Single Match case
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
        
//      System.out.println(ms);
//      System.out.println(p.getCurrentMatchSet());
        
        assertTrue(ms.equals(p.getCurrentMatchSet()),"Wrong match found");
	}
	
	@Test
	public void singleMatch2() throws Exception {
		utils.Utils.initialize();

        // Imports
		MetaModel OracleMM = new MetaModel("Oracle", "/Users/sebastien.ehouan/eclipse-workspace2/jtcore/Ramifier_New/Model/Oracle.ecore"); //Oracle MetaModel
        MetaModel Oracle_ramified = new MetaModel("OracleRoot", "/Users/sebastien.ehouan/eclipse-workspace2/jtcore/Ramifier_New/Model/Oracle_augmented.ecore"); //Ramified Oracle
        
        Model oracle = new Model("Oracle", "/Users/sebastien.ehouan/eclipse-workspace2/jtcore/Ramifier_New/Model/Oracle.xmi", OracleMM); //Dynamic Instance from Oracle
        
        Pattern SingleMatch_pre = new Pattern("SingleMatch_pre", "/Users/sebastien.ehouan/eclipse-workspace2/jtcore/Ramifier_New/Model/SingleMatch_pre.xmi", Oracle_ramified); //

        Packet p = new Packet(oracle);
        LHS lhs = new LHS(SingleMatch_pre, null);
        
        //Testing
        Matcher tester = new Matcher(lhs, 5);  //max=1
        
		@SuppressWarnings("unused")
		Packet result = tester.packetIn(p);
		
		Match expectedMatch = new Match();
		
		for(EObject o : oracle.getObjects()){
			switch(EcoreUtil.getID(o)) {
				case "1" : expectedMatch.addMapping("1", o);
					break;
				case "2" : expectedMatch.addMapping("2", o);
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
