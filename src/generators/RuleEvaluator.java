package generators;

import edu.stanford.nlp.trees.TreeGraphNode;

import java.util.HashMap;
import java.util.LinkedList;

public class RuleEvaluator {

    private HashMap<String, Rule> rules;

    public RuleEvaluator(){
        rules = new HashMap<>();

        rules.put("nn", new NNRule());
        rules.put("amod", new AMODRule());
        rules.put("nsubj", new NSUBJRule());
        rules.put("nsubjpass", new NSUBJRule());
        rules.put("xsubj", new XSUBJRule());
        rules.put("agent", new AGENTRule());
        rules.put("iobj", new DOBJRule());
        rules.put("dobj", new DOBJRule());
        rules.put("advmod", new ADVMODRule());
        rules.put("prep", new PREPRule());
    }

    public Headline evaluateRules(Node root){

        LinkedList<Node> nodeQueue = new LinkedList<>();
        LinkedList<Node> nextLevel = new LinkedList<>();
        Headline output = new Headline();

        // Handle root node.

        output.add(root);

        for (Node node: root.getDependents())
            nodeQueue.add(node);

        for (int i=1; i < 3; i++){
            for(Node node: nodeQueue){
                String type = node.getGrammaticalRelation().getShortName();
                Rule rule = null;
                if (type.matches("prep.*")){
                    rule = rules.get("prep");
                } else {
                    rule = rules.get(type);
                }
                if (rule != null){
                    if (rule.apply(node, output));
                        nextLevel.addAll(node.getDependents());
                }
            }

            nodeQueue = nextLevel;
            // nextLevel = new LinkedList<>();
        }

        return output;
    }
}
