package sid;

import java.util.Scanner;
import jade.core.behaviours.CyclicBehaviour;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;
import java.util.Scanner;
import java.util.Stack;
import jade.core.behaviours.CyclicBehaviour;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.JenaException;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;


public class InterfaceAgent extends Agent {
     
    Scanner keyboard = new Scanner(System.in);
    String NS = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#";
    OntModel model;
    InterfaceAgent me;
            
    //Comunication Variables
    boolean linked;
    AID  clockAgent;
    public class WaitInstructions extends CyclicBehaviour
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
                System.out.println("ERRORAgents");
            }

            //Filter the targets
           
            
            Stack<AID> pool = new Stack<AID>();

            for (i=0; i<allAgents.length;i++)
            {
                AID agentID = allAgents[i].getName();
                if (agentID.getLocalName().startsWith("clock")) 
                {
                    clockAgent = agentID;
                    i=allAgents.length;
                }
            } 
            
            linked = true;
            
        }
        
        
        public void print(String name)
        {
            String queryString = 
                "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
                "SELECT * \n" +
                "WHERE { ?lista"+ name +" a :" + name + "." +
                "}\n"+
                "";

            Query query = QueryFactory.create(queryString);
            QueryExecution qe = QueryExecutionFactory.create(query, model);
            ResultSet results =  qe.execSelect();
            ResultSetFormatter.out(System.out, results, query);
            qe.close();
            
        }
        
        
        public void resetLinks()
        {
            if(!linked)connectAgents();
            ACLMessage msg = new ACLMessage( ACLMessage.INFORM );
            msg.setContent("rlink:");
            msg.addReceiver(clockAgent);
            send(msg);
        }
        
        public void newPatient(String name) 
        {
            PlatformController apc;
            try {
                apc = me.getContainerController().getPlatformController();
                AgentController ac;
                ac = apc.createNewAgent("patient-" + name, "sid.PatientAgent",null);
                ac.start();
            } catch (Exception e) {
                System.out.println("Error");
            } 
            resetLinks();
        }
        
        public void newDoctor(String name) 
        {
            PlatformController apc;
            try {
                apc = me.getContainerController().getPlatformController();
                AgentController ac;
                ac = apc.createNewAgent("doctor-" + name, "sid.DoctorAgent",null);
                ac.start();
            } catch (Exception e) {
                System.out.println("Error");
            } 
        }
        
        public void start()
        {
            if(!linked)connectAgents();
            resetLinks();
            ACLMessage msg = new ACLMessage( ACLMessage.INFORM );
            msg.setContent("tfrez:");
            msg.addReceiver(clockAgent);
            send(msg);
        }
        
        public void action()
        {
            ACLMessage msg = myAgent.receive();
            if(msg != null)
            {
                String s = msg.getContent();
                String command = s.substring(0, Math.min(s.length(), 6));
                String content = s.substring(Math.min(s.length(), 6),s.length());
                
                if (command.equals("print:")) 
                    print(content);
                
                else if (command.equals("initA:")) 
                    baseAgents();
                
                else if(command.equals("start:"))
                    start();
                
                else if(command.equals("npati:"))
                    newPatient(content);
                
                else if(command.equals("ndoct:"))
                    newDoctor(content);
                
                else System.out.println("Intergace: Can't process the message: " + s);  
         
            }
            else block();
            
        }

    }
    
    public void baseAgents() 
    {
        PlatformController apc;
        try {
            apc = me.getContainerController().getPlatformController();
            AgentController ac;
            ac = apc.createNewAgent("clockAgent", "sid.ClockAgent",null);
            ac.start();
            ac = apc.createNewAgent("deviceAgent", "sid.DevicesAgent",null);
            ac.start();
            //ac = apc.createNewAgent("eventAgent", "sid.EventAgent",null);
            //ac.start();
        } catch (Exception e) {
            System.out.println("Error");
        } 
                      
    }
    
    protected void setup() {
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        linked = false;
        
        me = this;
                
        try {  
            model.read("file:/home/bernat/Repo/SID/projectRDF.owl", "RDF/XML");
        }
        catch (JenaException je) {        
           System.out.println("ERROR");
           je.printStackTrace();
           System.exit(0);
        }  
        
        baseAgents();
                
        WaitInstructions b = new WaitInstructions();
        this.addBehaviour(b);
        
        System.out.println("Interface Agent Ready");
        
    }   
    
}
                
                
       
