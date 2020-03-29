package com.etz.pkg20200329224549

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
					3,
			] as int[]
	]
	static final def INPUTS3 = [
			[
					1,
			] as int[]
	]
	static final def INPUTS4 = [
			[
					4,
			] as int[]
	]
	static final def INPUTS5 = [
			[
					2,
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
					3,
			] as int[]
	]
	static final def OUTPUTS3 = [
			[
					1,
			] as int[]
	]
	static final def OUTPUTS4 = [
			[
					4,
			] as int[]
	]
	static final def OUTPUTS5 = [
			[
					2,
			] as int[]
	]
	
	static final def RETURNED1 = [[name: 'ok', counter: 5, data: '1'], [name: 'ok', counter: 5, data: '2'], [name: 'ok', counter: 5, data: '3'], [name: 'ok', counter: 5, data: '4']] as java.util.List<moc.etz.zunit.targets.HelloEntity<java.lang.String>>;
	
	static final def RETURNED2 = ['3',] as java.util.List<java.lang.String>;
	
	static final def RETURNED3 = ['1',] as java.util.List<java.lang.String>;
	
	static final def RETURNED4 = ['4',] as java.util.List<java.lang.String>;
	
	static final def RETURNED5 = ['2',] as java.util.List<java.lang.String>;
	
	
}