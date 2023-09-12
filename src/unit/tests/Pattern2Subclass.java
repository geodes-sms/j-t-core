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
import tcore.constant.JTCoreConstant;
import tcore.messages.MatchSet;
import tcore.messages.Packet;
import tcore.strategy.Matcher;
import tcore.messages.Match;

/**
 * Unit test for a case with multiple matches found.
 *
 * @author Sebastien Ehouan
 * @author An Li
 * @since 2021-12-19
 */

class Pattern2Subclass {

	@Before
	public void setUp() {
	}

	@Test
	public void isSuccessVF2() throws Exception {

		System.out.println("------------------------VF2------------------------");

		// Same as previous one, but using VF2 instead
		utils.Utils.initialize();

		// Imports
		MetaModel OracleMM = new MetaModel("Oracle", "../Ramifier_New/Model/Subclasses/OracleV2.ecore"); // Oracle
																											// MetaModel
		MetaModel Oracle_ramified = new MetaModel("OracleRoot",
				"../Ramifier_New/Model/Subclasses/OracleV2_augmented.ecore"); // Ramified Oracle

		Model oracle = new Model("Oracle", "../Ramifier_New/Model/Subclasses/OracleV2.xmi", OracleMM); // Dynamic
																										// Instance from
																										// Oracle

		Pattern pre_A = new Pattern("pre_A", "../Ramifier_New/Model/Subclasses/Pattern2Subclass.xmi", Oracle_ramified);

		Packet p = new Packet(oracle);
		LHS lhs = new LHS(pre_A, null);

		// Testing
		Matcher tester = new Matcher(lhs, 5, JTCoreConstant.VF2_ALGORITHM);

		@SuppressWarnings("unused")
		Packet result = tester.packetIn(p);

		Match expectedMatch = new Match();

		for (EObject o : oracle.getObjects()) {
			switch (EcoreUtil.getID(o)) {
			case "1":
				expectedMatch.addMapping("1", o);
				break;
			case "3":
				expectedMatch.addMapping("5", o);
				break;
			case "2":
				expectedMatch.addMapping("2", o);
				break;
			default:
				break;
			}
		}

		// Array of matches expected to be found
		ArrayList<Match> expectedMatchArray = new ArrayList<Match>();
		expectedMatchArray.add(expectedMatch);

		// Expected MatchSet to find
		MatchSet ms = new MatchSet(expectedMatchArray, lhs);

		System.out.println("------------------------FIN VF2------------------------");

		assertTrue(tester.isSuccess(), "Matcher failed");

	}
}