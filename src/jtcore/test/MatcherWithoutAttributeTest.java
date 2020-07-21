package jtcore.test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
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

class MatcherWithoutAttributeTest {
	
	@Before
	public void setUp() {
	}

	@Test
	public void isSuccess() throws Exception {
		utils.Utils.initialize();

        // Imports
        MetaModel tablesMM = new MetaModel("tables", "res/tables/tables.ecore");
        MetaModel tables_ramified = new MetaModel("RamRoot", "res/tables/tables_ramified.ecore");

        Model tables = new Model("tables", "res/tables/tables.xmi", tablesMM);

        Pattern fireMimi_pre = new Pattern("fireMimi_pre", "res/tables/fireMimi_pre.xmi", tables_ramified);

        Packet p = new Packet(tables);
        LHS lhs = new LHS(fireMimi_pre, null);
        
        //Testing
        Matcher tester = new Matcher(lhs, 1);  //max=1
        
        ArrayList<Match> chosenMatches = new ArrayList<>();
//        for (int i = 0; i < allMatches.size() && i < 1; i++) {  //1 is max
//            chosenMatches.add(allMatches.get(i));
//        }
        
        @SuppressWarnings("unused")
		Packet result = tester.packetIn(p);
  		
        assertTrue(tester.isSuccess(),"Matcher failed");
        
        MatchSet ms = new MatchSet(chosenMatches,lhs);

        assertTrue(ms.equals(p.getCurrentMatchSet()),"Wrong match found");
	}
}
