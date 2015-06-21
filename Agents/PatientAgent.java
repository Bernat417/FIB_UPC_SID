package sid;


import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;
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
import jade.domain.FIPAAgentManagement.*;
    
public class PatientAgent extends Agent {
     
    public static final long threshold = 43200;
            
    Scanner keyboard = new Scanner(System.in);
    String NS = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#";
    OntModel model;
            
    long endTime, currentTime;
    String username;
    String password;
    String dniPersona;

    //Comunication Variables
    boolean linked;
    ClockAgent me;
    AID  deviceAgent;
    
    public class WaitInstructions extends CyclicBehaviour
    {
        
        public void connectAgents()
        {
            int i;
            AMSAgentDescription [] allAgents = null;
         
            //Get all the agents
            try 
            {
                SearchConstraints c = new SearchConstraints();
                c.setMaxResults ( new Long(-1) );
                allAgents = AMSService.search(me, new AMSAgentDescription (), c );
            }
            catch (Exception e) 
            {
                System.out.println("ERROR");
            }

            //Filter the targets
            Stack<AID> pool = new Stack<AID>();

            for (i=0; i<allAgents.length;i++)
            {
                AID agentID = allAgents[i].getName();
                if (agentID.getLocalName().startsWith("devic")) 
                    deviceAgent = agentID;
            } 
            
            linked = true;
        }
        
         public ArrayList<String> eventsActuals(long minuts) {
            ArrayList<String> avisos = new ArrayList <String>();
            String QueryString = 
                "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
                "SELECT ?descripcion\n" +
                "WHERE {\n" +        
                "?login a :LogIn.\n" +
                "?login :Username ?user.\n" +
                "?login :Identifica ?persona.\n" +
                "?persona :Dispone_calendario ?calendario.\n" + 
                "?calendario :Formado_por ?evento.\n" +
                "?evento :Tiempo_evento ?timePoint.\n" +
                "?timePoint :Fecha ?fecha." +
                "?evento :Realiza_accion ?accion.\n" +
                "?accion :Descripcion ?descripcion.\n" +    
                "FILTER regex(?user, ?u). \n" +
                "FILTER (?fecha = ?minuts). \n" +
                "}\n"+ "";  
        
            ParameterizedSparqlString str = new ParameterizedSparqlString(QueryString);
            str.setLiteral("u", username.toString());
            str.setLiteral("minuts",minuts);
        
            Query query = QueryFactory.create(str.toString());
            QueryExecution qe2 = QueryExecutionFactory.create(query, model);
            ResultSet results =  qe2.execSelect();
            while(results.hasNext()) {
                QuerySolution row = results.nextSolution();
                avisos.add("showd:" + username + ":" + row.getLiteral("descripcion").getString());
            }
        
            qe2.close();

            return avisos;
            
        }
        
        public void checkEvents(String minutes)
        {
            if(!linked) connectAgents();
            
            ArrayList <String> avisos = eventsActuals(Long.valueOf(minutes));
        
            for (int i=0; i < avisos.size(); ++i) 
            {
                ACLMessage msg = new ACLMessage( ACLMessage.INFORM );
                msg.setContent(avisos.get(i));
                msg.addReceiver(deviceAgent);
                send(msg);
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
                
                if(command.equals("ctime:") ) 
                    checkEvents(content);
                
                else if(command.equals("rlink:"))
                    linked = false;
                
                else System.out.println("Can't process the message");
            }
            else block();
        }

    }
    
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
            QueryExecution qe2 = QueryExecutionFactory.create(query, model);
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
    
    protected void setup() {
        //Load Model
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        try {  
            model.read("file:/home/bernat/Repo/SID/projectRDF.owl", "RDF/XML");
        }
        catch (JenaException je) {        
           System.out.println("ERROR");
           je.printStackTrace();
           System.exit(0);
        }  
    
        //Add Behaviours
        WaitInstructions b = new WaitInstructions();
        this.addBehaviour(b); 
        
        login();
        
        System.out.println("Patient Agent Ready");
    
    }
    
}
                
                
       
