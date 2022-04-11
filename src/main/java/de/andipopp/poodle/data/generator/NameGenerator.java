package de.andipopp.poodle.data.generator;

import java.util.Random;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.entity.polls.Answer;
import de.andipopp.poodle.data.entity.polls.AnswerType;
import de.andipopp.poodle.data.entity.polls.Vote;
import de.andipopp.poodle.util.UUIDUtils;

public class NameGenerator {

	private static Random rng = new Random();
	
	private static final String NUMBER_LABEL = "#";
	
	private static String[] prenames = {"Adept", "Affectionate", "Agreeable", "Alluring", "Amazing", "Amiable", "Ample", "Awesome", "Blithesome", "Bountiful", "Brave", "Breathtaking ", "Bright", "Brilliant", "Capable", "Charming", "Considerate", "Courageous", "Creative", "Dazzling", "Determined", "Diligent", "Diplomatic", "Dynamic", "Elegant", "Enchanting", "Energetic", "Excellent", "Fabulous", "Faithful", "Favorable", "Fearless", "Fortuitous", "Frank", "Friendly", "Funny", "Generous", "Giving", "Gleaming", "Glimmering", "Glistening", "Glittering", "Glowing", "Gorgeous", "Gregarious", "Hardworking", "Helpful", "Humorous", "Imaginative", "Incredible", "Kind", "Knowledgeable", "Likable", "Lovely", "Loving", "Loyal", "Lustrous", "Magnificent", "Marvelous", "Mirthful", "Nice", "Optimistic", "Outstanding", "Passionate", "Patient", "Perfect", "Persistent", "Philosophical", "Plucky", "Polite", "Proficient", "Propitious", "Ravishing", "Remarkable", "Romantic", "Rousing", "Self-Confident", "Sensible", "Shimmering", "Shining", "Sincere", "Sleek", "Sparkling", "Spectacular", "Splendid", "Stellar", "Stunning", "Stupendous", "Super", "Thoughtful", "Twinkling", "Unique", "Upbeat", "Vibrant", "Vivacious", "Vivid", "Warmhearted", "Willing", "Wondrous", "Zestful"};
	
	private static String[] surnames = {"Aardvark", "Alligator", "Alpaca", "Aoudad", "Ape", "Armadillo", "Badger", "Bald Eagle", "Basilisk", "Bat", "Beaver", "Bighorn", "Blue Crab", "Boar", "Budgerigar", "Buffalo", "Camel", "Canary", "Capybara", "Chameleon", "Cheetah", "Chicken", "Civet", "Coati", "Cougar", "Dingo", "Dormouse", "Duckbill Platypus", "Eland", "Ermine", "Fawn", "Fish", "Fox", "Gazelle", "Hamster", "Hare", "Hippopotamus", "Hog", "Horse", "Ibex", "Iguana", "Jaguar", "Kangaroo", "Kitten", "Lamb", "Lemur", "Lion", "Lizard", "Lovebird", "Lynx", "Mare", "Marmoset", "Mole", "Mongoose", "Monkey", "Mouse", "Mustang", "Mynah Bird", "Ocelot", "Okapi", "Oryx", "Otter", "Panda", "Parakeet", "Parrot", "Peccary", "Polar Bear", "Pony", "Pronghorn", "Quagga", "Rabbit", "Ram", "Reindeer", "Rooster", "Salamander", "Sheep", "Silver Fox", "Sloth", "Snake", "Snowy Owl", "Tiger", "Turtle", "Vicuna", "Waterbuck", "Wildcat", "Wolf", "Wombat", "Woodchuck", "Yak"};

	public static String randomName() {
		int pre = rng.nextInt(prenames.length);
		int sur = rng.nextInt(surnames.length);
		return prenames[pre] + " " + surnames[sur];
	}
	
	public static String randomNumberLabel(int byteSize) {
		byte[] bytes = new byte[byteSize];
		rng.nextBytes(bytes);
		return NUMBER_LABEL + UUIDUtils.enc.encodeToString(bytes);
	}
	
	public static Vote<?,?> addRandomVote(AbstractPoll<?,?> poll) {
		Vote<?,?> vote = poll.addEmptyVote();
		for(Answer<?,?> answer : vote.getAnswers()) {
			int i = rng.nextInt(AnswerType.values().length);
			answer.setValue(AnswerType.values()[i]);
		}
		vote.setDisplayName(randomName());
		return vote;
	}
	
	public static void addRandomVotes(AbstractPoll<?,?> poll, int count) {
		for(int i = 0; i < count; i++) {
			addRandomVote(poll);
		}
	}
	
	
	public static void main(String[] args) {
		for(int i = 0; i < 100; i++) {
			System.out.println(randomName());
		}
	}
	
}
