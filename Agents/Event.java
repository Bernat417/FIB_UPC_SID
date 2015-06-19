package sid;
public class Event {

    String text;
    long time;

    public Event(String text, long time) {
        this.time = time;
        this.text = text;
    }

 
    public int compare(Event patient1, Event patient2) {
        return (Long.valueOf(patient1.time).compareTo(patient2.time));                                                          
    }
} 

