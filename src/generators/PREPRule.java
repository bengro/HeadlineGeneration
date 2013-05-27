package generators;

public class PREPRule implements Rule {
    @Override
    public boolean apply(Node currentNode, Headline output, int maxHeadlineLength) {
        String preposition =  "";
        String specific = currentNode.getGrammaticalRelation().getSpecific();

        if (specific != null){
            preposition += specific + " ";
        }

        int currentHeadlineSize = output.getHeadlineSize();
        if (currentNode.getWord().length() + preposition.length() + 1 + currentHeadlineSize > maxHeadlineLength){
            return false;
        }

        currentNode.setWord(preposition + currentNode.getWord());

        Node governor = currentNode.getParent();
        int govIndex = output.lastIndexOf(governor);

        assert govIndex >= 0;

        output.add(govIndex + 1, currentNode);

        return true;
    }
}
