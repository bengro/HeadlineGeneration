package generators;

/**
 * Created with IntelliJ IDEA.
 * User: daspan
 * Date: 5/27/13
 * Time: 1:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class PRTRule implements Rule {

    @Override
    public boolean apply(Node currentNode, Headline output, int maxHeadlineLength) {
        int currentHeadlineSize = output.getHeadlineSize();
        if (currentNode.getWord().length() + 1 + currentHeadlineSize > maxHeadlineLength) {
            output.removeLast();
        }

        Node governor = currentNode.getParent();
        int govIndex = output.lastIndexOf(governor);

        output.add(govIndex + 1, currentNode);
        return true;
    }

}
