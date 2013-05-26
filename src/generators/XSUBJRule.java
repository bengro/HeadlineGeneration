package generators;

public class XSUBJRule implements Rule {
    @Override
    public boolean apply(Node currentNode, Headline output, int maxHeadlineLength) {
        int currentHeadlineSize = output.getHeadlineSize();
        if (currentNode.getWord().length() + 1 + currentHeadlineSize > maxHeadlineLength) {
            return false;
        }

        if (!output.contains(currentNode))
            output.add(0, currentNode);

        return true;
    }
}
