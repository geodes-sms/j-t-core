package unit.tests;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import tcore.LHS;
import tcore.MetaModel;
import tcore.Model;
import tcore.Pattern;
import tcore.constant.JTCoreConstant;
import tcore.messages.MatchSet;
import tcore.messages.Packet;
import tcore.strategy.Matcher;
import tcore.messages.Match;

/**
 * Unit test for a case with no matches found.
 *
 * @author Sebastien Ehouan
 * @author An Li
 * @since 2021-12-19
 */

class MatcherZeroMatchTest {
	
	@Before
	public void setUp() {
	}

	@Test
	public void isSuccess() throws Exception {
		utils.Utils.initialize();

        // Imports
		MetaModel OracleMM = new MetaModel("Oracle", "../Ramifier_New/Model/Oracle.ecore"); //Oracle MetaModel
        MetaModel Oracle_augmented = new MetaModel("OracleRoot", "../Ramifier_New/Model/Oracle_augmented.ecore"); //Ramified Oracle
        
        Model oracle = new Model("Oracle", "../Ramifier_New/Model/Oracle.xmi", OracleMM); //Dynamic Instance from Oracle
        
        Pattern ZeroMatch_pre = new Pattern("ZeroMatch_pre", "../Ramifier_New/Model/ZeroMatch_pre.xmi", Oracle_augmented); //precondition

        Packet p = new Packet(oracle);
        LHS lhs = new LHS(ZeroMatch_pre, null);
        
        //Testing
        Matcher tester = new Matcher(lhs, 5, JTCoreConstant.SIMPLEMATCH_ALGORITHM);  //max=1
        
		@SuppressWarnings("unused")
		Packet result = tester.packetIn(p);
		
		@SuppressWarnings("unused")
		Match expectedMatch = new Match();
		
		//Array of matches expected to be found
		ArrayList<Match> expectedMatchArray = new ArrayList<Match>();
		
		//Expected MatchSet to find
        MatchSet ms = new MatchSet(expectedMatchArray,lhs);
  		
        assertTrue(tester.isSuccess(),"Matcher failed");        
        assertTrue(ms.equals(p.getCurrentMatchSet()),"Wrong match found");
	}
	
	@Test
	public void isSuccessVF2() throws Exception {
		utils.Utils.initialize();

        // Imports
		MetaModel OracleMM = new MetaModel("Oracle", "../Ramifier_New/Model/Oracle.ecore"); //Oracle MetaModel
        MetaModel Oracle_augmented = new MetaModel("OracleRoot", "../Ramifier_New/Model/Oracle_augmented.ecore"); //Ramified Oracle
        
        Model oracle = new Model("Oracle", "../Ramifier_New/Model/Oracle.xmi", OracleMM); //Dynamic Instance from Oracle
        
        Pattern ZeroMatch_pre = new Pattern("ZeroMatch_pre", "../Ramifier_New/Model/ZeroMatch_pre.xmi", Oracle_augmented); //precondition

        Packet p = new Packet(oracle);
        LHS lhs = new LHS(ZeroMatch_pre, null);
        
        //Testing
        Matcher tester = new Matcher(lhs, 5, JTCoreConstant.VF2_ALGORITHM);  //max=1
        
		@SuppressWarnings("unused")
		Packet result = tester.packetIn(p);
		
		@SuppressWarnings("unused")
		Match expectedMatch = new Match();
		
		//Array of matches expected to be found
		ArrayList<Match> expectedMatchArray = new ArrayList<Match>();
		
		//Expected MatchSet to find
        MatchSet ms = new MatchSet(expectedMatchArray,lhs);
  		
        assertTrue(tester.isSuccess(),"Matcher failed");        
        assertTrue(ms.equals(p.getCurrentMatchSet()),"Wrong match found");
	}
	
	@Test
	public void isSuccessWrongClassVF2() throws Exception {
		utils.Utils.initialize();

        // Imports
		MetaModel OracleMM = new MetaModel("Oracle", "../Ramifier_New/Model/Oracle.ecore"); //Oracle MetaModel
        MetaModel Oracle_augmented = new MetaModel("OracleRoot", "../Ramifier_New/Model/Oracle_augmented.ecore"); //Ramified Oracle
        
        Model oracle = new Model("Oracle", "../Ramifier_New/Model/Oracle.xmi", OracleMM); //Dynamic Instance from Oracle
        
        Pattern ZeroMatch_pre = new Pattern("ZeroMatch_pre", "../Ramifier_New/Model/ZeroMatch_pre_wrongclass.xmi", Oracle_augmented); //precondition

        Packet p = new Packet(oracle);
        LHS lhs = new LHS(ZeroMatch_pre, null);
        
        //Testing
        Matcher tester = new Matcher(lhs, 5, JTCoreConstant.VF2_ALGORITHM);  //max=1
        
		@SuppressWarnings("unused")
		Packet result = tester.packetIn(p);
		
		@SuppressWarnings("unused")
		Match expectedMatch = new Match();
		
		//Array of matches expected to be found
		ArrayList<Match> expectedMatchArray = new ArrayList<Match>();
		
		//Expected MatchSet to find
        MatchSet ms = new MatchSet(expectedMatchArray,lhs);
  		
        assertTrue(tester.isSuccess(),"Matcher failed");        
        assertTrue(ms.equals(p.getCurrentMatchSet()),"Wrong match found");
	}
	
	@Test
	public void isSuccessLabelDoesNotExistVF2() throws Exception {
		utils.Utils.initialize();

        // Imports
		MetaModel OracleMM = new MetaModel("Oracle", "../Ramifier_New/Model/Oracle.ecore"); //Oracle MetaModel
        MetaModel Oracle_augmented = new MetaModel("OracleRoot", "../Ramifier_New/Model/Oracle_augmented.ecore"); //Ramified Oracle
        
        Model oracle = new Model("Oracle", "../Ramifier_New/Model/Oracle.xmi", OracleMM); //Dynamic Instance from Oracle
        
        Pattern ZeroMatch_pre = new Pattern("ZeroMatch_pre", "../Ramifier_New/Model/ZeroMatch_pre_labeldoesnotexist.xmi", Oracle_augmented); //precondition

        Packet p = new Packet(oracle);
        LHS lhs = new LHS(ZeroMatch_pre, null);
        
        //Testing
        Matcher tester = new Matcher(lhs, 5, JTCoreConstant.VF2_ALGORITHM);  //max=1
        
		@SuppressWarnings("unused")
		Packet result = tester.packetIn(p);
		
		@SuppressWarnings("unused")
		Match expectedMatch = new Match();
		
		//Array of matches expected to be found
		ArrayList<Match> expectedMatchArray = new ArrayList<Match>();
		
		//Expected MatchSet to find
        MatchSet ms = new MatchSet(expectedMatchArray,lhs);
  		
        assertTrue(tester.isSuccess(),"Matcher failed");        
		assertTrue(ms.equals(p.getCurrentMatchSet()), "Wrong match found");
	}
}
