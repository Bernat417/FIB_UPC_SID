/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sid;

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

/**
 *
 * @author carlos
 */
public class SidAgent extends Agent {
   
    
    protected void setup() {
       
        
        OntModel model1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        
        try {
            model1.read("file:/home/carlos/Documentos/sid/projecto/project.owl", "RDF/XML");
        }
        catch (JenaException je) {        

           System.out.println("ERROR");

           je.printStackTrace();

           System.exit(0);

        }
        
        
        //Create a new query
         String queryString = 
        "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
        "SELECT ?nombre ?apellidos ?dolencia_nombre" +
        "WHERE { ?paciente a :Paciente." +
        "?paciente :Nombre_persona ?nombre." +
        "?paciente :Apellidos ?apellidos." +
        "?paciente :Padece ?dolencia." +
        "?dolencia :Nombre_dolencia ?dolencia_nombre.}\n"+
        "";
        
         Query query = QueryFactory.create(queryString);
        
         // Execute the query and obtain results
         QueryExecution qe = QueryExecutionFactory.create(query, model1);
         ResultSet results =  qe.execSelect();
         
          // Output query results    
         ResultSetFormatter.out(System.out, results, query);

         qe.close(); 
        
        System.out.println("Agente de SID");

                
                
                
    }
    
}
