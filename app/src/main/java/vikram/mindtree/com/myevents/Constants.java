package vikram.mindtree.com.myevents;


public class Constants {

    private static final String IP ="192.168.43.153";
    private static final String SCHEMA = "http";
    private static final String PORT = "8080";
    public static final String URI = "https://mindtree-aol-fun.herokuapp.com"; //SCHEMA+"://"+IP+":"+PORT; //

    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";
    public static final String PUT_METHOD = "PUT";
    public static final String DELETE_METHOD = "DELETE";

    public static final String TEAMS = URI+"/team";
    public static String MEMBER = TEAMS+"/teamName/member"; // /team/:teamName/member
    public static final String Event = URI+"/event";
    public static final String DEVICE = URI+"/device";
    public static final String NOTIFY = URI+"/notify";
    public static final String COMING_SOON = URI+"/comingsoon";
    public static final String SCORE_BOARD = URI+"/scoreboard";
    public static final String LOGIN = URI+"/login";
    public static final String INFORM_EVENT= URI+"/inform";
    public static final String GET_TEASER = URI+"/teaser";

}
