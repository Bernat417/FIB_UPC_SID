package sid;

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

public class DevicesAgent extends Agent {
    Scanner keyboard = new Scanner(System.in);
    String NS = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#";
    OntModel model;
            
    public class WaitInstructions extends CyclicBehaviour
    {
        public void output(String idPacient, String contingut)
        {
            String aparell = "";
            String name  = "";
            //<-----------------------------------------
            //Aqui va la consulta sparql, que retorna el aparell
            
            
            System.out.println("Avis a pacient " + name + " mitjançant " + aparell
            + " amb contingut " + contingut);
        }
        
        public void action()
        {
            ACLMessage msg = myAgent.receive();
            if(msg != null)
            {
                String s = msg.getContent();
                String command = s.substring(0, Math.min(s.length(), 6));
                String content = s.substring(Math.min(s.length(), 6),s.length());
                
                //El missatge s'hauria de parsejar(showd:name:action_description)
                switch (command) {
                    case "showd:":  output("name",content);
                        break;
            
                    default: System.out.println("Can't process the message");
                        break;
                }
                
            }
            else block();
        }

    }
    protected void setup() {
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        
        try {  
            model.read("file:/home/bernat/Repo/SID/projectRDF.owl", "RDF/XML");
        }
        catch (JenaException je) {        
           System.out.println("ERROR");
           je.printStackTrace();
           System.exit(0);
        }  
    
        WaitInstructions b = new WaitInstructions();
        this.addBehaviour(b);
        
        System.out.println("Interface Agent Ready");
    
    }     

}
                
                
       
