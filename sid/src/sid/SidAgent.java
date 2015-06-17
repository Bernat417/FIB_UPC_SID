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
import jade.core.Agent;
import java.io.FileOutputStream;
import java.util.Iterator;

/**
 *
 * @author carlos
 */
public class SidAgent extends Agent {
     
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

           //Create a new query
             String queryString = 
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT * \n" +
            "WHERE { ?accion a :Accion." +
            "?paciente :Nombre_Accion " + nombreAccion +  ".\n" +
            "}\n"+
            "";

             Query query = QueryFactory.create(queryString);

             // Execute the query and obtain results
             QueryExecution qe = QueryExecutionFactory.create(query, model1);
             ResultSet results =  qe.execSelect();

            results.hasNext();
            if (results.hasNext()) 
                 System.out.println("Ya exista la accion");
            else 
               correcta = true;
            qe.close(); 
        }   
        
        System.out.println("Tratamientos disponibles en el sistema");
        
        String queryString = 
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT * \n" +
            "WHERE { ?tratamiento a :Tratamiento." +
            "}\n"+
            "";

        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model1);
        ResultSet results =  qe.execSelect();
        ResultSetFormatter.out(System.out, results, query);
        qe.close();
        
        correcta = false;
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
        
        OntClass att = model1.getOntClass(NS + "Accion");
        Individual I1 = model1.createIndividual(NS + nombreAccion, att);
        Individual T = getTratamiento(model1,nombreTratamiento);
        Property nombre = model1.createProperty(NS +"Nombre_accion");
        Property descripcion = model1.createProperty(NS +"Descripcion");
        Property pertenece = model1.createProperty(NS +"Pertenece_a_tratamiento");
        model1.add(I1, nombre, nombreAccion);
        model1.add(I1,descripcion,descripcionAccion);
        model1.add(I1,pertenece,T);
 
    }
   
    public Individual getTratamiento(OntModel model1, String nombreTratamiento)
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
                RDFNode n = trat.getPropertyValue(nombreTrat); 
                if (n.toString().equals(nombreTratamiento))
                    return trat;
            }
        }
        return null;
        
    }
    
    protected void setup() {
       
        // <--------------- if(not exists)añadir tratamiento a ontologia
        /*if (option.equals("yes")) {
            System.out.println("Introduce el nombre del tratamiento");
            String nombreTratamiento = keyboard.nextLine();
            System.out.println("¿Cuantas acciones lo forman?");
            int numAcciones = keyboard.nextInt();
            for (int i=0; i < numAcciones; ++i) {
                System.out.println("Introduce el nombre de la accion");
                String nombreAccion = keyboard.nextLine();
                
                System.out.println("Introduce la descripcion de la accion");
                String descripcionAccion = keyboard.nextLine();
                
                System.out.println("Introduce la periodicidad de la accion");
                String perioricidadAccion = keyboard.nextLine();
                
                // <--------------- if(not exists) añadir accion (i vincular preioricidad) a ontologia
            }
            
        }*/
        
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
        int opcion = keyboard.nextInt();
        if (opcion == 1)
           creaAccion(model1);
                
                
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
