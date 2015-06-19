/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sid;

//Cambia el path del modelo
//Indica en el startGUI.sh donde se encuentra el jar de nuestro proyecto.

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import java.util.Scanner;
import java.lang.InterruptedException;
import jade.core.behaviours.CyclicBehaviour;



import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.JenaException;
import jade.core.AID;
import java.io.FileOutputStream;
import java.util.Iterator;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
/**
 *
 * @author carlos
 */
public class ClockAgent extends Agent {
     
    Scanner keyboard = new Scanner(System.in);
    String NS = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#";
    OntModel model1;
    ClockAgent me;
    long time;
    boolean freeze;
    double speed;
    
    public class TicTac extends CyclicBehaviour
    {        
        public void action()
        {
            ACLMessage msg = myAgent.receive();
            if(msg != null)
            {
                String s = msg.getContent();
                String command = s.substring(0, Math.min(s.length(), 6));
                String content = s.substring(Math.min(s.length(), 6),s.length());
                if(command.equals("stime:") ) {
                    time = Long.parseLong(content);
                } else if (command.equals("tfrez:") ) {
                    if (freeze) freeze = false;
                    else freeze = true;
                } else if (command.equals("speed:") ) {
                    speed = Double.parseDouble(content);
                }
                else System.out.println("Can't process the message");
            }
            else if (!freeze) {
                time += 1;
                System.out.println("New time is "+time);
                AMSAgentDescription [] agents = null;

                try {
                    SearchConstraints c = new SearchConstraints();
                    c.setMaxResults ( new Long(-1) );
                    agents = AMSService.search(me, new AMSAgentDescription (), c );
                }
                catch (Exception e) {

                }

                for (int i=0; i<agents.length;i++){
                    AID agentID = agents[i].getName();
                    if (agentID.getLocalName().startsWith("pacie")) {
                        ACLMessage msg2 = new ACLMessage( ACLMessage.INFORM );
                        msg2.setContent("ntime:"+time);
                        msg2.addReceiver(agentID);
                        send(msg2);
                    }
                }
                try {
                    double n = 1000.0;
                    n /= speed;
                    Thread.sleep((int)n);
                } catch (InterruptedException e) {
                    
                }
            }
        }

    }
    protected void setup() {
        me = this;
        freeze = true;
        speed = 1.0;
        
        //Load Model
        model1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        try {  
            model1.read("file:/home2/users/alumnes/1161756/SID/projectRDF.owl", "RDF/XML");
        }
        catch (JenaException je) {        
           System.out.println("ERROR");
           je.printStackTrace();
           System.exit(0);
        }  
    
        //Add Behaviours
        TicTac b = new TicTac();
        this.addBehaviour(b);
        
         System.out.println("Interface Agent Ready");
    }     
}
                
                
       
