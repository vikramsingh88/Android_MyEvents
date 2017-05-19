package vikram.mindtree.com.myevents;

public class ScoreBoard {

    private String teamName;
    private String eventName;
    private String points;
    private String date;

    public ScoreBoard(String teamName, String eventName, String points, String date) {
        this.teamName = teamName;
        this.eventName = eventName;
        this.points = points;
        this.date = date;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
