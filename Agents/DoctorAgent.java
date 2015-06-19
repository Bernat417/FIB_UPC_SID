//Cambia el path del modelo
//Indica en el startGUI.sh donde se encuentra el jar de nuestro proyecto.

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import java.util.Scanner;


import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.JenaException;
import jade.core.Agent;
import java.io.FileOutputStream;
import java.util.Iterator;

/**
 *
 * @author carlos
 */
public class DoctorAgent extends Agent {
     
    Scanner keyboard = new Scanner(System.in);
    String NS = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#";
    
    private void creaAccion(OntModel model1) {    
        boolean correcta = false;
        String nombreAccion = "";
        String descripcionAccion  = "";
        while (!correcta) {
            System.out.println("Introduce el nombre de la acción: ");
            nombreAccion = keyboard.nextLine();       
            System.out.println("Introduce la descripcion de la accion");
            descripcionAccion = keyboard.nextLine();
            
            Individual t = getIndividual(model1,nombreAccion);
            if (t == null)
                correcta = true;
            else 
                System.out.println("Ya exista la accion especificada");
           
        }   
        
        System.out.println("Tratamientos disponibles en el sistema");
        
        String queryString = 
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT * \n" +
            "WHERE { ?tratamiento a :Tratamiento." +
            "}\n"+
            "";

        Query query2 = QueryFactory.create(queryString);
        QueryExecution qe2 = QueryExecutionFactory.create(query2, model1);
        ResultSet results =  qe2.execSelect();
        ResultSetFormatter.out(System.out, results, query2);
        qe2.close();
        Individual T;
        
        correcta = false;
        String nombreTratamiento = "";
        while(!correcta) {
           System.out.println("Introduce el nombre del tratamiento seleccionado");
           nombreTratamiento = keyboard.nextLine();

            Individual t = getIndividual(model1,nombreTratamiento);
            
            if (t != null) {
                correcta = true;
                OntClass att = model1.getOntClass(NS + "Acción");
                Individual I1 = model1.createIndividual(NS + nombreAccion, att);
                Property nombre = model1.createProperty(NS +"Nombre_accion");
                Property descripcion = model1.createProperty(NS +"Descripcion");
                Property pertenece = model1.createProperty(NS +"Pertenece_a_tratamiento");
                model1.add(I1, nombre, nombreAccion);
                model1.add(I1,descripcion,descripcionAccion);
                model1.add(I1,pertenece,t);
            }
            else
                System.out.println("Nombre de tratamiento incorrecto");         
           
        }
       
    }
   
    public Individual getIndividual(OntModel model1, String nombreTratamiento)
    {
   
        Iterator<OntClass> classesIt = model1.listNamedClasses();
        Individual trat;
        while ( classesIt.hasNext() )
        {
            OntClass actual = classesIt.next();
            OntClass Tratamiento = model1.getOntClass(actual.getURI() );
            Property nombreTrat = model1.getProperty(NS + "Nombre_tratamiento");
            for (Iterator i = model1.listIndividuals(Tratamiento); i.hasNext(); )
            {
                trat = (Individual) i.next();
                if (trat.getLocalName().equals(nombreTratamiento))
                    return trat;
            }
        }
        return null;
        
    }
    
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
        
        System.out.println("Selecciona una accion a realizar:");
        System.out.println("1. Crear una accion");
        String opcion = keyboard.nextLine();
        if (opcion.equals("1")) 
           creaAccion(model1);
                
        //Create a new query
             String queryString = 
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT  *\n" +
            "WHERE { ?accion a :Acción." +
            "}\n"+
            "";

             Query query = QueryFactory.create(queryString);

             // Execute the query and obtain results
             QueryExecution qe = QueryExecutionFactory.create(query, model1);
             ResultSet results =  qe.execSelect();
             ResultSetFormatter.out(System.out, results, query);
             
             qe.close();

                
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