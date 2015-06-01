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
import jade.core.Agent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author carlos
 */
public class SidAgent extends Agent {
   
    
    protected void setup() {
        String source = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7";
        String ns = source + "#";
        File file = new File ("/home/carlos/Documentos/sid/projecto/project.owl");
        FileInputStream fis = null;
        
        try {
            fis = new FileInputStream(file);
        } 
        catch (IOException e) {
			e.printStackTrace();
        }
        OntModel model1 = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM);
        
        // read the RDF/XML file
        model1.read( fis, "RDF/XML" );
        
        //Create a new query
        String queryString = 
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
        "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
        "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
        "SELECT ?nombre ?apellidos ?dolencia_nombre" +
        "WHERE { ?paciente a :Paciente." +
        "?paciente :Nombre_persona ?nombre." +
        "?paciente :Apellidos ?apellidos." +
        "?paciente :Padece ?dolencia." +
        "?dolencia :Nombre_dolencia ?dolencia_nombre.";
        
         Query query = QueryFactory.create(queryString);
        
         // Execute the query and obtain results
         QueryExecution qe = QueryExecutionFactory.create(query, model1);
         ResultSet results =  qe.execSelect();
         
          // Output query results    
         ResultSetFormatter.out(System.out, results, query);

         qe.close();

                
                
                
    }
    
}
