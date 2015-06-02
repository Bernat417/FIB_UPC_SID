/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sid;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntClass;
//import com.hp.hpl.jena.ontology.Property;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.shared.JenaException;
import jade.core.Agent;
import java.io.FileOutputStream;

/**
 *
 * @author carlos
 */
public class SidAgent extends Agent {
   
    
    protected void setup() {
       
        
        OntModel model1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        model1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF);
        
        try {
            //model1.read("file:/Users/alex/Documents/workspace/FIB_UPC_SID/project.owl", "RDF/XML");
            model1.read("file:/Users/alex/Documents/workspace/FIB_UPC_SID/project.owl", "OWL-API");
        }
        catch (JenaException je) {        

           System.out.println("ERROR");

           je.printStackTrace();

           System.exit(0);

        }
        
        /*String NS = "http://www.owl-ontologies.com/OntologyBase.owl#";
        OntClass att = model1.getOntClass(NS + "Paciente");
        Individual I1 = model1.createIndividual(NS + "Alex", att);*/
        
        
        String NS = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#";
        OntClass att = model1.getOntClass(NS + "Paciente");
        Individual I1 = model1.createIndividual(NS + "Alex", att);
        OntClass att2 = model1.getOntClass(NS + "Nombre_persona");
        Individual I2 = model1.createIndividual(NS + "Alex", att);

        Property prop = model1.createProperty(NS +"Nombre_persona");

        model1.add(I1, prop, "Alex");
        

        
    
    
        //Create a new query
        /* String queryString = 
        "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
        "SELECT ?nombre ?apellidos ?dolencia_nombre\n" +
        "WHERE { ?paciente a :Paciente." +
        "?paciente :Nombre_persona ?nombre." +
        "?paciente :Apellidos ?apellidos." +
        "?paciente :Padece ?dolencia." +
        "?dolencia :Nombre_dolencia ?dolencia_nombre.}\n"+
        "";*/
        
        //Create a new query
         String queryString = 
        "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
        "SELECT *\n" +
        "WHERE { ?paciente a :Paciente." +
        "}\n"+
        "";
        
         Query query = QueryFactory.create(queryString);
        
         // Execute the query and obtain results
         QueryExecution qe = QueryExecutionFactory.create(query, model1);
         ResultSet results =  qe.execSelect();
         
          // Output query results    
         ResultSetFormatter.out(System.out, results, query);

         qe.close(); 
        
        System.out.println("Agente de SID");

                
                
        if (!model1.isClosed())
        {
            try {
                model1.write(new FileOutputStream("/Users/alex/Documents/workspace/FIB_UPC_SID/project.owl", true));
                model1.close();
            } catch (Exception e) {
            }
            
        }     
    }
    
}
