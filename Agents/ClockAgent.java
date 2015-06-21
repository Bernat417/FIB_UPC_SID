package sid;

import java.util.Scanner;
import java.util.Stack;
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
   
    //Timer Variables
    long time;
    boolean freeze;
    double speed;
    
    //Comunication Variables
    boolean linked;
    ClockAgent me;
    AID [] targetAgents;
    
    public class TicTac extends CyclicBehaviour
    {       
        public void connectAgents()
        {
            int i;
            AMSAgentDescription [] allAgents = null;
         
            //Get all the agents
            try 
            {
                SearchConstraints c = new SearchConstraints();
                c.setMaxResults ( new Long(-1) );
                allAgents = AMSService.search(me, new AMSAgentDescription (), c );
            }
            catch (Exception e) 
            {
                System.out.println("ERROR");
            }

            //Filter the targets
            Stack<AID> pool = new Stack<AID>();

            for (i=0; i<allAgents.length;i++)
            {
                AID agentID = allAgents[i].getName();
                if (agentID.getLocalName().startsWith("pacie") || 
                agentID.getLocalName().startsWith("event")) 
                    pool.push(agentID);
            } 
            
            //Move the targets agents to a faster structure
            targetAgents = new AID[pool.size()]; 
             
            i = 0;
            while(!pool.empty())
            {
                targetAgents[i] = pool.pop();
                ++i;
            }
            
            linked = true;
        }
        
        public void incrementTime()
        {
            time += 1;
            //
            if(!linked) connectAgents();
            
            for (int i=0; i < targetAgents.length; i++)
            {
                ACLMessage msg = new ACLMessage( ACLMessage.INFORM );
                msg.setContent("ctime:"+time);
                msg.addReceiver(targetAgents[i]);
                send(msg);
                System.out.println("New time is "+time + targetAgents[i]);
            } 
            
            try 
            {
                double n = 1000.0;
                n /= speed;
                Thread.sleep((int)n);
            } 
            catch (InterruptedException e) 
            {
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
                
                if(command.equals("stime:"))
                    time = Long.parseLong(content);
                
                else if(command.equals("tfrez:"))
                    freeze = !freeze;
                  
                else if(command.equals("speed:"))
                    speed = Double.parseDouble(content);
                
                else if(command.equals("rlink:"))
                    linked = false;
                else
                    System.out.println("Can't process the message");
                
            }
            else if (!freeze) incrementTime();
        
        }

    }
    protected void setup() {
        me = this;
        freeze = true;
        speed = 1.0;
        linked = false;
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        
        try 
        {  
            model.read("file:/home/bernat/Repo/SID/projectRDF.owl", "RDF/XML");
        }
        catch (JenaException je) 
        {        
           System.out.println("ERROR");
           je.printStackTrace();
           System.exit(0);
        }  
    
        TicTac b = new TicTac();
        this.addBehaviour(b);
        
         System.out.println("Clock Agent Ready");
    }     
}
                
                
       
