
   

import java.util.Scanner;
import jade.core.behaviours.CyclicBehaviour;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.JenaException;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class DevicesAgent extends Agent {
    Scanner keyboard = new Scanner(System.in);
    String NS = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#";
    OntModel model;
            
    public class WaitInstructions extends CyclicBehaviour
    {
        public String calcularFecha(long minuts) {
            minuts = minuts * 60000;
            return new Date(new Timestamp(minuts).getTime()).toString();
        }
        
        public void output(String idPacient, String contingut)
        {
            String[] parts = contingut.split(":");
            contingut = parts[1];
            String name  = parts[0];
            ArrayList<String> avisos = new ArrayList <String>();
            
            String QueryString = 
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT ?nombreAparato\n" +
            "WHERE {\n" +   
            "?paciente a :Paciente.\n" +
            "?paciente :Nombre_persona ?nombrePaciente.\n" +
            "?paciente :Padece ?dolencia.\n" +
            "?dolencia :Incompatible ?medioDolencia.\n" +      
            "?paciente :Tiene_acceso ?aparato.\n" +
            "?aparato :Utiliza ?medioAparato.\n" +
            "?aparato :Nombre_aparato ?nombreAparato.\n" +
            "FILTER regex(?nombrePaciente, ?np). \n" +
            "FILTER (?medioDolencia != ?medioAparato).\n" +        
            "}\n"+ "";
            
            ParameterizedSparqlString str = new ParameterizedSparqlString(QueryString);
            str.setLiteral("np", name);
            Query query = QueryFactory.create(str.toString());
            QueryExecution qe2 = QueryExecutionFactory.create(query, model);
            ResultSet results =  qe2.execSelect();
            if (results.hasNext()) {
                QuerySolution row = results.nextSolution();
                System.out.println("Avisa al paciente " + name + " mediante " + row.getLiteral("nombreAparato").getString()
            + " con contedido " + contingut + " en la fecha: "+ calcularFecha(Long.valueOf(parts[2])) );    
            } else {
                System.out.println("Ningun aparell disponible");
            }

            qe2.close();
            
        }
        
        public void action()
        {
            ACLMessage msg = myAgent.receive();
            if(msg != null)
            {
                String s = msg.getContent();
                String command = s.substring(0, Math.min(s.length(), 6));
                String content = s.substring(Math.min(s.length(), 6),s.length());
                //El missatge s'hauria de parsejar(showd:name:action_description)
                //Hotfix
                if (command.equals("showd:")) {
                    output("idPaient",content);  
                } else {
                    System.out.println("Can't process the message");
                }
                /**
                switch (command) {
                    case "showd:":  output("idPaient",content);
                        break;
            
                    default: System.out.println("Can't process the message");
                        break;
                }*/
                
            }
            else block();
        
        }
        
    }
    protected void setup() {
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        
        try {  
           model.read("file:/home/carlos/Documentos/sid/proyecto/projectRDF.owl", "RDF/XML");
        }
        catch (JenaException je) {        
           System.out.println("ERROR");
           je.printStackTrace();
           System.exit(0);
        }  
    
        WaitInstructions b = new WaitInstructions();
        this.addBehaviour(b);
        
        System.out.println("Interface Agent Ready");
    
    }     

}
