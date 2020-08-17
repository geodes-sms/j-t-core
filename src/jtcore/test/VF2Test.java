package jtcore.test;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import tcore.LHS;
import tcore.MetaModel;
import tcore.Model;
import tcore.Pattern;
import tcore.State;
import tcore.messages.Match;
import tcore.messages.MatchSet;
import tcore.messages.Packet;
import tcore.strategy.VF2;
import graph.Graph;


public class VF2Test {

	@Test
	public void isSuccess() throws Exception {
		utils.Utils.initialize();
		
		// Imports
		MetaModel OracleMM = new MetaModel("Oracle", "/Users/sebastien.ehouan/eclipse-workspace2/jtcore/Ramifier_New/Model/Oracle.ecore"); //Oracle MetaModel
        MetaModel Oracle_augmented = new MetaModel("OracleRoot", "/Users/sebastien.ehouan/eclipse-workspace2/jtcore/Ramifier_New/Model/Oracle_augmented.ecore"); //Ramified Oracle
        
        Model oracle = new Model("Oracle", "/Users/sebastien.ehouan/eclipse-workspace2/jtcore/res/Instance1.xmi", OracleMM); //Dynamic Instance from Oracle
        
        Pattern pre_A = new Pattern("pre_A", "/Users/sebastien.ehouan/eclipse-workspace2/jtcore/Ramifier_New/Model/pre_A.xmi", Oracle_augmented); //

        Packet p = new Packet(oracle);
        LHS lhs = new LHS(pre_A, null);

		VF2 vf2Test = new VF2();
		
		ArrayList<Match> result = vf2Test.match();
		
		Match expectedMatch = new Match();
		
		for(EObject o : oracle.getObjects()){
			switch(EcoreUtil.getID(o)) {
			case "0" : expectedMatch.addMapping("1", o);
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
  		
//      assertTrue(vf2Test.isSuccess(),"Matcher failed");
        assertTrue(ms.equals(p.getCurrentMatchSet()),"Wrong match found");
		
	}
}
