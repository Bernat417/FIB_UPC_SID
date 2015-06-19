/*
 * To do: 
 * updateEvents -- Rellenar la priority queue de events
 * checkEvents -- Buscar un agent de devices (nomes hi haura un) i enviar-li
 * un mesage de contingut "showd:name:content", on content es la descripció
 * de l'acció i name el nom del agent (this)
 */
package sid;

//Cambia el path del modelo
//Indica en el startGUI.sh donde se encuentra el jar de nuestro proyecto.

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
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
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.JenaException;
import java.io.FileOutputStream;
import java.util.Iterator;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.util.PriorityQueue;

public class PatientAgent extends Agent {
     
    public static final long threshold = 43200;
            
    Scanner keyboard = new Scanner(System.in);
    String NS = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#";
    OntModel model1;
    PriorityQueue<Event> events;
            
    long endTime, currentTime;
    
    public class WaitInstructions extends CyclicBehaviour
    {
        public void checkEvents(String minutes)
        {
            currentTime = Long.parseLong(minutes);
            
            if(currentTime > (endTime - threshold)) updateEvents((currentTime + 53280)+"");
            
            Event current = events.peek();
            while(current != null && current.time <= currentTime)
            {
                //send message to devices agent
            }
            
            
        }
        
        public void updateEvents(String minutes)
        {
            //Rellena eventos hasta el tiempo de la entrada (endTime)
            events = new PriorityQueue<Event>();
            endTime = Long.parseLong(minutes);
            
            Event e = new Event("Take the drugs", 0);
            //<--------------- CARLOS GUAPO DALE AQUI
            //construir Entrades i afegirles a 
            events.add(e);
        }
        
        
        public void action()
        {
            ACLMessage msg = myAgent.receive();
            if(msg != null)
            {
                String s = msg.getContent();
                String command = s.substring(0, Math.min(s.length(), 6));
                String content = s.substring(Math.min(s.length(), 6),s.length());
                if(command.equals("ntime:") ) checkEvents(content);
                if(command.equals("updat:") ) updateEvents(content);
                else System.out.println("Can't process the message");
            }
            else block();
        }

    }
    protected void setup() {
        //Load Model
        model1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        try {  
            model1.read("file:/home/bernat/Repo/SID/projectRDF.owl", "RDF/XML");
        }
        catch (JenaException je) {        
           System.out.println("ERROR");
           je.printStackTrace();
           System.exit(0);
        }  
    
        //Add Behaviours
        WaitInstructions b = new WaitInstructions();
        this.addBehaviour(b);
        
        currentTime = endTime = 0;
        
        
        System.out.println("Patient Agent Ready");
    }     
}
                
                
       
