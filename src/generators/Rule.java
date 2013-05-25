package generators;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.TypedDependency;

import java.util.LinkedList;

public interface Rule {
    public void apply(Node currentNode, Headline output);
}
