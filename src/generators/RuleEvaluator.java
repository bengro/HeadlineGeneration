package generators;

import edu.stanford.nlp.trees.GrammaticalRelation;

import java.util.*;

public class RuleEvaluator {

    private HashMap<String, Rule> rules;

    public RuleEvaluator() {
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
        rules.put("xcomp", new XCOMPRule());
        rules.put("ccomp", new CCOMPRule());
        rules.put("prt", new PRTRule());
        // rules.put("conj", new CONJRule());
    }

    public Headline evaluateRules(Node root, int maxHeadlineLength) {

        LinkedList<Node> nodeQueue = new LinkedList<>();
        LinkedList<Node> nextLevel = new LinkedList<>();
        Headline output = new Headline();

        Node subsentenceNode = null;
        Node realRoot = null;

        for (Node node : root.getDependents()) {
            String type = node.getGrammaticalRelation().getShortName();
            if (type.equals("xcomp") || type.equals("ccomp")) {
                subsentenceNode = node;
                break;
            }
        }

        if (subsentenceNode != null && root.getDependents().size() < 3) {
            realRoot = subsentenceNode;
        } else {
            realRoot = root;
        }

        String[] afterGovernor = new String[]{"agent", "iobj", "dobj", "advmod", "prep", "dcomp", "xcomp", "prt", "conj"};
        Set<String> afterGov = new HashSet<>(Arrays.asList(afterGovernor));

        String[] beforeGovernor = new String[]{"nn", "amod"};
        Set<String> beforeGov = new HashSet<>(Arrays.asList(beforeGovernor));

        if (realRoot.getWord().length() > maxHeadlineLength)
            return output;

        output.add(realRoot);
        nodeQueue.addAll(realRoot.getDependents());

        for (int i = 1; i < 8; i++) {
            int length = nodeQueue.size();

            HashMap<Integer, Node> beforeNodes = new HashMap<>();
            HashMap<Integer, Node> afterNodes = new HashMap<>();
            HashMap<Integer, Node> otherNodes = new HashMap<>();

            for (int j = 0; j < length; j++) {
                Node node = nodeQueue.removeFirst();
                GrammaticalRelation rel = node.getGrammaticalRelation();
                String type = rel.getShortName();
                String[] splits = node.getIdentifier().split("-");
                int position = Integer.parseInt(splits[splits.length - 1]);

                if (beforeGov.contains(type)) {
                    beforeNodes.put(position, node);
                } else if (afterGov.contains(type)) {
                    afterNodes.put(position, node);
                } else {
                    otherNodes.put(j, node);
                }
            }

            evalRule(beforeNodes, output, nextLevel, maxHeadlineLength, false);
            evalRule(afterNodes, output, nextLevel, maxHeadlineLength, true);
            evalRule(otherNodes, output, nextLevel, maxHeadlineLength, false);

            nodeQueue = nextLevel;
        }

        return output;
    }

    private void evalRule(HashMap<Integer, Node> nodes, Headline output, LinkedList<Node> nextLevel, int maxHeadlineLength, boolean reverseOrder) {

        if (nodes.isEmpty())
            return;

        Rule rule = null;
        Set<Integer> sets = nodes.keySet();
        int firstKey = sets.iterator().next();

        int min = firstKey;
        int max = firstKey;
        for (Integer i : sets) {
            if (i < min)
                min = i;
            if (i > max)
                max = i;
        }

        for (int i = reverseOrder ? max : min ; reverseOrder ? i >= min : i <= max; i = reverseOrder ? i - 1 : i + 1) {

            Node node = nodes.get(i);

            if (node != null) {

                String type = node.getGrammaticalRelation().getShortName();

                if (type.matches("prep.*")) {
                    rule = rules.get("prep");
                } else if (type.matches("conj")){
                    rule = rules.get("conj");
                } else {
                    rule = rules.get(type);
                }

                if (rule != null && !node.isModified()) {
                    if (rule.apply(node, output, maxHeadlineLength)) {
                        for (Node n : node.getDependents()) {
                            if (!n.isModified()) {
                                nextLevel.add(n);
                            }
                        }
                    }
                }
            }
        }
    }
}
