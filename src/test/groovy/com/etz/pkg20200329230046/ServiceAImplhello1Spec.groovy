package com.etz.pkg20200329230046

import spock.lang.Specification;


class ServiceAImplhello1Spec extends Specification {
	void verifyhello() {
		given:
			def subject = new moc.etz.zunit.targets.ServiceAImpl()
			subject.serviceB = Mock(moc.etz.zunit.targets.ServiceBImpl) {
				1 * doServiceB(INPUTS4[0]) >> RETURNED4
				1 * doServiceB(INPUTS2[0]) >> RETURNED2
				1 * doServiceB(INPUTS3[0]) >> RETURNED3
				1 * doServiceB(INPUTS5[0]) >> RETURNED5
			}
		when:
			1 == 1
		then:
			1 == 1
	}
	
	
	static final def INPUTS1 = [
			5,
			[
					'a',
					'b',
					'c'
			] as java.util.List<java.lang.String>
	]
	static final def INPUTS2 = [
			[
					2,
			] as int[]
	]
	static final def INPUTS3 = [
			[
					4,
			] as int[]
	]
	static final def INPUTS4 = [
			[
					3,
			] as int[]
	]
	static final def INPUTS5 = [
			[
					1,
			] as int[]
	]
	
	static final def OUTPUTS1 = [
			5,
			[
					'a',
					'b',
					'c'
			] as java.util.List<java.lang.String>
	]
	static final def OUTPUTS2 = [
			[
					2,
			] as int[]
	]
	static final def OUTPUTS3 = [
			[
					4,
			] as int[]
	]
	static final def OUTPUTS4 = [
			[
					3,
			] as int[]
	]
	static final def OUTPUTS5 = [
			[
					1,
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
	] as java.util.List<moc.etz.zunit.targets.HelloEntity<java.lang.String>>
	;
	
	static final def RETURNED2 = [
			'2',
	] as java.util.List<java.lang.String>
	;
	
	static final def RETURNED3 = [
			'4',
	] as java.util.List<java.lang.String>
	;
	
	static final def RETURNED4 = [
			'3',
	] as java.util.List<java.lang.String>
	;
	
	static final def RETURNED5 = [
			'1',
	] as java.util.List<java.lang.String>
	;
	
	
}