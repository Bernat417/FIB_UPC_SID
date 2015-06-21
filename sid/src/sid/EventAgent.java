/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sid;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.JenaException;
import jade.core.Agent;
import java.io.FileOutputStream;
import java.util.Iterator;

/**
 *
 * @author carlos
 */
public class EventAgent extends Agent {
    
    public void getIndividualsByClass(String c, OntModel model) {
        Iterator<OntClass> classesIt = model.listNamedClasses();
        while ( classesIt.hasNext() )
        {
            OntClass actual = classesIt.next();
            OntClass Class = model.getOntClass(actual.getURI() );
            System.out.println(Class.getLocalName());
            if (Class.getLocalName().equals(c)) {
                for (Iterator i = model.listIndividuals(Class); i.hasNext(); )
                {
                    System.out.println("    · " + i.next() );
                }
            }    
        }

    }
    
    public void 
    
    
    
    protected void setup() {
               
        OntModel model1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        
        try {
            model1.read("file:/home/carlos/Documentos/sid/proyecto/projectRDF.owl", "RDF/XML");
        }
        catch (JenaException je) {       
           System.out.println("ERROR");
           je.printStackTrace();
           System.exit(0);
        }
                
        if (!model1.isClosed())
        {
            try {
                model1.write(new FileOutputStream("/home/carlos/Documentos/sid/proyecto/projectRDF.owl", false));
                model1.close();
            } catch (Exception e) {
            }
            
        }     
        
        
    }
    
}
