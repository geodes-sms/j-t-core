package jtcore.test;

import static org.junit.jupiter.api.Assertions.*;
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
        MetaModel tablesMM = new MetaModel("tables", "res/tables/tables.ecore");
        MetaModel tables_ramified = new MetaModel("RamRoot", "res/tables/tables_ramified.ecore");

        Model tables = new Model("tables", "res/tables/tables.xmi", tablesMM);

        Pattern fireMimi_pre = new Pattern("fireMimi_pre", "res/tables/fireMimi_pre.xmi", tables_ramified);
        LHS lhs = new LHS(fireMimi_pre, null);
        
        Packet p = new Packet(tables);

		VF2 vf2Test = new VF2();
		
		ArrayList<Match> result = vf2Test.match();
		ArrayList<Match> chosenMatches = new ArrayList<>();
  		
//      assertTrue(vf2Test.isSuccess(),"Matcher failed");
        
        MatchSet ms = new MatchSet(chosenMatches,lhs);

        assertTrue(ms.equals(p.getCurrentMatchSet()),"Wrong match found");
		
	}
}
