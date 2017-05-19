package vikram.mindtree.com.myevents;

public class EventDetail {

    private String eventName;
    private String aboutEvent;
    private String eventRules;
    private String eventVenue;
    private String eventPoints;
    private String eventTime;
    private String eventDate;
    private boolean isNotified;

    public EventDetail(String eventName, String aboutEvent, String eventRules, String eventVenue, String eventPoints, String eventTime, String eventDate, boolean isNotified) {
        this.eventName = eventName;
        this.aboutEvent = aboutEvent;
        this.eventRules = eventRules;
        this.eventVenue = eventVenue;
        this.eventPoints = eventPoints;
        this.eventTime = eventTime;
        this.eventDate = eventDate;
        this.isNotified = isNotified;
    }

    public boolean getIsNotified() {
        return isNotified;
    }

    public void setIsNotified(boolean isNotified) {
        this.isNotified = isNotified;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getAboutEvent() {
        return aboutEvent;
    }

    public void setAboutEvent(String aboutEvent) {
        this.aboutEvent = aboutEvent;
    }

    public String getEventRules() {
        return eventRules;
    }

    public void setEventRules(String eventRules) {
        this.eventRules = eventRules;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public String getEventPoints() {
        return eventPoints;
    }

    public void setEventPoints(String eventPoints) {
        this.eventPoints = eventPoints;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }
}
