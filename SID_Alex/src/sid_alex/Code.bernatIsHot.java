	    

	    //Variables globals
	    bool linked;
            AID DevicesAgent;
	    
	    //Al setup
	    linked = false;

	
	    //Al action
            if(!linked){

	    AMSAgentDescription [] agents = null;
            try 
            {
                SearchConstraints c = new SearchConstraints();
                c.setMaxResults ( new Long(-1) );
                agents = AMSService.search(me, new AMSAgentDescription (), c );
            }
            catch (Exception e) {
                System.out.println("ERROR");
            }

            for (int i=0; i<agents.length;i++){
                AID agentID = agents[i].getName();
                if (agentID.getLocalName().startsWith("devic")) 
		{
                    DevicesAgent = agentID;
		    linked = true;
		    i = agents.length;
		}		   
            }
	    
 	    }

	    //Per enviar missatge

            ACLMessage msg = new ACLMessage( ACLMessage.INFORM );
            msg.setContent("HERE GOES THE CONTENT, MY BELOVED HOTTIE");
            msg.addReceiver(DevicesAgent);
            send(msg);

