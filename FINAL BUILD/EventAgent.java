
package sid;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Property;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Stack;
import jade.core.behaviours.CyclicBehaviour;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.JenaException;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.domain.AMSService;


public class EventAgent extends Agent {
    
    OntModel model1;
    String NS = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#";
    long endTime;
    long beginTime;
    
     public class WaitInstructions extends CyclicBehaviour
    {
        public Individual getIndividual(String nombre)
        {
   
            Iterator<OntClass> classesIt = model1.listNamedClasses();
            Individual trat;
            while ( classesIt.hasNext() )
            {
                OntClass actual = classesIt.next();
                OntClass Tratamiento = model1.getOntClass(actual.getURI() );
                for (Iterator i = model1.listIndividuals(Tratamiento); i.hasNext(); )
                {
                    trat = (Individual) i.next();
                    if (trat.getLocalName().equals(nombre))
                        return trat;
                }
            }
            return null;
        
        }
    
        public void creaEventos(int idP,String user) {
            String queryString = 
                "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
                "SELECT DISTINCT ?accion ?fini ?ffi ?cantidad ?calendario \n" +
                "WHERE { \n" +
                "?login a :LogIn.\n" +
                "?login :Username ?user.\n" +    
                "?login :Identifica ?persona.\n" +
                "?persona :Dispone_calendario ?calendario.\n" +   
                "?persona :Tiene_prescrito ?prescripcion.\n"  +
                "?prescripcion :Asigna_tratamiento  ?tratamiento.\n" + 
                "?tratamiento :Contiene_accion ?accion.\n" +
                "?accion :Periodicidad_accion ?timequan.\n" +
                "?timequan :Cantidad ?cantidad.\n" +
                "?prescripcion :Id_prescripcion ?id.\n" +
                "?prescripcion :Periodo_prescripcion ?periodo.\n" +
                "?periodo :Tiempo_inicio ?tini.\n" +
                "?periodo :Tiempo_Final ?tfi.\n" +
                "?tini :Fecha ?fini. \n" +
                "?tfi :Fecha ?ffi.\n" +
                "FILTER (?id = ?idP).\n" +   
                "FILTER regex(?user,?u).\n" +   
                "}\n" + "";    
        
            ParameterizedSparqlString str = new ParameterizedSparqlString(queryString);
            str.setLiteral("idP", idP);
            str.setLiteral("u", user);
       
            Query query = QueryFactory.create(str.toString());
            QueryExecution qe = QueryExecutionFactory.create(query, model1);
            ResultSet results =  qe.execSelect();
       
            while (results.hasNext()) {
                QuerySolution row = results.nextSolution();
                long diff = row.getLiteral("cantidad").getLong();
                long fini = row.getLiteral("fini").getLong();
                long ffi = row.getLiteral("ffi").getLong();
                if (ffi > beginTime) {
                    if (fini < beginTime)
                        fini = beginTime; 
           
                    while (fini <= ffi && fini <= endTime) {  
                        OntClass event = model1.getOntClass(NS + "Evento");
                        OntClass timePoint = model1.getOntClass(NS + "Time_Point");
                        Individual I1 = model1.createIndividual(NS + user + row.getResource("accion").getLocalName() + fini , event);
                        Individual I2 = model1.createIndividual(NS + "tp" + user + row.getResource("accion").getLocalName() + fini , timePoint);
                        Individual accion = getIndividual(row.getResource("accion").getLocalName());
                        Individual calendario = getIndividual(row.getResource("calendario").getLocalName());
                        Property realiza = model1.createProperty(NS + "Realiza_accion");
                        Property tiempo = model1.createProperty(NS +"Tiempo_evento");
                        Property pertenece = model1.createProperty(NS +"Pertenece_a_calendario");
                        Property fecha = model1.createProperty(NS +"Fecha");
                        Property formado_por = model1.createProperty(NS + "Formado_por");
                        model1.add(calendario,formado_por,I1);
                        model1.addLiteral(I2, fecha, fini);
                        model1.add(I1, realiza,accion);
                        model1.add(I1,pertenece,calendario);
                        model1.add(I1,tiempo,I2);
                        fini = fini + diff;
                        System.out.println(diff);
                    }
                }    
            }       
            qe.close(); 
        }
    
    
        public void crearTodosEventos(String time) {
            long currentTime = Long.valueOf(time);
            if ((currentTime +(4*7*24*60))> endTime ) {
                beginTime = endTime;
                endTime = currentTime+(5*7*24*60);
            String queryString = 
                "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
                "SELECT  ?id ?user\n" +
                "WHERE { ?prescripcion a :Prescripción.\n" +
                "?prescripcion :Id_prescripcion ?id.\n" + 
                "?prescripcion :Esta_prescrito_a ?persona.\n" +
                "?persona :Identificado_por ?login.\n" +
                "?login :Username ?user.\n"  +   
                "}\n"+
                "";

                Query query = QueryFactory.create(queryString);

                // Execute the query and obtain results
                QueryExecution qe = QueryExecutionFactory.create(query, model1);
                ResultSet results =  qe.execSelect();
                int id;
                String user;
                if(results.hasNext())  System.out.println("Events nottttgenerated");  
                while(results.hasNext()) {
                    QuerySolution row = results.nextSolution();
                    id = row.getLiteral("id").getInt();
                    user = row.getLiteral("user").getString();
                    creaEventos(id,user);
                }
        
                qe.close();
                System.out.println("Events generated");  
            }
        }
    
        public void action()
            {
                ACLMessage msg = myAgent.receive();
                if(msg != null)
                {
                    String s = msg.getContent();
                    String command = s.substring(0, Math.min(s.length(), 6));
                    String content = s.substring(Math.min(s.length(), 6),s.length());

                    if (command.equals("ctime:")) 
                        crearTodosEventos(content);

                    else System.out.println("Events: Can't process the message: " + s);  

                }
                else block();

            }

        }
        protected void setup() {

            model1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);

            try {
                model1.read("file:/home/bernat/Repo/SID/projectRDF.owl", "RDF/XML");
            }
            catch (JenaException je) {       
               System.out.println("ERROR");
               je.printStackTrace();
               System.exit(0);
            }

            beginTime = endTime = 1432645200;

            WaitInstructions b = new WaitInstructions();
            this.addBehaviour(b);

             System.out.println("Events Agent Ready");
        }
}