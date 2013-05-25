package generators;

public class DOBJRule implements Rule {
    @Override
    public void apply(Node currentNode, Headline output) {
        int currentHeadlineSize = output.getHeadlineSize();
        if (currentNode.getWord().length() + 1 + currentHeadlineSize > 75){
            return;
        }

        // verb
        Node governor = currentNode.getParent();
        int govIndex = output.lastIndexOf(governor);
        
        // add object after verb.
        output.add(govIndex, currentNode);
        
    }
}
