package generators;

/**
 * Created with IntelliJ IDEA.
 * User: daspan
 * Date: 5/25/13
 * Time: 5:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class XSUBJRule implements Rule {
    @Override
    public boolean apply(Node currentNode, Headline output) {
        int currentHeadlineSize = output.getHeadlineSize();
        if (currentNode.getWord().length() + 1 + currentHeadlineSize > maxHeadlineLength) {
            return false;
        }

        if (!output.contains(currentNode))
            output.add(0, currentNode);

        return true;
    }
}
