package generators;

/**
 * Created with IntelliJ IDEA.
 * User: daspan
 * Date: 5/25/13
 * Time: 4:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class AGENTRule implements Rule {
    @Override
    public boolean apply(Node currentNode, Headline output) {
        int currentHeadlineSize = output.getHeadlineSize();
        if (currentNode.getWord().length() + "by ".length() + 1 + currentHeadlineSize > maxHeadlineLength){
            return false;
        }

        Node governor = currentNode.getParent();
        int govIndex = output.lastIndexOf(governor);

        assert govIndex >= 0;

        currentNode.setWord("by " + currentNode.getWord());
        output.add(govIndex + 1, currentNode);

        return true;
    }
}
