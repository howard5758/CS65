package cs65.edu.dartmouth.cs.gifto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiron on 2/24/18.
 */

public class Globals {

    public static final String DEFAULT_BACKGROUND = "default_bg";
    public static final String EMPTY = "empty";

    // list of animals, animal size and items, gifts, and gift size and money hashmap
    public static final Map<String, Integer> ANIMAL_TO_TYPE = new HashMap<String, Integer>();
    public static final Map<String, String> ANIMAL_TO_NIGHT = new HashMap<String, String>();
    public static final Map<String, String> NIGHT_TO_ANIMAL = new HashMap<String, String>();
    public static final Map<String, Integer> ITEM_TO_TYPE = new HashMap<String, Integer>();
    public static final Map<String, Integer> ITEM_TO_PRICE = new HashMap<String, Integer>();
    public static final Map<String, Integer> BOX_TO_TYPE = new HashMap<String, Integer>();

    public static final Map<String, Double> ANIMAL_TO_PROB = new HashMap<String, Double>();
    public static final Map<String, String> ANIMAL_TO_GIFT = new HashMap<String, String>();
    public static final Map<String, ArrayList<String>> ITEM_TO_ANIMAL_LIST =
            new HashMap<String, ArrayList<String>>();
    public static final Map<String, ArrayList<String>> ANIMAL_TO_BOX_LIST =
            new HashMap<String, ArrayList<String>>();
    public static final ArrayList<String> INT_TO_BOX = new ArrayList<String> (Arrays.asList("", "blue bag", "green bag", "pink bag", "red bag", "black mole bag", "blue mole bag", "green mole bag", "purple mole bag", "watermelon bag", "blue box", "green box", "purple box", "red box", "yellow box","tiffany box", "envelope”, “black mole bag", "blue mole bag", "green mole bag", "purple mole bag", "tiffany box", "dog food bag", "lucky bag", "envelope"));

    static {
        /** animals: 0: small, can only carry messages
         * 1: medium, can carry messages and small items
         * 2: large, can carry messages and small item or large items
         */
        ANIMAL_TO_TYPE.put("sleeping alligator", 2);
        ANIMAL_TO_TYPE.put("alligator", 2);
        ANIMAL_TO_TYPE.put("sleeping bat", 0);
        ANIMAL_TO_TYPE.put("bat", 0);
        ANIMAL_TO_TYPE.put("sleeping cat", 1);
        ANIMAL_TO_TYPE.put("cat", 1);
        ANIMAL_TO_TYPE.put("kangaroo", 2);
        ANIMAL_TO_TYPE.put("sleeping kangaroo", 2);
        ANIMAL_TO_TYPE.put("monkey", 1);
        ANIMAL_TO_TYPE.put("sleeping monkey", 1);
        ANIMAL_TO_TYPE.put("monkey on tree with banana", 1);
        ANIMAL_TO_TYPE.put("monkey on tree", 1);
        ANIMAL_TO_TYPE.put("owl", 0);
        ANIMAL_TO_TYPE.put("sleeping owl", 0);
        ANIMAL_TO_TYPE.put("sleeping American Shorthair", 1);
        ANIMAL_TO_TYPE.put("American Shorthair", 1);
        ANIMAL_TO_TYPE.put("sleeping Siamese Cat", 1);
        ANIMAL_TO_TYPE.put("Siamese Cat", 1);
        ANIMAL_TO_TYPE.put("sleeping pink squirrel", 0);
        ANIMAL_TO_TYPE.put("pink squirrel", 0);
        ANIMAL_TO_TYPE.put("sleeping squirrel", 0);
        ANIMAL_TO_TYPE.put("squirrel", 0);
        ANIMAL_TO_TYPE.put("unicorn with grass", 2);
        ANIMAL_TO_TYPE.put("sleeping unicorn", 2);
        ANIMAL_TO_TYPE.put("giggling unicorn", 2);
        ANIMAL_TO_TYPE.put("unicorn", 2);
        ANIMAL_TO_TYPE.put("Corgi back", 1);
        ANIMAL_TO_TYPE.put("Corgi", 1);
        ANIMAL_TO_TYPE.put("sleeping Corgi", 1);
        ANIMAL_TO_TYPE.put("Corgi side", 1);

        // night image of each animal
        ANIMAL_TO_NIGHT.put("alligator", "sleeping alligator");
        ANIMAL_TO_NIGHT.put("sleeping bat", "bat");
        ANIMAL_TO_NIGHT.put("cat", "sleeping cat");
        ANIMAL_TO_NIGHT.put("kangaroo", "sleeping kangaroo");
        ANIMAL_TO_NIGHT.put("monkey", "sleeping monkey");
        ANIMAL_TO_NIGHT.put("monkey on tree with banana", "monkey on tree with banana");
        ANIMAL_TO_NIGHT.put("monkey on tree", "monkey on tree");
        ANIMAL_TO_NIGHT.put("sleeping owl", "owl");
        ANIMAL_TO_NIGHT.put("American Shorthair", "sleeping American Shorthair");
        ANIMAL_TO_NIGHT.put("Siamese Cat", "sleeping Siamese Cat");
        ANIMAL_TO_NIGHT.put("pink squirrel", "sleeping pink squirrel");
        ANIMAL_TO_NIGHT.put("squirrel", "sleeping squirrel");
        ANIMAL_TO_NIGHT.put("unicorn with grass", "sleeping unicorn");
        ANIMAL_TO_NIGHT.put("giggling unicorn", "sleeping unicorn");
        ANIMAL_TO_NIGHT.put("unicorn", "sleeping unicorn");
        ANIMAL_TO_NIGHT.put("Corgi back", "sleeping Corgi");
        ANIMAL_TO_NIGHT.put("Corgi", "sleeping Corgi");
        ANIMAL_TO_NIGHT.put("Corgi side", "sleeping Corgi");

        NIGHT_TO_ANIMAL.put("sleeping alligator", "alligator");
        NIGHT_TO_ANIMAL.put("bat", "sleeping bat");
        NIGHT_TO_ANIMAL.put("sleeping cat", "cat");
        NIGHT_TO_ANIMAL.put("sleeping kangaroo", "kangaroo");
        NIGHT_TO_ANIMAL.put("sleeping monkey", "monkey");
        NIGHT_TO_ANIMAL.put("monkey on tree with banana", "monkey on tree with banana");
        NIGHT_TO_ANIMAL.put("monkey on tree", "monkey on tree");
        NIGHT_TO_ANIMAL.put("owl", "sleeping owl");
        NIGHT_TO_ANIMAL.put("sleeping American Shorthair", "American Shorthair");
        NIGHT_TO_ANIMAL.put("sleeping Siamese Cat", "Siamese Cat");
        NIGHT_TO_ANIMAL.put("sleeping pink squirrel", "pink squirrel");
        NIGHT_TO_ANIMAL.put("sleeping squirrel", "squirrel");
        NIGHT_TO_ANIMAL.put("sleeping unicorn", "unicorn");
        NIGHT_TO_ANIMAL.put("sleeping Corgi", "Corgi");

        /** items: 0: food, place1 in garden, can't be given as gift
         * 1: small, place2, place3, or place4 in garden, carried by medium and large animals
         * 2: large, place2, place3 in garden, carried by large animals
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
        ITEM_TO_TYPE.put("giant blue mitten",1);
        ITEM_TO_TYPE.put("giant yellow mitten",1);
        ITEM_TO_TYPE.put("brown pot",1);
        ITEM_TO_TYPE.put("green pot",1);
        ITEM_TO_TYPE.put("salad",0);
        ITEM_TO_TYPE.put("yellow toy fish",1);
        ITEM_TO_TYPE.put("blue toy fish",1);
        ITEM_TO_TYPE.put("toy reindeer",1);
        ITEM_TO_TYPE.put("banana",0);
        ITEM_TO_TYPE.put("pool",2);
        ITEM_TO_TYPE.put("tennis ball",1);
        ITEM_TO_TYPE.put("trunk",2);
        ITEM_TO_TYPE.put("tree",2);
        ITEM_TO_TYPE.put("tuna",0);
        ITEM_TO_TYPE.put("pile of leaves", 1);

        // price of each item in shop
        ITEM_TO_PRICE.put("white cushion",30);
        ITEM_TO_PRICE.put("blue cloud cushion",30);
        ITEM_TO_PRICE.put("pink cushion",30);
        ITEM_TO_PRICE.put("purple cushion",30);
        ITEM_TO_PRICE.put("yellow cushion",30);
        ITEM_TO_PRICE.put("flower pot 1",40);
        ITEM_TO_PRICE.put("flower pot 2",35);
        ITEM_TO_PRICE.put("flower pot 3",40);
        ITEM_TO_PRICE.put("grapes",15);
        ITEM_TO_PRICE.put("hotdog",15);
        ITEM_TO_PRICE.put("green pot",50);
        ITEM_TO_PRICE.put("salad",15);
        ITEM_TO_PRICE.put("blue toy fish",55);
        ITEM_TO_PRICE.put("banana",10);
        ITEM_TO_PRICE.put("pool",100);
        ITEM_TO_PRICE.put("tennis ball",20);
        ITEM_TO_PRICE.put("trunk",80);
        ITEM_TO_PRICE.put("tree",100);
        ITEM_TO_PRICE.put("tuna",20);
        ITEM_TO_PRICE.put("pile of leaves", 10);

        /** gifts: 0: small, can only carry messages
         * 1: medium, can carry messages and small items
         * 2: large, can carry messages and small item or large items
         */
        BOX_TO_TYPE.put("blue bag",2);
        BOX_TO_TYPE.put("green bag",2);
        BOX_TO_TYPE.put("pink bag",2);
        BOX_TO_TYPE.put("red bag",2);
        BOX_TO_TYPE.put("watermelon bag",1);
        BOX_TO_TYPE.put("blue box",1);
        BOX_TO_TYPE.put("green box",1);
        BOX_TO_TYPE.put("purple box",1);
        BOX_TO_TYPE.put("red box",1);
        BOX_TO_TYPE.put("yellow box",1);
        BOX_TO_TYPE.put("black mole bag",2);
        BOX_TO_TYPE.put("blue mole bag",2);
        BOX_TO_TYPE.put("green mole bag",2);
        BOX_TO_TYPE.put("purple mole bag",2);
        BOX_TO_TYPE.put("tiffany box",0);
        BOX_TO_TYPE.put("dog food bag",0);
        BOX_TO_TYPE.put("lucky bag",0);
        BOX_TO_TYPE.put("envelope",0);

        // probability for an animal to visit
        ANIMAL_TO_PROB.put("sleeping alligator", .4);
        ANIMAL_TO_PROB.put("alligator", .4);
        ANIMAL_TO_PROB.put("sleeping bat", .35);
        ANIMAL_TO_PROB.put("bat", .35);
        ANIMAL_TO_PROB.put("sleeping cat", .35);
        ANIMAL_TO_PROB.put("cat", .45);
        ANIMAL_TO_PROB.put("kangaroo", .3);
        ANIMAL_TO_PROB.put("sleeping kangaroo", .3);
        ANIMAL_TO_PROB.put("monkey", .35);
        ANIMAL_TO_PROB.put("sleeping monkey", .35);
        ANIMAL_TO_PROB.put("monkey on tree with banana", .1);
        ANIMAL_TO_PROB.put("monkey on tree", .5);
        ANIMAL_TO_PROB.put("owl", .5);
        ANIMAL_TO_PROB.put("sleeping owl", .5);
        ANIMAL_TO_PROB.put("sleeping American Shorthair", .35);
        ANIMAL_TO_PROB.put("American Shorthair", .35);
        ANIMAL_TO_PROB.put("sleeping Siamese Cat", .3);
        ANIMAL_TO_PROB.put("Siamese Cat", .3);
        ANIMAL_TO_PROB.put("sleeping pink squirrel", .35);
        ANIMAL_TO_PROB.put("pink squirrel", .35);
        ANIMAL_TO_PROB.put("sleeping squirrel", .35);
        ANIMAL_TO_PROB.put("squirrel", .35);
        ANIMAL_TO_PROB.put("unicorn with grass", .15);
        ANIMAL_TO_PROB.put("sleeping unicorn", .15);
        ANIMAL_TO_PROB.put("giggling unicorn", .15);
        ANIMAL_TO_PROB.put("unicorn", .15);
        ANIMAL_TO_PROB.put("Corgi back", .45);
        ANIMAL_TO_PROB.put("Corgi", .01);
        ANIMAL_TO_PROB.put("sleeping Corgi", .1);
        ANIMAL_TO_PROB.put("Corgi side", .01);

        // gift each animal brings after certain times of visits
        ANIMAL_TO_GIFT.put("alligator", "blue toy fish");
        ANIMAL_TO_GIFT.put("bat", "butterfly teaser");
        ANIMAL_TO_GIFT.put("cat", "giant blue mitten");
        ANIMAL_TO_GIFT.put("kangaroo", "orange cloud cushion");
        ANIMAL_TO_GIFT.put("monkey", "giant yellow mitten");
        ANIMAL_TO_GIFT.put("owl", "yellow toy fish");
        ANIMAL_TO_GIFT.put("American Shorthair", "toy reindeer");
        ANIMAL_TO_GIFT.put("Siamese Cat", "green cushion");
        ANIMAL_TO_GIFT.put("pink squirrel", "boxing glove");
        ANIMAL_TO_GIFT.put("squirrel", "giant acorn");
        ANIMAL_TO_GIFT.put("unicorn", "magic egg");
        ANIMAL_TO_GIFT.put("Corgi", "brown pot");


        // animals that could come for a item
        ITEM_TO_ANIMAL_LIST.put("boxing glove",new ArrayList<String> (Arrays.asList("kangaroo","Corgi back", "Corgi", "Corgi side","monkey")));
        ITEM_TO_ANIMAL_LIST.put("butterfly teaser",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair","Corgi back", "Corgi", "Corgi side","giggling unicorn", "unicorn")));
        ITEM_TO_ANIMAL_LIST.put("white cushion",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair","Corgi back", "Corgi", "Corgi side","monkey", "pink squirrel", "squirrel")));
        ITEM_TO_ANIMAL_LIST.put("blue cloud cushion",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair")));
        ITEM_TO_ANIMAL_LIST.put("orange cloud cushion",new ArrayList<String> (Arrays.asList("Corgi back", "Corgi", "Corgi side","monkey", "pink squirrel", "squirrel")));
        ITEM_TO_ANIMAL_LIST.put("green cushion",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair","Corgi back", "Corgi", "Corgi side","monkey", "pink squirrel", "squirrel")));
        ITEM_TO_ANIMAL_LIST.put("pink cushion",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair","Corgi back", "Corgi", "Corgi side","monkey", "pink squirrel", "squirrel")));
        ITEM_TO_ANIMAL_LIST.put("purple cushion",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair","Corgi back", "Corgi", "Corgi side","monkey", "pink squirrel", "squirrel")));
        ITEM_TO_ANIMAL_LIST.put("yellow cushion",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair","Corgi back", "Corgi", "Corgi side","monkey", "pink squirrel", "squirrel")));
        ITEM_TO_ANIMAL_LIST.put("flower pot 1",new ArrayList<String> (Arrays.asList("cat","American Shorthair","Siamese Cat","Corgi","monkey","pink squirrel", "squirrel")));
        ITEM_TO_ANIMAL_LIST.put("flower pot 2",new ArrayList<String> (Arrays.asList("cat","American Shorthair","Siamese Cat","Corgi","monkey","pink squirrel", "squirrel")));
        ITEM_TO_ANIMAL_LIST.put("flower pot 3",new ArrayList<String> (Arrays.asList("cat","American Shorthair","Siamese Cat","Corgi","monkey","pink squirrel", "squirrel")));
        ITEM_TO_ANIMAL_LIST.put("giant acorn",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair", "Corgi back", "Corgi", "Corgi side","pink squirrel", "squirrel","unicorn with grass", "giggling unicorn", "unicorn","kangaroo","monkey")));
        ITEM_TO_ANIMAL_LIST.put("grapes",new ArrayList<String> (Arrays.asList("Corgi back", "Corgi", "Corgi side","pink squirrel", "squirrel","monkey")));
        ITEM_TO_ANIMAL_LIST.put("hotdog",new ArrayList<String> (Arrays.asList("Corgi back", "Corgi", "Corgi side","monkey")));
        ITEM_TO_ANIMAL_LIST.put("magic egg",new ArrayList<String> (Arrays.asList("alligator","unicorn with grass", "giggling unicorn", "unicorn","kangaroo","pink squirrel")));
        ITEM_TO_ANIMAL_LIST.put("giant blue mitten",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair", "Corgi back", "Corgi", "Corgi side","sleeping Siamese Cat")));
        ITEM_TO_ANIMAL_LIST.put("giant yellow mitten",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair", "Corgi back", "Corgi", "Corgi side","sleeping Siamese Cat")));
        ITEM_TO_ANIMAL_LIST.put("brown pot",new ArrayList<String> (Arrays.asList("cat","American Shorthair","Siamese Cat","Corgi","monkey","pink squirrel", "squirrel")));
        ITEM_TO_ANIMAL_LIST.put("green pot",new ArrayList<String> (Arrays.asList("cat","American Shorthair","Siamese Cat","Corgi","monkey","pink squirrel", "squirrel")));
        ITEM_TO_ANIMAL_LIST.put("salad",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair", "Corgi back", "Corgi", "Corgi side","kangaroo","pink squirrel", "squirrel", "unicorn with grass")));
        ITEM_TO_ANIMAL_LIST.put("yellow cushion",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair","Corgi back", "Corgi", "Corgi side","monkey", "pink squirrel", "squirrel")));
        ITEM_TO_ANIMAL_LIST.put("blue toy fish",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair", "Corgi back", "Corgi", "Corgi side")));
        ITEM_TO_ANIMAL_LIST.put("yellow toy fish",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair", "Corgi back", "Corgi", "Corgi side")));
        ITEM_TO_ANIMAL_LIST.put("toy reindeer",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair", "Corgi", "Corgi side","kangaroo","pink squirrel", "squirrel", "unicorn with grass","giggling unicorn", "unicorn")));
        ITEM_TO_ANIMAL_LIST.put("banana",new ArrayList<String> (Arrays.asList("monkey","Corgi", "Corgi side")));
        ITEM_TO_ANIMAL_LIST.put("pool",new ArrayList<String> (Arrays.asList("alligator","Corgi","monkey")));
        ITEM_TO_ANIMAL_LIST.put("tennis ball",new ArrayList<String> (Arrays.asList("Corgi back", "Corgi", "Corgi side", "kangaroo", "monkey")));
        ITEM_TO_ANIMAL_LIST.put("trunk",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair", "Corgi back", "Corgi", "Corgi side","pink squirrel", "squirrel","monkey")));
        ITEM_TO_ANIMAL_LIST.put("tuna",new ArrayList<String> (Arrays.asList("cat", "Siamese Cat", "American Shorthair")));
        ITEM_TO_ANIMAL_LIST.put("pile of leaves", new ArrayList<String> (Arrays.asList("sleeping American Shorthair","sleeping cat","sleeping American Shorthair","Corgi back")));
        ITEM_TO_ANIMAL_LIST.put("tree", new ArrayList<String> (Arrays.asList("monkey", "monkey on tree with banana", "monkey on tree","pink squirrel", "squirrel","sleeping owl", "sleeping bat")));

        
        // boxes an animal can bring
        ANIMAL_TO_BOX_LIST.put("sleeping alligator",new ArrayList<String> (Arrays.asList("blue bag", "green bag", "pink bag", "red bag")));
        ANIMAL_TO_BOX_LIST.put("alligator",new ArrayList<String> (Arrays.asList("blue bag", "green bag", "pink bag", "red bag")));
        ANIMAL_TO_BOX_LIST.put("sleeping bat",new ArrayList<String> (Arrays.asList("tiffany box", "dog food bag", "lucky bag", "envelope")));
        ANIMAL_TO_BOX_LIST.put("bat",new ArrayList<String> (Arrays.asList("tiffany box", "dog food bag", "lucky bag", "envelope")));
        ANIMAL_TO_BOX_LIST.put("sleeping cat",new ArrayList<String> (Arrays.asList("watermelon bag", "blue box", "green box", "purple box", "red box", "yellow box","tiffany box", "envelope")));
        ANIMAL_TO_BOX_LIST.put("cat",new ArrayList<String> (Arrays.asList("watermelon bag", "blue box", "green box", "purple box", "tiffany box", "envelope")));
        ANIMAL_TO_BOX_LIST.put("kangaroo",new ArrayList<String> (Arrays.asList("blue bag", "green bag", "pink bag", "red bag", "watermelon bag","lucky bag","black mole bag", "blue mole bag", "green mole bag", "purple mole bag")));
        ANIMAL_TO_BOX_LIST.put("sleeping kangaroo",new ArrayList<String> (Arrays.asList("blue bag", "green bag", "pink bag", "red bag", "watermelon bag","lucky bag","black mole bag", "blue mole bag", "green mole bag", "purple mole bag")));
        ANIMAL_TO_BOX_LIST.put("monkey",new ArrayList<String> (Arrays.asList("watermelon bag", "purple box", "red box", "yellow box","tiffany box", "envelope")));
        ANIMAL_TO_BOX_LIST.put("sleeping monkey",new ArrayList<String> (Arrays.asList("watermelon bag", "blue box", "green box", "purple box", "red box", "yellow box","tiffany box", "envelope")));
        ANIMAL_TO_BOX_LIST.put("monkey on tree with banana",new ArrayList<String> (Arrays.asList("watermelon bag", "blue box", "purple box", "red box", "yellow box","tiffany box", "envelope")));
        ANIMAL_TO_BOX_LIST.put("monkey on tree",new ArrayList<String> (Arrays.asList("watermelon bag", "blue box", "green box", "red box", "yellow box","tiffany box", "envelope")));
        ANIMAL_TO_BOX_LIST.put("owl",new ArrayList<String> (Arrays.asList("tiffany box", "dog food bag", "lucky bag", "envelope")));
        ANIMAL_TO_BOX_LIST.put("sleeping owl",new ArrayList<String> (Arrays.asList("tiffany box", "dog food bag", "lucky bag", "envelope")));
        ANIMAL_TO_BOX_LIST.put("sleeping American Shorthair",new ArrayList<String> (Arrays.asList("watermelon bag", "blue box", "green box", "purple box", "red box", "yellow box","tiffany box", "envelope")));
        ANIMAL_TO_BOX_LIST.put("American Shorthair",new ArrayList<String> (Arrays.asList("watermelon bag", "blue box", "green box", "purple box", "red box", "yellow box","tiffany box", "envelope")));
        ANIMAL_TO_BOX_LIST.put("sleeping Siamese Cat",new ArrayList<String> (Arrays.asList("watermelon bag", "blue box", "green box", "purple box", "red box", "yellow box","tiffany box", "envelope")));
        ANIMAL_TO_BOX_LIST.put("Siamese Cat",new ArrayList<String> (Arrays.asList("watermelon bag", "blue box", "green box", "purple box", "red box", "yellow box","tiffany box", "envelope")));
        ANIMAL_TO_BOX_LIST.put("sleeping pink squirrel",new ArrayList<String> (Arrays.asList("tiffany box", "dog food bag", "lucky bag", "envelope")));
        ANIMAL_TO_BOX_LIST.put("pink squirrel",new ArrayList<String> (Arrays.asList("tiffany box", "dog food bag", "lucky bag", "envelope")));
        ANIMAL_TO_BOX_LIST.put("sleeping squirrel",new ArrayList<String> (Arrays.asList("tiffany box", "dog food bag", "lucky bag", "envelope")));
        ANIMAL_TO_BOX_LIST.put("squirrel",new ArrayList<String> (Arrays.asList("tiffany box", "dog food bag", "lucky bag", "envelope")));
        ANIMAL_TO_BOX_LIST.put("unicorn with grass",new ArrayList<String> (Arrays.asList("tiffany box", "lucky bag","watermelon bag","blue bag", "green bag", "pink bag", "red bag", "black mole bag", "blue mole bag", "green mole bag", "purple mole bag")));
        ANIMAL_TO_BOX_LIST.put("sleeping unicorn",new ArrayList<String> (Arrays.asList("tiffany box", "lucky bag","watermelon bag","blue bag", "green bag", "pink bag", "red bag", "black mole bag", "blue mole bag", "green mole bag", "purple mole bag")));
        ANIMAL_TO_BOX_LIST.put("giggling unicorn",new ArrayList<String> (Arrays.asList("tiffany box", "lucky bag","watermelon bag","blue bag", "green bag", "pink bag", "red bag", "black mole bag", "blue mole bag", "green mole bag", "purple mole bag")));
        ANIMAL_TO_BOX_LIST.put("unicorn",new ArrayList<String> (Arrays.asList("tiffany box", "lucky bag","watermelon bag","blue bag", "green bag", "pink bag", "red bag", "black mole bag", "blue mole bag", "green mole bag", "purple mole bag")));
        ANIMAL_TO_BOX_LIST.put("Corgi back",new ArrayList<String> (Arrays.asList("watermelon bag", "blue box", "green box", "purple box", "red box", "yellow box", "dog food bag", "lucky bag")));
        ANIMAL_TO_BOX_LIST.put("Corgi",new ArrayList<String> (Arrays.asList("watermelon bag", "blue box", "green box", "purple box", "red box", "yellow box", "dog food bag", "lucky bag")));
        ANIMAL_TO_BOX_LIST.put("sleeping Corgi",new ArrayList<String> (Arrays.asList("watermelon bag", "blue box", "green box", "purple box", "red box", "yellow box", "dog food bag", "lucky bag")));
        ANIMAL_TO_BOX_LIST.put("Corgi side",new ArrayList<String> (Arrays.asList("watermelon bag", "blue box", "green box", "purple box", "red box", "yellow box", "dog food bag", "lucky bag")));

    }

}
