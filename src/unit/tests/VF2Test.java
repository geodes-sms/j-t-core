package unit.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import graph.Graph;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tcore.LHS;
import tcore.MetaModel;
import tcore.Model;
import tcore.Pattern;
import tcore.State;
import tcore.messages.MatchSet;
import tcore.messages.Packet;
import tcore.strategy.Matcher;
import tcore.strategy.VF2;
import tcore.messages.Match;
import tcore.strategy.*;

/**
 * Unit test case for matches using VF2.
 * 
 * YET TO FINALIZE -- DO NOT USE
 *
 * @author Sebastien EHouan
 * @since 2020-07-30
 */

class VF2Test {
	@Before
	public void setUp() {
	}

	
	@Test
	public void singleMatch1() throws Exception {
		utils.Utils.initialize();

        // Imports
//		Path graphPath = Paths.get("/Users/sebastien.ehouan/Documents/UdeM", "mygraphdb.data");
//		Path queryPath = Paths.get("/Users/sebastien.ehouan/Documents/UdeM", "Q4.my");
//		Path outPath = Paths.get("/Users/sebastien.ehouan/Documents/UdeM", "results_Q4.my");
//		
//		System.out.println("Target Graph Path: " + graphPath.toString());
//		System.out.println("Query Graph Path: " + queryPath.toString());
//		System.out.println("Output Path: " + outPath.toString());
//		System.out.println();
//		
//		long startMilli = System.currentTimeMillis();
//		
//		PrintWriter writer = new PrintWriter(outPath.toFile());
//        
//        //Testing
//		ArrayList<Graph> graphSet = loadGraphSetFromFile(graphPath, "Graph ");
//		ArrayList<Graph> querySet = loadGraphSetFromFile(queryPath, "Query ");
//
//		VF2 vf2= new VF2();
//		
//		int queryCnt = 0;
//		for (Graph queryGraph : querySet){
//			queryCnt++;
//			ArrayList<State> stateSet = vf2.matchGraphSetWithQuery(graphSet, queryGraph);
//			if (stateSet.isEmpty()){
//				System.out.println("Cannot find a match for: " + queryGraph.name);
//				printTimeFlapse(startMilli);
//				printAverageMatchingTime(startMilli, queryCnt);
//				System.out.println();
//				
//				writer.write("Cannot find a match for: " + queryGraph.name + "\n\n");
//				writer.flush();
//			} else {
//				System.out.println("Found " + stateSet.size() + " matches for: " + queryGraph.name);
//				printTimeFlapse(startMilli);
//				printAverageMatchingTime(startMilli, queryCnt);
//				System.out.println();
//				
//				writer.write("Matches for: " + queryGraph.name + "\n");
//				for (State state : stateSet){
//					writer.write("In: " + state.targetGraph.name + "\n");
//					state.printMapping();
//					state.writeMapping(writer);
//				}		
//				writer.write("\n");
//				writer.flush();
//			}
//		}
//  		
//        assertTrue(tester.isSuccess(),"Matcher failed");
	}
}
