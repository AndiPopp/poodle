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
	
	private static String[] prenames = {"adept", "affectionate", "agreeable", "alluring", "amazing", "amiable", "ample", "awesome", "blithesome", "bountiful", "brave", "breathtaking ", "bright", "brilliant", "capable", "charming", "considerate", "courageous", "creative", "dazzling", "determined", "diligent", "diplomatic", "dynamic", "elegant", "enchanting", "energetic", "excellent", "fabulous", "faithful", "favorable", "fearless", "fortuitous", "frank", "friendly", "funny", "generous", "giving", "gleaming", "glimmering", "glistening", "glittering", "glowing", "gorgeous", "gregarious", "hardworking", "helpful", "humorous", "imaginative", "incredible", "kind", "knowledgeable", "likable", "lovely", "loving", "loyal", "lustrous", "magnificent", "marvelous", "mirthful", "nice", "optimistic", "outstanding", "passionate", "patient", "perfect", "persistent", "philosophical", "plucky", "polite", "proficient", "propitious", "ravishing", "remarkable", "romantic", "rousing", "self-confident", "sensible", "shimmering", "shining", "sincere", "sleek", "sparkling", "spectacular", "splendid", "stellar", "stunning", "stupendous", "super", "thoughtful", "twinkling", "unique", "upbeat", "vibrant", "vivacious", "vivid", "warmhearted", "willing", "wondrous", "zestful"};
			
	private static String[] surnames = {"aardvark", "alligator", "alpaca", "aoudad", "ape", "armadillo", "badger", "bald eagle", "basilisk", "bat", "beaver", "bighorn", "blue crab", "boar", "budgerigar", "buffalo", "camel", "canary", "capybara", "chameleon", "cheetah", "chicken", "civet", "coati", "cougar", "dingo", "dormouse", "duckbill platypus", "eland", "ermine", "fawn", "fish", "fox", "gazelle", "hamster", "hare", "hippopotamus", "hog", "horse", "ibex", "iguana", "jaguar", "kangaroo", "kitten", "lamb", "lemur", "lion", "lizard", "lovebird", "lynx", "mare", "marmoset", "mole", "mongoose", "monkey", "mouse", "mustang", "mynah bird", "ocelot", "okapi", "oryx", "otter", "panda", "parakeet", "parrot", "peccary", "polar bear", "pony", "pronghorn", "quagga", "rabbit", "ram", "reindeer", "rooster", "salamander", "sheep", "silver fox", "sloth", "snake", "snowy owl", "tiger", "turtle", "vicuna", "waterbuck", "wildcat", "wolf", "wombat", "woodchuck", "yak"};

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
