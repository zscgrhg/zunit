package com.etz.pkg20200330215640

import com.fasterxml.jackson.core.type.TypeReference
import spock.lang.Specification

class ServiceAImplhello1Spec extends Specification {
	void verifyhello() {
		given:
			def subject = new moc.etz.zunit.targets.ServiceAImpl()
			subject.serviceB = Mock(moc.etz.zunit.targets.ServiceBImpl) {
				1 * doServiceB(INPUTS4[0]) >> RETURNED4
			}
		when:
			def ret = subject.hello(*INPUTS1)
		then:
			ret == RETURNED1
	}
	
	
	static final def INPUTS1 = [
			5,
			[
					'a',
					'b',
					'c'
			].reconstruction(new TypeReference<java.util.List<java.lang.String>>() {})
	]
	static final def INPUTS4 = [
			[
					3,
			] as int[]
	]
	
	static final def OUTPUTS1 = [
			5,
			[
					'a',
					'b',
					'c'
			].reconstruction(new TypeReference<java.util.List<java.lang.String>>() {})
	]
	static final def OUTPUTS4 = [
			[
					3,
			] as int[]
	]
	
	static final def RETURNED1 = [
			[
					name   : 'ok',
					counter: 5,
					data   : '1'
			],
			[
					name   : 'ok',
					counter: 5,
					data   : '2'
			],
			[
					name   : 'ok',
					counter: 5,
					data   : '3'
			],
			[
					name   : 'ok',
					counter: 5,
					data   : '4'
			]
	].reconstruction(new TypeReference<java.util.List<moc.etz.zunit.targets.HelloEntity<java.lang.String>>>() {})
	
	static final def RETURNED4 = [
			'3',
	].reconstruction(new TypeReference<java.util.List<java.lang.String>>() {})
	
}