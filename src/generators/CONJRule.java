package generators;

public class CONJRule implements Rule {
    @Override
    public boolean apply(Node currentNode, Headline output, int maxHeadlineLength) {
        String conj = currentNode.getGrammaticalRelation().getSpecific();

        int currentHeadlineSize = output.getHeadlineSize();
        if (currentNode.getWord().length() + conj.length() + 2 + currentHeadlineSize > maxHeadlineLength) {
            return false;
        }

        if (output.contains(currentNode))
            return false;

        Node governor = currentNode.getParent();
        int govIndex = output.lastIndexOf(governor);

        currentNode.setWord(conj + " " + currentNode.getWord());

        output.add(govIndex + 1, currentNode);

        return true;
    }
}
