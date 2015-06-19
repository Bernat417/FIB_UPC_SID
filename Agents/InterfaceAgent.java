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
/**
 *
 * @author carlos
 */
public class InterfaceAgent extends Agent {
     
    Scanner keyboard = new Scanner(System.in);
    String NS = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#";
    
    public class WaitInstructions extends CyclicBehaviour
    {
        public void action()
        {
            ACLMessage msg = myAgent.receive();
            if(msg != null)
            {
                String s = msg.getContent();
                String command = s.substring(0, Math.min(s.length(), 6));
                String content = s.substring(Math.min(s.length(), 6),s.length());
                if(command.equals("print:") )
                {
                    System.out.println(command);
                    System.out.println(content);
                }
                else System.out.println("yeah not that");
            }
            else block();
        }

    }
    protected void setup() {
        //Load Model
        OntModel model1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        
        try {
            
            model1.read("file:/home/bernat/Repo/SID/projectRDF.owl", "RDF/XML");
        }
        catch (JenaException je) {        

           System.out.println("ERROR");

           je.printStackTrace();

           System.exit(0);

        }  
        System.out.println("Selecciona una accion a realizar:");
        
        //Add Behaviours
        WaitInstructions b = new WaitInstructions();
        this.addBehaviour(b);
    
    }   
    
    
}
                
                
       
