package generators;

public class NNRule implements Rule {
    @Override
    public void apply(Node currentNode, Headline output) {
        int currentHeadlineSize = output.getHeadlineSize();
        if (currentNode.getWord().length() + 1 + currentHeadlineSize > 75){
            return;
        }

        Node governor = currentNode.getParent();
        int govIndex = output.lastIndexOf(governor);
        output.add(govIndex, currentNode);
    }
}
