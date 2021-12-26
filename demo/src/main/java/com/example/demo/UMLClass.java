/**
 * 
 */
package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

/**
 * @author Meenakshi
 *
 */
public class UMLClass {
	
	private String className;
	private String extendedTypes = "";
	private String implementedTypes = "";
	private ArrayList<String[]> umlRelations = new ArrayList<String[]>();
	private ArrayList<String[]> umlAssociations = new ArrayList<String[]>();
	private List<ConstructorDeclaration> constructors = new ArrayList<ConstructorDeclaration>();
	private List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();
	private List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
	private boolean isInterface = false;
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	
	/**
	 * @return the extendedTypes
	 */
	public String getExtendedTypes() {
		return extendedTypes;
	}
	
	/**
	 * @param extendedTypes the extendedTypes to set
	 */
	public void setExtendedTypes(String extendedTypes) {
		this.extendedTypes = extendedTypes;
	}
	
	/**
	 * @return the implementedTypes
	 */
	public String getImplementedTypes() {
		return implementedTypes;
	}
	
	/**
	 * @param implementedTypes the implementedTypes to set
	 */
	public void setImplementedTypes(String implementedTypes) {
		this.implementedTypes = implementedTypes;
	}
	
	/**
	 * @return the umlRelations
	 */
	public ArrayList<String[]> getUmlRelations() {
		return umlRelations;
	}
	
	/**
	 * @param umlRelations the umlRelations to set
	 */
	public void setUmlRelations(ArrayList<String[]> umlRelations) {
		this.umlRelations = umlRelations;
	}
	
	public void addUmlRelations(String[] umlRelation){
		umlRelations.add(umlRelation);
	}
	
	/**
	 * @return the umlAssociations
	 */
	public ArrayList<String[]> getUmlAssociations() {
		return umlAssociations;
	}
	
	public void addUmlAssociations(String[] umlAssociation){
		umlAssociations.add(umlAssociation);
	}
	
	/**
	 * @param umlAssociations the umlAssociations to set
	 */
	public void setUmlAssociations(ArrayList<String[]> umlAssociations) {
		this.umlAssociations = umlAssociations;
	}
	
	/**
	 * @return the constructors
	 */
	public List<ConstructorDeclaration> getConstructors() {
		return constructors;
	}
	
	/**
	 * @param constructors the constructors to set
	 */
	public void setConstructors(List<ConstructorDeclaration> constructors) {
		this.constructors = constructors;
	}
	
	/**
	 * 
	 * @param constructorDeclaration
	 */
	public void addConstructors(ConstructorDeclaration constructorDeclaration){
		this.constructors.add(constructorDeclaration);
	}
	/**
	 * @return the fields
	 */
	public List<FieldDeclaration> getFields() {
		return fields;
	}
	
	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<FieldDeclaration> fields) {
		this.fields = fields;
	}
	
	/**
	 * @return the methods
	 */
	public List<MethodDeclaration> getMethods() {
		return methods;
	}
	
	/**
	 * @param methods the methods to set
	 */
	public void setMethods(List<MethodDeclaration> methods) {
		this.methods = methods;
	}
	
	/**
	 * @return the isInterface
	 */
	public boolean isInterface() {
		return isInterface;
	}
	
	/**
	 * @param isInterface the isInterface to set
	 */
	public void setIsInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}
	
	
	
	
	
	

}
