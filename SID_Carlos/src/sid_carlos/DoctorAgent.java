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
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.shared.JenaException;
import jade.core.Agent;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

/**
 *
 * @author carlos
 */
public class DoctorAgent extends Agent {
     
    private String URL_ONTOLOGIA = "/home/carlos/Documentos/sid/proyecto/projectRDF.owl";
    Scanner keyboard = new Scanner(System.in);
    String NS = "http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#";
    String username;
    String password;
    String dniPersona;
    long currentTime;
    OntModel model1;
    
        public String calcularFecha(long minuts) {
            minuts = minuts * 60000;
            return new Date(new Timestamp(minuts).getTime()).toString();
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
            "?login :Identifica_asistente ?persona." +         
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
        
    public void consultaCalendarios() {
        boolean correcta = false;
        String paciente = "";
        while (!correcta) {
            String QueryString =
                "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
                "SELECT ?paciente\n" +
                "WHERE {" +   
                "?login a :LogIn.\n" +
                "?login :Username ?user.\n" +   
                "?login :Identifica ?persona.\n" +
                "?persona :Trata ?paciente.\n" +    
                "FILTER regex(?user,?u).\n" +    
                "}\n" + " ";
            
            ParameterizedSparqlString str = new ParameterizedSparqlString(QueryString);
            str.setLiteral("u", username.toString());
            Query query = QueryFactory.create(str.toString());
            QueryExecution qe = QueryExecutionFactory.create(query, model1);
            ResultSet results =  qe.execSelect();
            ResultSetFormatter.out(System.out, results, query);
            System.out.println("Escoje uno de los pacientes disponibles: ");
            paciente = keyboard.nextLine();
            Individual i = getIndividual(paciente);
            qe.close();
            if (i != null) {
                correcta = true;
                 String QueryString2 =
                "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
                "SELECT ?userp\n" +
                "WHERE {" +   
                "?login a :LogIn.\n" +
                "?login :Identifica_asistente ?persona. \n" +   
                "?login :Username ?user.\n" +
                "?persona :Trata ?paciente.\n" +
                "?paciente :Nombre_persona ?nombre.\n"  +       
                "?paciente :Identificado_por ?loginp.\n" +
                "?loginp :Username ?userp."  +
                "FILTER regex(?nombre,?selec)." +
                "FILTER regex(?user,?u).\n" +         
                "}\n" + " ";
                 
                ParameterizedSparqlString str2 = new ParameterizedSparqlString(QueryString2);
                System.out.println(paciente + username);
                str2.setLiteral("selec", paciente);
                str2.setLiteral("u",username);
                Query query2 = QueryFactory.create(str2.toString());
                QueryExecution qe2 = QueryExecutionFactory.create(query2, model1);
                ResultSet results2 =  qe2.execSelect();
                if (results2.hasNext()) 
                 calendarioPaciente(currentTime, paciente);  
                qe2.close();
            }    
                
        }
        
            
    }    
    
    public void calendarioPaciente(long currentTime,String paciente) {
        String QueryString =
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT ?fecha ?descripcion\n" +
            "WHERE {\n" +   
            "?login a :LogIn.\n" +
            "?login :Username ?user.\n" +   
            "?login :Identifica_asistente ?persona.\n" +
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
            str.setLiteral("u", paciente.toString());
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
    
    private void creaAccion() {    
        boolean correcta = false;
        String nombreAccion = "";
        String descripcionAccion  = "";
        while (!correcta) {
            System.out.println("Introduce el nombre de la acción: ");
            nombreAccion = keyboard.nextLine();       
            System.out.println("Introduce la descripcion de la accion");
            descripcionAccion = keyboard.nextLine();
            
//            Individual t = getIndividual(nombreAccion);
//            Individual t = getIndividualGeneral(model1, nombreAccion, "Nombre_tratamiento");
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

        executeAndPrintQuery(model1, queryString);

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
        String nombreTratamiento = "";
        
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

        executeAndPrintQuery(model1, queryString);
        
        correcta = false;
        String nombreAccion = "";
        while(!correcta) {
           System.out.println("Introduce el nombre de la acción seleccionada");
           nombreAccion = keyboard.nextLine();

            Individual t = getIndividual(nombreAccion);
            
            if (t != null) {
                correcta = true;
                
                OntClass att = model1.getOntClass(NS + "Tratamiento");
                Individual I1 = model1.createIndividual(NS + nombreTratamiento, att);
                Property nombre = model1.createProperty(NS +"Nombre_tratamiento");
                Property contiene = model1.createProperty(NS +"Contiene_accion");
                model1.add(I1, nombre, nombreTratamiento);
                model1.add(I1, contiene, t);                
            }
            else
                System.out.println("Nombre de acción incorrecto");                  
        }  
    }
    
    public void crearPrescripcion() {
        int idPrescripcion = 1;
        boolean correcta = false;
        String nombrePaciente = "";
        String nombreTratamiento = "";
        Query query;
        QueryExecution qe;
        ResultSet results;
        QuerySolution row;
        
        // Obtenemos el idPrescipcion
        String queryString = 
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT ?max \n" +
            "WHERE { ?prescripcion a :Prescripción." +
            "?prescripcion :Id_prescripcion ?max." +    
            "}\n"+
            "ORDER BY DESC(?max) LIMIT 1" +    
            "";
        
        query = QueryFactory.create(queryString);
        qe = QueryExecutionFactory.create(query, model1);
        results =  qe.execSelect();
        if (results.hasNext()) {
            row = results.nextSolution();
            idPrescripcion = row.getLiteral("max").getInt() + 1;
        }
        qe.close();
        
        
        //obtenemos el paciente
        System.out.println("Selecciona el paciente al que se va a prescribir: ");
        String queryPaciente = 
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT * \n" +
            "WHERE { ?paciente a :Paciente." +
            "}\n"+
        
                "";
        executeAndPrintQuery(model1,queryPaciente);
        
        while(!correcta) {
           System.out.println("Introduce el nombre del Paciente");
           nombrePaciente = keyboard.nextLine();

            Individual t = getIndividual(nombrePaciente);
            if(t != null) {
                correcta = true;
            }
            else {
                System.out.println("Nombre incorrecto"); 
            }    
        }
        
        // Obtenemos el tratamiento
        System.out.println("Tratamientos disponibles en el sistema");
        String queryTratamiento = 
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT * \n" +
            "WHERE { ?tratamiento a :Tratamiento." +
            "}\n"+
            "";
        
        executeAndPrintQuery(model1,queryTratamiento);
        
        correcta = false;
        while(!correcta) {
            System.out.println("Introduce el nombre del tratamiento");
            nombreTratamiento = keyboard.nextLine();

            Individual t = getIndividual(nombreTratamiento);
            if(t != null) {
                correcta = true;
            }
            else {
                System.out.println("Nombre incorrecto"); 
            }                    
        }    
        
        //tenemos que crear los time_point y asociarlos a un time_interval
        int anoIni,mesIni,diaIni,anoFi,mesFi,diaFi;
        correcta = false;
        
        System.out.println("Introduce la fecha de inicio"); 
        Boolean cAno = false, cMes = false, cDia = false;

        // fecha de inici
        do{
            System.out.println("Año de inicio"); 
            anoIni = Integer.parseInt(keyboard.nextLine());
            if(anoIni > 0 && anoIni < 9999) {
                cAno = true;
            }
            else {
                System.out.println("Año incorrecto"); 
            }
        }while(!cAno);

        do{
            System.out.println("Mes de inicio"); 
            mesIni = Integer.parseInt(keyboard.nextLine());
            if(mesIni > 0 && mesIni < 13) {
                cMes = true;
                mesIni--;
            }
            else {
                System.out.println("Mes incorrecto");
            }
        }while(!cMes);

        do{
            System.out.println("Dia de inicio"); 

            int diasMax = numDiasMes(anoIni,mesIni);

            diaIni = Integer.parseInt(keyboard.nextLine());
            if(diaIni > 0 && diaIni < diasMax) {
                cDia = true;
            }
            else {
                System.out.println("Dia incorrecto");
            }
        }while(!cDia);


        // fecha de fin
        cAno = cMes = cDia = false;
        Boolean anoIgual = false;
        Boolean mesIgual = false;
        do{
            System.out.println("Año de Fin"); 
            anoFi = Integer.parseInt(keyboard.nextLine());
            if(anoFi > 0 && anoFi < 9999) {
                if(anoFi >= anoIni) {
                    anoIgual = (anoFi == anoIni);
                    cAno = true;
                }
                else {
                    System.out.println("Error: Año inicio superior ha año fin"); 
                }                    
            }
            else {
                System.out.println("Año incorrecto"); 
            }
        }while(!cAno);

        do{
            System.out.println("Mes de fin"); 
            mesFi = Integer.parseInt(keyboard.nextLine());
            if(mesFi > 0 && mesFi < 13) {
                if(anoIgual) {
                    if(mesFi >= mesIni) {
                        mesIgual = (mesFi == mesIni);
                        cMes = true;
                        mesIni--;
                    }
                    else {
                        System.out.println("Error: Mes inicio superior ha mes Fin"); 
                    }
                }
                else {
                    cMes = true;
                    mesIni--;
                }
            }
            else {
                System.out.println("Mes incorrecto");
            }
        }while(!cMes);

        do{
            System.out.println("Dia de inicio"); 

            int diasMax = numDiasMes(anoFi,mesFi);

            diaFi = Integer.parseInt(keyboard.nextLine());
            if(diaFi > 0 && diaFi < diasMax) {
                if(mesIgual) {
                    if(diaFi >= diaIni) {
                        cDia = true;                            
                    }
                    else {
                        System.out.println("Error: Dia inici superior ha dia fin"); 
                    }
                }
                else {
                    cDia = true;
                }                    
            }
            else {
                System.out.println("Dia incorrecto");
            }
        }while(!cDia);              

        // Creamos los times_points y el time_interval
        Individual tP1 = createTimePoint(anoIni, mesIni, diaIni);
        Individual tP2 = createTimePoint(anoFi, mesFi, diaFi);
        
        // Creamos el time_interval
        Individual timeInterval = createTimeInterval(tP1,tP2);
        Individual paciente = getIndividual(nombrePaciente);
        Individual tratamiento = getIndividual(nombreTratamiento);
        Individual asistente_Sanitario = getIndividualFromUsername();
        
        // Creamos la prescripción
        createPrescripcion(asistente_Sanitario, tratamiento, paciente, timeInterval, idPrescripcion);    
    }
    
    /**
     * Metodo para crear una prescripción con los individual por parametro.
     * @param medico
     * @param tratamiento
     * @param paciente
     * @param time
     * @param id
     * @return Individual, Prescripcion
     */
    public Individual createPrescripcion(Individual medico, Individual tratamiento
    ,Individual paciente, Individual time, int id) {
        
        String nombre = "prescripcion_" + medico.getLocalName() + "_" + tratamiento.getLocalName()
                + "_" + paciente.getLocalName() + "_" + time.getLocalName();
        
        OntClass att = model1.getOntClass(NS + "Prescripción");
        Individual I1 = model1.createIndividual(NS + nombre, att);
        
        Property prescrito_por = model1.createProperty(NS +"Prescrito_por");      
        Property asigna_tratamiento = model1.createProperty(NS +"Asigna_tratamiento");
        Property esta_prescrito_a = model1.createProperty(NS +"Esta_prescrito_a");
        Property periodo_prescripcion = model1.createProperty(NS +"Periodo_prescripcion");
        Property id_prescripcion = model1.createProperty(NS +"Id_prescripcion");
        
        model1.add(I1, prescrito_por, medico);
        model1.add(I1, asigna_tratamiento, tratamiento);
        model1.add(I1, esta_prescrito_a, paciente);
        model1.add(I1, periodo_prescripcion, time);
        model1.addLiteral(I1, id_prescripcion, id);
        
        return I1;
    }
       
    /**
     * Metodo para obtener el Individual de usuario registrado
     * @return Individual, Asistente
     */
    public Individual getIndividualFromUsername() {
        Individual user = null;
        String QueryString = 
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT ?persona\n" +
            "WHERE {\n" +        
            "?login a :LogIn.\n" +
            "?login :Username ?user.\n" +
            "?login :Password ?pass.\n" +
            "?login :Identifica_asistente ?persona." +         
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
                QuerySolution row = results.nextSolution();                
                user = getIndividual(row.getResource("persona").getLocalName());
            }    
            else
                System.out.println("Usuario o contraseña incorrectos");
            qe2.close();
            
            return user;
    }
    
    /**
     * Metodo para crear un Time_Interval con dos Times_Points
     * @param ep1
     * @param ep2
     * @return Individual Time_Interval
     */
    public Individual createTimeInterval(Individual ep1, Individual ep2) {
        String nombre = "time_interval_" + ep1.getLocalName() + "_" + ep2.getLocalName();
        OntClass att = model1.getOntClass(NS + "Time_Interval");
        Individual I1 = model1.createIndividual(NS + nombre, att);
        
        Property timep_inicio = model1.createProperty(NS +"Tiempo_inicio");
        Property timep_fi = model1.createProperty(NS +"Tiempo_fi");
        
        model1.add(I1, timep_inicio, ep1);
        model1.add(I1, timep_fi, ep2);
        
        return I1;
    }
    
    /**
     * Crea un TimePoint con el nombre: time_point_(timestamp en minutos)
     * @param ano
     * @param mes
     * @param dia
     * @return Individual Time_Point
     */
    public Individual createTimePoint(int ano, int mes, int dia) {
        long timestamp = getTimeStampfromDate(ano, mes, dia);
        timestamp /= (long)60000;
        
        String nombre = "time_point_" + timestamp;
        
        OntClass att = model1.getOntClass(NS + "Time_point");
        Individual I1 = model1.createIndividual(NS + nombre, att);
        
        Property fecha = model1.createProperty(NS +"Fecha");
        model1.add(I1, fecha, Long.toString(timestamp));
        
        return I1;
    }
    
    /**
     * Metodo para obtener el timestamp de año,mes,dia
     * @param ano
     * @param mes
     * @param dia
     * @return long, timestamp
     */
    public long getTimeStampfromDate(int ano, int mes, int dia) {
        String str_date=mes+"-"+dia+"-"+ano;
        DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        long timestamp = 0;
        try {
            Date date = (Date)formatter.parse(str_date); 
            long output=date.getTime()/1000L;
            String str=Long.toString(output);
            timestamp = Long.parseLong(str) * 1000;
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        
        return timestamp;        
    }
    
    /**
     * Metodo para consular el numero de dias de un mes y año indicado
     * @param ano
     * @param mes
     * @return int, numero de dias
     */
    public int numDiasMes(int ano, int mes) {        
        Calendar cal = new GregorianCalendar(ano, mes, 1);         
        int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
        
        return days;
    }
   
    /**
     * Metodo para mostrar el resultado de una query
     * @param model
     * @param q 
     */
    public void executeAndPrintQuery(OntModel model, String q) {
        Query query = QueryFactory.create(q);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results =  qe.execSelect();
        ResultSetFormatter.out(System.out, results, query);
        qe.close();
    }
    
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

    protected void setup() {
               
        model1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        currentTime = 1432731600;
        
        try {
            model1.read("file:" + URL_ONTOLOGIA, "RDF/XML");
        }
        catch (JenaException je) {       
           System.out.println("ERROR");
           je.printStackTrace();
           System.exit(0);
        }
        
        login();
        
        printMenu();
        String opcion = keyboard.nextLine();
        switch(opcion) {
            case "1":
                creaAccion();
                break;
            case "2":
                creaTratamiento();
                break;
            case "3":
                crearPrescripcion();
                break;
            case "4":
                consultaCalendarios();
                break;    
        }
        
        if (!model1.isClosed())
        {
            try {
                model1.write(new FileOutputStream(URL_ONTOLOGIA, false));
                model1.close();
            } catch (Exception e) {
            }
            
        }     
    }
    
    private void printMenu() {
        System.out.println("Selecciona una accion a realizar:");
        System.out.println("1. Crear una accion");
        System.out.println("2. Crear un tratamiento");
        System.out.println("3. Crear una prescripcion.");
        
    }
    
}