package dgwerlod.movieanalysis;

import dgwerlod.movielensinterface.FileIO;

import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class NetflixTester {
	
	public static final String baseFile = "ml-custom-test" + FileIO.FILE_SEPARATOR + "1Ratings.csv";
	public static final String testFile = "ml-custom-test" + FileIO.FILE_SEPARATOR + "1Tests.csv";

	public static final String moviesFile = "ml-latest-small" + FileIO.FILE_SEPARATOR + "movies.csv";
	public static final String linksFile = "ml-latest-small" + FileIO.FILE_SEPARATOR + "links.csv";
	public static final String tagsFile = "ml-latest-small" + FileIO.FILE_SEPARATOR + "tags.csv";

	public static void main(String[] args) throws IOException {
		
		System.out.println("\n***Starting the clock***");
		long startTime = System.currentTimeMillis();
		
		System.out.println("\n***Initializing Predictor***");
		
		NetflixPredictor tester = new NetflixPredictor(moviesFile, baseFile, tagsFile, linksFile);
		
		System.out.println("\n***Testing getRating Method***");
		
		{
			int numberTested, userToTest, movieToTest;
			ArrayList<String> baseDataLines = FileIO.readFile(baseFile);
			baseDataLines.remove(0);

			ArrayList<Integer> users = new ArrayList<>();
			ArrayList<ArrayList<Integer>> movies = new ArrayList<>();
			
			for (String line : baseDataLines) {
				String[] lineVals = line.split(",");
				userToTest = Integer.parseInt(lineVals[0]);
				movieToTest = Integer.parseInt(lineVals[1]);
				boolean found = false;
				for (int i = 0; i < users.size(); i++)
					if (users.get(i) == userToTest) {
						movies.get(i).add(movieToTest);
						found = true;
						break;
					}
				if (!found) {
					users.add(userToTest);
					ArrayList<Integer> newList = new ArrayList<>();
					newList.add(movieToTest);
					movies.add(newList);
				}
			}
			numberTested = 0;
			while(numberTested < 20) {
				int testIndex;
				do {
					testIndex = (int)(Math.random()*2000);
				} while (users.indexOf(testIndex) >= 0);
				double val = tester.getRating(testIndex, 1);
				if (val >= 0) {
					System.out.println("Rating returned from getRating for a non-existing user! ");
					System.out.println("(This probably means you're reading in the database incorrectly)");
					System.exit(0);
				}
				numberTested++;
			}
			numberTested = 0;
			while(numberTested < 20) {
				int testIndex = (int)(Math.random()*users.size()), testIndex2;
				do {
					testIndex2 = (int)(Math.random()*200);
				} while (movies.get(testIndex).indexOf(testIndex2) >= 0);
				
				double val = tester.getRating(users.get(testIndex), testIndex2);
				if (val >= 0) {
					System.out.println("Rating returned from getRating for a movie that was not rated by user!");
					System.out.println("(This probably means you're reading in the database incorrectly)");
					System.exit(0);
				}
				numberTested++;
			}
			numberTested = 0;
			while(numberTested < 20) {
				int testIndex = (int)(Math.random()*users.size());
				int testIndex2 = (int)(Math.random()*movies.get(testIndex).size());
				
				int val1 = users.get(testIndex);
				int val2 = movies.get(testIndex).get(testIndex2);
				
				double val = tester.getRating(val1, val2);
				if (val < 0) {
					System.out.println("No rating returned from getRating for a movie that was rated by user!");
					System.out.println("(This probably means you're reading in the database incorrectly)");
					System.exit(0);
				}
				numberTested++;
			}
		}
		
		
		System.out.println("\n***Generating Recommendations***");
		
		ArrayList<String> testDataLines = FileIO.readFile(testFile);
		
		double guessStars, actualStars, totalDifference = 0, totalSquareDifference = 0;
		int numberTested = 0, numberPresent = 0, userToTest, movieToTest;
		
		for (String line : testDataLines) {
			String[] lineVals = line.split(",");
			userToTest = Integer.parseInt(lineVals[0]);
			movieToTest = Integer.parseInt(lineVals[1]);
			actualStars = Double.parseDouble(lineVals[2]);
			guessStars = tester.getRating(userToTest, movieToTest);
			if (guessStars >= 0) {
				numberPresent++;
				continue;
			}
			guessStars = tester.guessRating(userToTest, movieToTest);
			totalDifference += Math.abs(guessStars-actualStars);
			totalSquareDifference += (guessStars-actualStars) * (guessStars-actualStars);
			numberTested++;
			System.out.println("Tested " + (numberTested+numberPresent) + "/" + testDataLines.size());
		}
		
		System.out.println("\n***Stopping the clock***");
		long endTime = System.currentTimeMillis();
		long time = (endTime-startTime);
		long mins = time/60000;
		long secs = time%60000/1000;
		long millis = time % 1000;
		
		System.out.println("\n\n******Results******");
		System.out.println("Runtime: " + mins + ":" + secs + ":" + millis);
		System.out.println("Test combinations already present in dataset: " + numberPresent);
		System.out.println("Test combinations not present (rating guessed): " + numberTested);
		System.out.println("Total difference between guessed and actual ratings: " + totalDifference);
		System.out.println("Average difference between guessed and actual ratings: " + totalDifference/numberTested);
		System.out.println("Root square mean difference between guessed and actual ratings: " + Math.sqrt(totalSquareDifference/numberTested));

	}

}
