package com.etz.pkg20200329202852

import spock.lang.Specification;


class ServiceAImplhello1Spec extends Specification {
	void verifyhello() {
		given:
			def subject = new moc.etz.zunit.targets.ServiceAImpl()
		when:
			1 == 1
		then:
			1 == 1
	}
	
	
}