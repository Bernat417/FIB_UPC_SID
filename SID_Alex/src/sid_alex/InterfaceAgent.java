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
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.JenaException;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class InterfaceAgent extends Agent {
     
    Scanner keyboard = new Scanner(System.in);
    String NS = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#";
    OntModel model;
            
    public class WaitInstructions extends CyclicBehaviour
    {
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
        
        public void action()
        {
            ACLMessage msg = myAgent.receive();
            if(msg != null)
            {
                String s = msg.getContent();
                String command = s.substring(0, Math.min(s.length(), 6));
                String content = s.substring(Math.min(s.length(), 6),s.length());

                switch (command) {
                    case "print:":  print(content);
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
                
                
       
