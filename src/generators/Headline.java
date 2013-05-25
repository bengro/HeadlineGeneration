package generators;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: daspan
 * Date: 5/25/13
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Headline extends LinkedList<Node> {
    public int getHeadlineSize(){
        int size = 0;
        int words = 0;
        for (Node node: this){
            size += node.getWord().length();
            words++;
        }

        return size + words - 1;
    }
}
