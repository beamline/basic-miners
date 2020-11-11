package pmcep.miners.discovery_miner.view.graph.graphviz;

import java.util.Set;

/**
 * This code is part of the GraphViz ProM package.
 *
 * @author Sander Leemnans
 * @author Andrea Burattin
 * @see {@link http://promtools.org/}
 * @see {@link http://leemans.ch/}
 * @see {@link https://svn.win.tue.nl/repos/prom/Packages/GraphViz/Trunk/src/org/processmining/plugins/graphviz/dot/}
 */
public interface DotElement {

    public String getLabel();

    public void setLabel(String label);

    public void setOption(String key, String value);

    /**
     *
     * @param key
     * @return the value of the option if it was set, otherwise null.
     */
    public String getOption(String key);

    /**
     *
     * @return the set of options that is set (keys)
     */
    public Set<String> getOptionKeySet();

    public String getId();

}