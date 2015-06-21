//Cambia el path del modelo
//Indica en el startGUI.sh donde se encuentra el jar de nuestro proyecto.

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import java.util.Scanner;


import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
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
     
    private String URL_ONTOLOGIA = "/Users/alex/Documents/workspace/FIB_UPC_SID/projectRDF.owl";
    Scanner keyboard = new Scanner(System.in);
    String NS = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#";
    String username;
    String password;
    String dniPersona;
    OntModel model1;
    
        public void login() {
        boolean correct = false;
        while (!correct) {

            System.out.println("Introduce tu nombre de usuario:");
            username = keyboard.nextLine();
            System.out.println("Introduce tu contraseña: ");
            password = keyboard.nextLine();

            String QueryString = 
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT *\n" +
            "WHERE {\n" +        
            "?login a :LogIn.\n" +
            "?login :Username ?user.\n" +
            "?login :Password ?pass.\n" +
            "?login :Identifica ?persona." +         
            "FILTER regex(?user, ?u). \n" +
            "FILTER regex(?pass, ?p). \n" +
            "}\n"+ "";   

            ParameterizedSparqlString str = new ParameterizedSparqlString(QueryString);
            str.setLiteral("u", username.toString());
            str.setLiteral("p",password.toString());

            Query query = QueryFactory.create(str.toString());
            QueryExecution qe2 = QueryExecutionFactory.create(query, model1);
            ResultSet results =  qe2.execSelect();
            if (results.hasNext()) {
                /*QuerySolution row = results.nextSolution();
                dniPersona = row.getLiteral("dni").getString();
                System.out.println(dniPersona);*/
                correct = true;
                System.out.println("Usuario correcto");
            }    
            else
                System.out.println("Usuario o contraseña incorrectos");
            qe2.close();

        }
    }
    
    private void creaAccion() {    
        boolean correcta = false;
        String nombreAccion = "";
        String descripcionAccion  = "";
        while (!correcta) {
            System.out.println("Introduce el nombre de la acción: ");
            nombreAccion = keyboard.nextLine();       
            System.out.println("Introduce la descripcion de la accion");
            descripcionAccion = keyboard.nextLine();
            
            Individual t = getIndividual(nombreAccion);
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

            Individual t = getIndividual(nombreTratamiento);
            
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
    
    private void creaTratamiento() {    
        boolean correcta = false;
//        String nombreAccion = "";
        String nombreTratamiento = "";
//        String descripcionAccion  = "";
        while (!correcta) {
            System.out.println("Introduce el nombre del tratamiento para crear: ");
            nombreTratamiento = keyboard.nextLine();       
            
            Individual t = getIndividual(nombreTratamiento);
            if (t == null)
                correcta = true;
            else 
                System.out.println("Ya exista el tratamiento");            
        }   
        
        System.out.println("Tratamientos disponibles en el sistema");
        
        String queryString = 
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT * \n" +
            "WHERE { ?accion a :Acción." +
            "}\n"+
            "";

        Query query2 = QueryFactory.create(queryString);
        QueryExecution qe2 = QueryExecutionFactory.create(query2, model1);
        ResultSet results =  qe2.execSelect();
        ResultSetFormatter.out(System.out, results, query2);
        qe2.close();
        Individual T;

        correcta = false;
        String nombreAccion = "";
        while(!correcta) {
           System.out.println("Introduce el nombre de la acción seleccionada");
           nombreAccion = keyboard.nextLine();

            Individual t = getIndividual(nombreAccion);
            
            if (t != null) {
                correcta = true;
                
//                OntClass att = model1.getOntClass(NS + "Acción");
//                Individual I1 = model1.createIndividual(NS + nombreAccion, att);
//                Property nombre = model1.createProperty(NS +"Nombre_accion");
                
//                Property descripcion = model1.createProperty(NS +"Descripcion");
//                Property pertenece = model1.createProperty(NS +"Pertenece_a_tratamiento");
//                model1.add(I1, nombre, nombreAccion);
//                model1.add(I1,descripcion,descripcionAccion);
//                model1.add(I1,pertenece,t);
                
               
                OntClass att = model1.getOntClass(NS + "Tratamiento");
                Individual I1 = model1.createIndividual(NS + nombreTratamiento, att);
                Property nombre = model1.createProperty(NS +"Nombre_tratamiento");
                
                Property pertenece = model1.createProperty(NS +"Pertenece_a_tratamiento");
                Property contiene = model1.createProperty(NS +"Contiene_accion");
                model1.add(I1, nombre, nombreTratamiento);
                model1.add(I1, contiene, t);
                
            }
            else
                System.out.println("Nombre de acción incorrecto");                  
        }  
    }
    
    public void crearPrescripcion() {
        int id = 1;
        String queryString = 
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT ?max \n" +
            "WHERE { ?prescripcion a :Prescripción." +
            "?prescripcion :Id_prescripcion ?max." +    
            "}\n"+
            "ORDER BY DESC(?max) LIMIT 1" +    
            "";
        
        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model1);
        ResultSet results =  qe.execSelect();
        if (results.hasNext()) {
            QuerySolution row = results.nextSolution();
            id = row.getLiteral("max").getInt() + 1;
        }
        qe.close();
        
        System.out.println("Selecciona al paciente al que se va a prescribir: ");
        
        System.out.println("Tratamientos disponibles en el sistema");
        
        String queryPaciente = 
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT * \n" +
            "WHERE { ?tratamiento a :Tratamiento." +
            "}\n"+
            "";

        Query query2 = QueryFactory.create(queryPaciente);
        QueryExecution qe2 = QueryExecutionFactory.create(query2, model1);
        ResultSet resultsPaciente =  qe2.execSelect();
        ResultSetFormatter.out(System.out, resultsPaciente, query2);
        qe2.close();
        Individual T;
        
        boolean correcta = false;
        String nombreTratamiento = "";
        while(!correcta) {
           System.out.println("Introduce el nombre del tratamiento seleccionado");
           nombreTratamiento = keyboard.nextLine();

            Individual t = getIndividual(nombreTratamiento);
            
           /* if (t != null) {
                correcta = true;
                OntClass att = model1.getOntClass(NS + "Acción");
                Individual I1 = model1.createIndividual(NS + nombreA, att);
                Property nombre = model1.createProperty(NS +"Nombre_accion");
                Property descripcion = model1.createProperty(NS +"Descripcion");
                Property pertenece = model1.createProperty(NS +"Pertenece_a_tratamiento");
                model1.add(I1, nombre, nombreAccion);
                model1.add(I1,descripcion,descripcionAccion);
                model1.add(I1,pertenece,t);
            }
            else
                System.out.println("Nombre de tratamiento incorrecto");*/         
           
        }
        
    }
   
    public Individual getIndividual(String nombre)
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
                if (trat.getLocalName().equals(nombre))
                    return trat;
            }
        }
        return null;
        
    }
    
    protected void setup() {
               
        model1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        
        try {
            model1.read("file:" + URL_ONTOLOGIA, "RDF/XML");
        }
        catch (JenaException je) {       
           System.out.println("ERROR");
           je.printStackTrace();
           System.exit(0);
        }
        
         login();
        
        System.out.println("Selecciona una accion a realizar:");
        System.out.println("1. Crear una accion");
        System.out.println("2. Crear un tratamiento");
        System.out.println("3. Crear una prescripcion.");
        String opcion = keyboard.nextLine();
        
        if (opcion.equals("1")) 
           creaAccion();
        else if(opcion.equals("2"))
           creaTratamiento();
        else if(opcion.equals("3"))
           crearPrescripcion();
                
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
                model1.write(new FileOutputStream(URL_ONTOLOGIA, false));
                model1.close();
            } catch (Exception e) {
            }
            
        }     
    }
    
}