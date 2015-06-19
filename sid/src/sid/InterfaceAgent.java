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
    
    public class HelloWorldCyclicBehaviour extends CyclicBehaviour
    {
        String message;
        int count_chocula;

        public HelloWorldCyclicBehaviour()
        {

        }

        public void onStart()
        {
            
                
            this.message = "Agent " + myAgent +" with HelloWorldCyclicBehaviour in action!!" + count_chocula;
            count_chocula = 0;
        }

        public int onEnd()
        {
            System.out.println("I have done " + count_chocula + " iterations");
            return count_chocula;
        }

        public void print()
        {        
            boolean correcta = false;
            String nombreTratamiento = "";
            while(!correcta) {
            System.out.println("Introduce el nombre del tratamiento seleccionado");
            nombreTratamiento = keyboard.nextLine();
           
            String queryStringTrat = 
                "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
                "SELECT * \n" +
                "WHERE { ?tratamiento a :Tratamiento." +
                "}\n"+
                "";

            Query queryTrat = QueryFactory.create(queryStringTrat);
            QueryExecution qeTrat = QueryExecutionFactory.create(queryTrat, model1);
            ResultSet resultsTrat =  qe.execSelect();
            
            if (resultsTrat.hasNext())
                correcta = true;
            else
                System.out.println("Nombre de tratamiento incorrecto");
            }
            
        }
        public void action()
        {
            ACLMessage msg = myAgent.receive();
            if(msg != null)
            {
                if(msg.getContent()== "print Tratamientos" )
                {
                    
                }
            }
            else
            {
                block();
            }
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
        System.out.println("1. Crear una accion");
        int opcion = keyboard.nextInt();
        if (opcion == 1)
           creaAccion(model1);
                
                
        if (!model1.isClosed())
        {
            try {
                model1.write(new FileOutputStream("file:/home/bernat/Repo/SID/projectRDF.owl", false));
                model1.close();
            } catch (Exception e) {
            }
            
        }     
    }
    
}
