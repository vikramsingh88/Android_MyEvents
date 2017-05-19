package vikram.mindtree.com.myevents;


public class TeamDetails {
    private String teamName;
    private String teamLogo;
    private String teamBanner;
    private String teamColor;

    public String getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(String teamColor) {
        this.teamColor = teamColor;
    }

    private String [] teamMember;

    public TeamDetails() {
    }

    public TeamDetails(String teamName, String teamLogo, String teamBanner,String teamColor, String[] teamMember) {
        this.teamName = teamName;
        this.teamLogo = teamLogo;
        this.teamBanner = teamBanner;
        this.teamColor = teamColor;
        this.teamMember = teamMember;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamLogo() {
        return teamLogo;
    }

    public void setTeamLogo(String teamLogo) {
        this.teamLogo = teamLogo;
    }

    public String getTeamBanner() {
        return teamBanner;
    }

    public void setTeamBanner(String teamBanner) {
        this.teamBanner = teamBanner;
    }

    public String[] getTeamMember() {
        return teamMember;
    }

    public void setTeamMember(String[] teamMember) {
        this.teamMember = teamMember;
    }
}
