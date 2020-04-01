package com.etz.pkg20200401194621

import com.fasterxml.jackson.core.type.TypeReference
import spock.lang.Specification

class ServiceAImplhello1Spec extends Specification {
	void verifyhello() {
		given:
			def subject = new moc.etz.zunit.targets.ServiceAImpl()
			subject.serviceB = Mock(moc.etz.zunit.targets.ServiceBImpl) {
				1 * doServiceB(INPUTS3[0]) >> RETURNED3
				1 * doServiceB(INPUTS2[0]) >> RETURNED2
				1 * doServiceB(INPUTS4[0]) >> RETURNED4
				1 * doServiceB(INPUTS5[0]) >> RETURNED5
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
	static final def INPUTS2 = [
			[
					2,
			] as int[]
	]
	static final def INPUTS3 = [
			[
					3,
			] as int[]
	]
	static final def INPUTS4 = [
			[
					1,
			] as int[]
	]
	static final def INPUTS5 = [
			[
					4,
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
	static final def OUTPUTS2 = [
			[
					2,
			] as int[]
	]
	static final def OUTPUTS3 = [
			[
					3,
			] as int[]
	]
	static final def OUTPUTS4 = [
			[
					1,
			] as int[]
	]
	static final def OUTPUTS5 = [
			[
					4,
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
	
	static final def RETURNED2 = [
			'2',
	].reconstruction(new TypeReference<java.util.List<java.lang.String>>() {})
	
	static final def RETURNED3 = [
			'3',
	].reconstruction(new TypeReference<java.util.List<java.lang.String>>() {})
	
	static final def RETURNED4 = [
			'1',
	].reconstruction(new TypeReference<java.util.List<java.lang.String>>() {})
	
	static final def RETURNED5 = [
			'4',
	].reconstruction(new TypeReference<java.util.List<java.lang.String>>() {})
	
}