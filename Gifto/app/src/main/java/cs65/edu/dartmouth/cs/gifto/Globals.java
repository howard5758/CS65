package cs65.edu.dartmouth.cs.gifto;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiron on 2/24/18.
 */

public class Globals {

    public static final String DEFAULT_BACKGROUND = "default_bg";
    public static final String EMPTY = "empty";
    public static final String DEFAULT_PLACE2 = "pile_of_leaves";
    public static final String DEFAULT_PLACE3 = "trunk";
    // list of animals, animal size and items, gifts, and gift size and money hashmap
    public static final Map<String, Integer> ANIMAL_TO_TYPE = new HashMap<String, Integer>();
    public static final Map<String, Integer> ITEM_TO_TYPE = new HashMap<String, Integer>();
    public static final Map<String, Integer> GIFT_TO_TYPE = new HashMap<String, Integer>();

    public Globals(){
        /** animals: 0: small, can only carry messages
         * 1: medium, can carry messages and small items
         * 2: large, can carry messages and small item or large items
         */
        ANIMAL_TO_TYPE.put("sleeping alligator", 2);
        ANIMAL_TO_TYPE.put("alligator", 2);
        ANIMAL_TO_TYPE.put("sleeping bat", 0);
        ANIMAL_TO_TYPE.put("bat", 0);
        ANIMAL_TO_TYPE.put("cat's head", 1);
        ANIMAL_TO_TYPE.put("sleeping cat", 1);
        ANIMAL_TO_TYPE.put("cat", 1);
        ANIMAL_TO_TYPE.put("kangaroo", 2);
        ANIMAL_TO_TYPE.put("sleeping kangaroo", 2);
        ANIMAL_TO_TYPE.put("monkey", 1);
        ANIMAL_TO_TYPE.put("monkey's head", 1);
        ANIMAL_TO_TYPE.put("sleeping monkey", 1);
        ANIMAL_TO_TYPE.put("monkey on tree with banana", 1);
        ANIMAL_TO_TYPE.put("monkey on tree", 1);
        ANIMAL_TO_TYPE.put("owl", 0);
        ANIMAL_TO_TYPE.put("sleeping owl", 0);
        ANIMAL_TO_TYPE.put("American Shorthair's head", 1);
        ANIMAL_TO_TYPE.put("sleeping American Shorthair", 1);
        ANIMAL_TO_TYPE.put("American Shorthair", 1);
        ANIMAL_TO_TYPE.put("sleeping Siamese Cat", 1);
        ANIMAL_TO_TYPE.put("Siamese Cat", 1);
        ANIMAL_TO_TYPE.put("Siamese Cat's head", 1);
        ANIMAL_TO_TYPE.put("sleeping pink squirrel", 0);
        ANIMAL_TO_TYPE.put("pink squirrel", 0);
        ANIMAL_TO_TYPE.put("sleeping squirrel", 0);
        ANIMAL_TO_TYPE.put("squirrel", 0);
        ANIMAL_TO_TYPE.put("unicorn with grass", 2);
        ANIMAL_TO_TYPE.put("sleeping unicorn", 2);
        ANIMAL_TO_TYPE.put("giggling unicorn", 2);
        ANIMAL_TO_TYPE.put("unicorn", 2);
        ANIMAL_TO_TYPE.put("Corgi's back", 1);
        ANIMAL_TO_TYPE.put("Corgi", 1);
        ANIMAL_TO_TYPE.put("sleeping Corgi", 1);
        ANIMAL_TO_TYPE.put("Corgi's side", 1);

        /** items: 0: food, place1 in garden, can't be given as gift
         * 1: small, place2, place3, or place4 in garden,
         *      carried by medium and large animals
         * 2: large, place4 in garden, carried by large animals
         */
        ITEM_TO_TYPE.put("boxing glove",1);
        ITEM_TO_TYPE.put("butterfly teaser",1);
        ITEM_TO_TYPE.put("white cushion",1);
        ITEM_TO_TYPE.put("blue cloud cushion",1);
        ITEM_TO_TYPE.put("orange cloud cushion",1);
        ITEM_TO_TYPE.put("green cushion",1);
        ITEM_TO_TYPE.put("pink cushion",1);
        ITEM_TO_TYPE.put("purple cushion",1);
        ITEM_TO_TYPE.put("yellow cushion",1);
        ITEM_TO_TYPE.put("flower pot 1",1);
        ITEM_TO_TYPE.put("flower pot 2",1);
        ITEM_TO_TYPE.put("flower pot 3",1);
        ITEM_TO_TYPE.put("giant acorn",2);
        ITEM_TO_TYPE.put("grapes",0);
        ITEM_TO_TYPE.put("hotdog",0);
        ITEM_TO_TYPE.put("magic egg",1);
        ITEM_TO_TYPE.put("blue mitten",1);
        ITEM_TO_TYPE.put("yellow mitten",1);
        ITEM_TO_TYPE.put("brown pot",1);
        ITEM_TO_TYPE.put("green pot",1);
        ITEM_TO_TYPE.put("salad",0);
        ITEM_TO_TYPE.put("yellow cushion",1);
        ITEM_TO_TYPE.put("blue toy fish",1);
        ITEM_TO_TYPE.put("yellow toy fish",1);
        ITEM_TO_TYPE.put("toy reindeer",1);
        ITEM_TO_TYPE.put("banana",0);
        ITEM_TO_TYPE.put("pool",2);
        ITEM_TO_TYPE.put("tennis ball",1);
        ITEM_TO_TYPE.put("trunk",2);
        ITEM_TO_TYPE.put("tuna",0);
        ITEM_TO_TYPE.put("pile of leaves", 1);

        /** gifts: 0: small, can only carry messages
         * 1: medium, can carry messages and small items
         * 2: large, can carry messages and small item or large items
         */
        GIFT_TO_TYPE.put("blue bag",1);
        GIFT_TO_TYPE.put("green bag",1);
        GIFT_TO_TYPE.put("pink bag",1);
        GIFT_TO_TYPE.put("red bag",1);
        GIFT_TO_TYPE.put("watermelon bag",1);
        GIFT_TO_TYPE.put("blue box",1);
        GIFT_TO_TYPE.put("green box",1);
        GIFT_TO_TYPE.put("purple box",1);
        GIFT_TO_TYPE.put("red box",1);
        GIFT_TO_TYPE.put("yellow box",1);
        GIFT_TO_TYPE.put("black mole bag",1);
        GIFT_TO_TYPE.put("blue mole bag",1);
        GIFT_TO_TYPE.put("green mole bag",1);
        GIFT_TO_TYPE.put("purple mole bag",1);
        GIFT_TO_TYPE.put("tiffany box",1);
        GIFT_TO_TYPE.put("god food bag",1);
        GIFT_TO_TYPE.put("lucky bag",1);
    }


}
