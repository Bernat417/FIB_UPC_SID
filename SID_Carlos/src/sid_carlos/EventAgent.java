/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sid_carlos;

import jade.core.Agent;

/**
 *
 * @author carlos
 */
public class EventAgent extends Agent {
    public void creaEventos(int idP) {
        String QueryString = 
            "PREFIX :<http://www.semanticweb.org/adriàabella/ontologies/2015/4/untitled-ontology-7#>" +
            "SELECT DISTINCT ?accion ?fini ?ffi\n" +
            "WHERE { \n" +
            "?prescripicion a :Prescripción.\n" +
            "?prescripcion :Asigna_tratamiento  ?tratamiento.\n" + 
            "?tratamiento :Contiene_accion ?accion.\n" +
            "?accion :Periodicidad_accion ?timequan.\n" +
            "?timequan :Cantidad ?cantidad.\n" +
            "?prescripcion :Id_prescripcion ?id.\n" +
            "?prescripcion :Periodo_prescripcion ?periodo.\n" +
            "?periodo :Tiempo_inicio ?tini.\n" +
            "?periodo :Tiempo_Final ?tfi.\n" +
            "?tini :Fecha ?fini. \n" +
            "?tfi :Fecha ?ffi." +
            "" +   
            "}\n" + "";    
    }
}
