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

class MatcherTest {
	@Before
	public void setUp() {
	}

	@Test
	public void isSuccess() throws Exception {
		utils.Utils.initialize();
		

        // Imports
		MetaModel OracleMM = new MetaModel("Oracle", "/Users/sebastien.ehouan/eclipse-workspace2/Ramifier_atl/Model/Oracle.ecore"); //Oracle MetaModel
        MetaModel Oracle_augmented = new MetaModel("OracleRoot", "/Users/sebastien.ehouan/eclipse-workspace2/Ramifier_atl/Model/Oracle_augmented.ecore"); //Ramified Oracle
        
//      XMIResourceImpl resource = new XMIResourceImpl();
//      File source = new File("/Users/sebastien.ehouan/eclipse-workspace2/jtcore/res/Instance1.xmi");
//      resource.load(new FileInputStream(source), new HashMap<Object, Object>());
//      Data data = (Data) resource.getContents().get(0);
        
        Model oracle = new Model("Oracle", "res/Instance1.xmi", OracleMM); //Dynamic Instance from Oracle
        
        Pattern pre_A = new Pattern("pre_A", "/Users/sebastien.ehouan/eclipse-workspace2/Ramifier_atl/Model/pre_A.xmi", Oracle_augmented); //

        Packet p = new Packet(oracle);
        LHS lhs = new LHS(pre_A, null);
        
        //Testing
        Matcher tester = new Matcher(lhs, 1);  //max=1
        
		Packet result = tester.packetIn(p);
		
		Match expectedMatch = new Match();
		
		for(EObject o : oracle.getObjects()){
			switch(EcoreUtil.getID(o)) {
			case "0" : expectedMatch.addMapping("1", o); 	  //Single Match case
				break;	
//			case "2" : expectedMatch.addMapping("2", o);      //NAC Bound case
//				break;
				
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
