package tcore;

import tcore.messages.Match;
import tcore.messages.MatchSet;
import tcore.messages.Packet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * File:
 * Description:
 * <p>
 * Package: tcore
 * Project: JT-Core
 * <p>
 * Date: 2017-12-01
 *
 * @author Pierre-Olivier Talbot
 * @version 1.0
 * @since 1.8.0_121
 */
public class Resolver extends RulePrimitive {

    private boolean externalMatchesOnly;
    private Method customResolution;

    public Resolver(boolean externalMatchesOnly, Method customResolution) {
        this.externalMatchesOnly = externalMatchesOnly;
        this.customResolution = customResolution;
    }

    @Override
    public Packet packetIn(Packet p) {
        exception = null;
        for (MatchSet ms : p.getMatchSets()) {
            if (externalMatchesOnly && ms == p.getCurrentMatchSet()) continue;
            if (ms.hasDirtyMatches()) {
                if (!customResolution(p)) {
                    if (!defaultResolution(p)) {
                        isSuccess = false;
                        exception = new Exception("Not implemented"); // FIXME: change exception to appropriate one
                    }
                }
            }
        }
        isSuccess = true;
        return p;
    }

    protected boolean defaultResolution(Packet p) {
        for (MatchSet ms : p.getMatchSets()) {
            if (externalMatchesOnly && p.getCurrentMatchSet() == ms) continue;
            if (ms.hasDirtyMatches()) {
                ArrayList<Match> toRemove = new ArrayList<>();
                for (Match m : ms.getMatches()) {
                    if (m.hasDirtyNodes()) {
                        toRemove.add(m);
                    }
                }
                ms.getMatches().removeAll(toRemove);
            }
        }
        return true;
    }

    protected boolean customResolution(Packet p) {
        if (customResolution != null) {
            try {
                customResolution.invoke(null, (Object) null);
                return true;
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
