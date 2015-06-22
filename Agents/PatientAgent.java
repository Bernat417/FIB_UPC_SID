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
import jade.domain.FIPAAgentManagement.*;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import jade.domain.AMSService;

public class PatientAgent extends Agent {
     
    public static final long threshold = 43200;
            
    Scanner keyboard = new Scanner(System.in);
    String NS = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#";
    OntModel model;
            
    long endTime, currentTime;
    String username;
    String password;    
    String dniPersona;

    public String calcularFecha(long minuts) {
            minuts = minuts * 60000;
            return new Date(new Timestamp(minuts).getTime()).toString();
        }

    //Comunication Variables
    boolean linked;
    PatientAgent me;
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
                {
                    deviceAgent = agentID;
                    i=allAgents.length;
                }
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
            
            
            if(!results.hasNext()) System.out.println("EMPTY " + minuts);
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
                msg.setContent(avisos.get(i)+":"+minutes);
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
                {
                    checkEvents(content);
                    System.out.println("Reached ");
                }
                else if(command.equals("rlink:"))
                    linked = false;
                
                else System.out.println("Patient: Can't process the message: " + s);
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
    
    public void consultarCalendario(long currentTime) {
        String QueryString =
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT ?fecha ?descripcion\n" +
            "WHERE {\n" +   
            "?login a :LogIn.\n" +
            "?login :Username ?user.\n" +   
            "?login :Identifica ?persona.\n" +
            "?persona :Dispone_calendario ?calendario.\n" +
            "?calendario :Formado_por ?evento.\n" +
            "?evento :Tiempo_evento ?timePoint.\n" +
            "?timePoint :Fecha ?fecha.\n" + 
            "?evento :Realiza_accion ?accion.\n" +
            "?accion :Descripcion ?descripcion.\n" +
            "FILTER regex(?user,?u).\n" +    
            "FILTER(?fecha >= ?current).\n" +
            "FILTER(?fecha <= ?fecha4sem).\n" +
            "}\n" + 
             "ORDER BY (?fecha) ";
        
        
            ParameterizedSparqlString str = new ParameterizedSparqlString(QueryString);
            str.setLiteral("u", username.toString());
            str.setLiteral("current",currentTime);
            str.setLiteral("fecha4sem",(currentTime + 4*7*24*60));
            
            Query query = QueryFactory.create(str.toString());
            QueryExecution qe = QueryExecutionFactory.create(query, model1);
            ResultSet results =  qe.execSelect();
            
            System.out.println("Fecha                           Descripcion");
            System.out.println("------------------------------------------------------------");
            while(results.hasNext()) {
                QuerySolution row = results.nextSolution();
                System.out.print(calcularFecha(row.getLiteral("fecha").getLong()));
                System.out.print(" ");
                System.out.println(row.getLiteral("descripcion").getString());
            }
            System.out.println("------------------------------------------------------------");                        
                
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
        QueryExecution qe2 = QueryExecutionFactory.create(query, model1);
        ResultSet results =  qe2.execSelect();
        while(results.hasNext()) {
             QuerySolution row = results.nextSolution();
             avisos.add("showd:" + username + ":" + row.getLiteral("descripcion").getString());
        }
        
        qe2.close();

        
        return avisos;
        
    }
    

    protected void setup() {
        //Load Model
        linked = false;
        me = this;
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        
        try {  
            model.read("file:/home/bernat/Repo/SID/projectRDF.owl", "RDF/XML");
        }
        catch (JenaException je) {        
           System.out.println("ERROR");
           je.printStackTrace();
           System.exit(0);
        }  
    
        login();
        
        ArrayList <String> avisos = eventsActuals((long)1432674000);
        
        consultarCalendario(1432674000);
        
        //Add Behaviours
        WaitInstructions b = new WaitInstructions();
        this.addBehaviour(b); 
        
        System.out.println("Patient Agent Ready");
    
    }
    
}
                
                
       
