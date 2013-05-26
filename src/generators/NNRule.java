package generators;

public class NNRule implements Rule {
    @Override
    public boolean apply(Node currentNode, Headline output, int maxHeadlineLength) {
        int currentHeadlineSize = output.getHeadlineSize();
        if (currentNode.getWord().length() + 1 + currentHeadlineSize > maxHeadlineLength){
            return false;
        }

        Node governor = currentNode.getParent();
        int govIndex = output.lastIndexOf(governor);

        assert govIndex >= 0;

        output.add(govIndex, currentNode);

        for (Node n: currentNode.getDependents()){
            String type = n.getGrammaticalRelation().getShortName();
            if (type.equals("nn")){
                this.apply(n, output, maxHeadlineLength);
                n.setModified(true);
            }
        }

        return true;
    }
}
