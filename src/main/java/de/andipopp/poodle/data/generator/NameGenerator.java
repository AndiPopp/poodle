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
	
	private static String[] prenames = {"'Avdi'el", "Amita", "Artur", "Azat", "Daniel", "Elenora", "Esther", "Fritiof", "Gallus", "Gordon", "Gordon", "Grahame", "Jacob", "Jacob", "Kevan", "Koa", "Konrad", "Leland", "Manius", "Manius", "Octavia", "Patty", "Plácido", "Puja", "Ragnar", "Robby", "Safi", "Sandip", "Sandip", "Sarvesh", "Sultana", "Sümeyye", "Sümeyye", "Summer", "Tod", "Udo", "Usha", "Vitalis", "Waldobert", "Wallace"};

	private static String[] surnames = {"Antonescu", "Antonescu", "Bohn", "Bourke", "Bover", "Bover", "Di Napoli", "Eliasson", "Franklin", "Franklin", "Garrod", "Garrod", "Harland", "Ingham", "Jackson", "Jordà", "Josephs", "Khimim", "Kopecký", "Krückel", "Lawrence", "Lázár", "Lécuyer", "Leoni", "Linville", "Marshall", "Matos", "Nardi", "Oppenheimer", "Palumbo", "Palumbo", "Schäfer", "Solberg", "Stark", "Tamm", "Van Rossem", "Weaver", "Wright", "Yang", "Yuen"};
	
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
