package com.ktds.dataark

import com.cloudera.sprue.Patient

class RulesExecutor extends Serializable {
    def evalRules (incomingEvents: Iterator[Patient]): Iterator[Patient] = {
	val ksession = KieSessionFactory.getKieSession()
	val events = incomingEvents.map(event => {
	    ksession.execute(event)
	    event
	})
	events
    }
}
