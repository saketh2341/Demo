/**
 * 
 */
package com.example.demo;

import com.example.demo.Relationship;

/**
 * Enum to track class or interface relatioships
 * 
 * @author Meenakshi
 *
 */
public enum RelationshipType implements Relationship{
	
	
	EXTENDS(" extends ", ""),
	IMPLEMENTS(" implements ", ""),
	USES(" ..> ", "uses");
	//USES(" -() "),
	//PROVIDES(" ()- ");
	
	
	private String symbol;
	private String label;

	private RelationshipType(String symbol, String label){
		this.symbol = symbol;
		this.label = label;
	}
	
	public String getSymbol(){
		return symbol;
	}
	
	public String getLabel() {
		return label;
	}
}
