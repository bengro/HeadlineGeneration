package generators;

/**
 * Created with IntelliJ IDEA.
 * User: daspan
 * Date: 5/25/13
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class PREPRule implements Rule {
    @Override
    public boolean apply(Node currentNode, Headline output) {
        String preposition = currentNode.getGrammaticalRelation().getSpecific();

        int currentHeadlineSize = output.getHeadlineSize();
        if (currentNode.getWord().length() + preposition.length() + 2 + currentHeadlineSize > maxHeadlineLength){
            return false;
        }

        currentNode.setWord(preposition + " " + currentNode.getWord());

        Node governor = currentNode.getParent();
        int govIndex = output.lastIndexOf(governor);

        assert govIndex >= 0;

        output.add(govIndex + 1, currentNode);

        return true;
    }
}
