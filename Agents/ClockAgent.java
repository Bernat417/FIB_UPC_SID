package sid;

import java.util.Scanner;
import jade.core.behaviours.CyclicBehaviour;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.JenaException;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.*;

public class ClockAgent extends Agent {
     
    Scanner keyboard = new Scanner(System.in);
    String NS = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#";
    OntModel model;
    
    ClockAgent me;
    long time;
    boolean freeze;
    double speed;
    
    public class TicTac extends CyclicBehaviour
    {        
        public void incrementTime()
        {
            time += 1;
            //System.out.println("New time is "+time);
            AMSAgentDescription [] agents = null;

            try 
            {
                SearchConstraints c = new SearchConstraints();
                c.setMaxResults ( new Long(-1) );
                agents = AMSService.search(me, new AMSAgentDescription (), c );
            }
            catch (Exception e) {
                System.out.println("ERROR");
            }

            for (int i=0; i<agents.length;i++){
                AID agentID = agents[i].getName();
                if (agentID.getLocalName().startsWith("pacie")) 
                {
                    ACLMessage msg2 = new ACLMessage( ACLMessage.INFORM );
                    msg2.setContent("ctime:"+time);
                    msg2.addReceiver(agentID);
                    send(msg2);
                }
            }
            
            try {
                double n = 1000.0;
                n /= speed;
                Thread.sleep((int)n);
            } 
            catch (InterruptedException e) {
                System.out.println("ERROR");
            }
        }
        
        public void action()
        {
            ACLMessage msg = myAgent.receive();
            
            if(msg != null)
            {
                String s = msg.getContent();
                String command = s.substring(0, Math.min(s.length(), 6));
                String content = s.substring(Math.min(s.length(), 6),s.length());
                
                switch (command) {
                    case "stime:":  time = Long.parseLong(content);
                        break;
   
                    case "tfrez:":  freeze = !freeze;
                        break;
                
                    case "speed:":  speed = Double.parseDouble(content);
                        break;
                
                    default: System.out.println("Can't process the message");
                        break;
                }
                
            }
            else if (!freeze) incrementTime();
        
        }

    }
    protected void setup() {
        me = this;
        freeze = true;
        speed = 1.0;
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        
        try {  
            model.read("file:/home2/users/alumnes/1161756/SID/projectRDF.owl", "RDF/XML");
        }
        catch (JenaException je) {        
           System.out.println("ERROR");
           je.printStackTrace();
           System.exit(0);
        }  
    
        TicTac b = new TicTac();
        this.addBehaviour(b);
        
         System.out.println("Clock Agent Ready");
    }     
}
                
                
       
