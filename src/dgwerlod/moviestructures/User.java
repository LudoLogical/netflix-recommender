package dgwerlod.moviestructures;

import java.util.ArrayList;

public class User implements Comparable<User> {

    private int id;
    private ArrayList<Rating> ratings = new ArrayList<>();
    private ArrayList<Tag> tags = new ArrayList<>();

    public User(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public void addRating(Rating r) {
        ratings.add(r);
    }

    public ArrayList<Rating> getRatings() {
        return ratings;
    }

    public double getAverageRating() {
        double total = 0;
        for (Rating r : ratings) {
            total += r.getRating();
        }
        return total / ratings.size();
    }

    public void addTag(Tag t) {
        tags.add(t);
    }

    public String toString() {

        StringBuilder output = new StringBuilder("User #" + id + " has performed the following ratings: \n");
        for (Rating r : ratings) {
            output.append(r.toString()).append('\n');
        }
        output.append("-- They have also issued the following tags: \n");
        for (Tag t : tags) {
            output.append(t.toString()).append('\n');
        }

        return output.toString();
    }

    public int compareTo(User o) {
        return id - o.id;
    }

}
