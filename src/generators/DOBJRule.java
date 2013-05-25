package generators;

public class DOBJRule implements Rule {
    @Override
    public boolean apply(Node currentNode, Headline output) {
        int currentHeadlineSize = output.getHeadlineSize();
        if (currentNode.getWord().length() + 1 + currentHeadlineSize > maxHeadlineLength){
            return false;
        }

        // verb
        Node governor = currentNode.getParent();
        int govIndex = output.lastIndexOf(governor);
        
        // add object after verb.
        output.add(govIndex + 1, currentNode);
        return true;
    }
}
