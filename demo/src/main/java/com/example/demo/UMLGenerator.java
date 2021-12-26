/**
 * 
 */
package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.example.demo.RelationshipType;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.example.demo.AssociationType;

/**
 * @author Meenakshi
 *
 */
public class UMLGenerator {

	public ArrayList<String[]> umlRelations = new ArrayList<String[]>();
	public ArrayList<String[]> umlAssociations = new ArrayList<String[]>();
	public final List<UMLClass> umlClasses = new ArrayList<UMLClass>();
	public String getClassOrInterfaceUML(List<UMLClass> umlClasses){
		StringBuilder builder = new StringBuilder("@startuml");
		this.umlClasses.addAll(umlClasses);
		for(UMLClass umlClass : umlClasses) {
			getClassOrInterfaceUML(builder, umlClass);
			getConstructorUML(builder, umlClass);
			getFieldsUML(builder, umlClass);
			getMethodsUML(builder, umlClass);
			addClosingUML(builder);
			removeDuplicateAssociations(umlClass.getUmlAssociations());
			removeDuplicateRelationships(umlClass.getUmlRelations());
		}
		addClassOrInterfaceRelations(builder, umlRelations, false);
		addClassOrInterfaceRelations(builder, umlAssociations, true);
		builder.append("\n@enduml");
//		System.out.println(builder.toString());
		return builder.toString();
	}

	private void getConstructorUML(StringBuilder builder, UMLClass umlClass) {
		List<ConstructorDeclaration> constructorDeclarations = umlClass.getConstructors();
		for(ConstructorDeclaration constructorDeclaration : constructorDeclarations) {
			builder.append("\n +" + constructorDeclaration.getNameAsString() + "(" );
			NodeList<Parameter> parameters = constructorDeclaration.getParameters();
			Map<String, String> paramterNameTypeMap = getParameterNameTypeMap(parameters);
			builder.append(StringUtils.join(paramterNameTypeMap.entrySet(), ',').replaceAll("=", " : "));
			builder.append(")");
		}
	}

	/**
	 * @param builder
	 */
	private void addClosingUML(StringBuilder builder) {
		builder.append("\n}\n");
	}

	/**
	 * @param builder
	 * @param classOrInterface
	 */
	private void getClassOrInterfaceUML(StringBuilder builder, UMLClass umlClass) {
		String classOrInterfaceName = umlClass.getClassName();
		if(umlClass.isInterface())
			builder.append("\ninterface " + classOrInterfaceName);
		else {
			builder.append("\nclass " + classOrInterfaceName);
		}
		if(umlClass.getExtendedTypes() != null && umlClass.getExtendedTypes() != "")
			builder.append(RelationshipType.EXTENDS.getSymbol() + umlClass.getExtendedTypes());
		if(umlClass.getImplementedTypes() != null && umlClass.getImplementedTypes() != "")
			builder.append(RelationshipType.IMPLEMENTS.getSymbol() + umlClass.getImplementedTypes());
		builder.append(" {");
	}


	/**
	 * To add class relationships
	 * 
	 * @param builder
	 * @param classRelations
	 */
	private void addClassOrInterfaceRelations(StringBuilder builder, ArrayList<String[]> classRelations, boolean isAssociation) {
		for(String[] relation : classRelations){
			Relationship relationship;
			if(isAssociation)
				relationship = Enum.valueOf(AssociationType.class, relation[2]);
			else
				relationship = Enum.valueOf(RelationshipType.class, relation[2]);
			String label = relationship.getLabel();
			String umlRelation = "\n" + relation[0] + relationship.getSymbol() + relation[1];
			if(label != null && label != "")
				umlRelation = umlRelation + " : " + label;
			builder.append(umlRelation); 
		}
	}

	/**
	 * Returns the Plant UML notation for Class/Interface fields
	 * Only Returns Public and Private Fields
	 * 
	 * @param builder
	 * @param fields
	 * @throws ClassNotFoundException 
	 */
	private void getFieldsUML(StringBuilder builder, UMLClass umlClass) {
		List<FieldDeclaration> fields = umlClass.getFields();
		for(FieldDeclaration field : fields) {
			String containedClass = field.getVariable(0).getType().toString();
			if(getClassOrInterfaceByName(containedClass) != null || UMLClassBuilder.isACollection(containedClass)){

			}else {
				String fieldName = field.getVariable(0).toString();
				if(field.isPrivate()){
					if(hasGetter(umlClass, fieldName) && hasSetter(umlClass, fieldName))
						builder.append("\n +" + fieldName + " : " + field.getCommonType());
					else
						builder.append("\n -" + fieldName + " : " + field.getCommonType());
				}else if (field.isPublic()){
					builder.append("\n +" + fieldName + " : " + field.getCommonType());
				}
			}
		}
	}

	/**
	 * Check if the relationship already exists to avoid duplicates
	 * 
	 * @param umlRelations
	 */
	private void removeDuplicateRelationships(ArrayList<String[]> umlRelations) {
		for(String[] umlRelation : umlRelations){
			String containingClass = umlRelation[0];
			String containedClass = umlRelation[1];
			RelationshipType relationshipType = Enum.valueOf(RelationshipType.class, umlRelation[2]);
			boolean iPresent = this.umlRelations.stream().anyMatch( p -> (p[0].equals(containingClass) && p[1].equals(containedClass)) ||
					p[0].equals(containedClass) && p[1].equals(containingClass));
			if(!iPresent){
				this.umlRelations.add(new String[]{containingClass, containedClass, relationshipType.toString()});
			}

		}
	}

	/**
	 * Check if the association already exists to avoid duplicates
	 * 
	 * @param umlAssociations
	 */
	private void removeDuplicateAssociations(ArrayList<String[]> umlAssociations) {
		for(String[] umlAssociation : umlAssociations){
			String containingClass = umlAssociation[0];
			String containedClass = umlAssociation[1];
			AssociationType associationType = Enum.valueOf(AssociationType.class, umlAssociation[2]);
			boolean isPresent = this.umlAssociations.stream().anyMatch(p -> (associationType.equals(AssociationType.ONE_TO_ONE)  && 
					OneToManyRelationExists(containedClass, containingClass) || ( p[0].equals(containedClass) &&  p[1].equals(containingClass))));
			if(!isPresent){
				this.umlAssociations.add(new String[]{containingClass, containedClass, associationType.toString()});
			}

		}

	}

	private void getMethodsUML(StringBuilder builder, UMLClass umlClass) {
		List<MethodDeclaration> methods = umlClass.getMethods();
		//TODO - remove the check for public protected - see if static and abstract methods included if not public
		List<MethodDeclaration> filteredMethods = new ArrayList<MethodDeclaration>();
		filteredMethods = methods.stream().filter(method -> !isGetterOrSetterMethod(umlClass, method.getNameAsString())).collect(Collectors.toList());
		for(MethodDeclaration method : filteredMethods){
			if(method.isPublic() /*&& isNotIncludedInParent(method.getNameAsString(), classOrInterface)*/){
				builder.append("\n +" + method.getNameAsString() + "(");
				NodeList<Parameter> parameters = method.getParameters();
				Map<String, String> paramterNameTypeMap = getParameterNameTypeMap(parameters);
				builder.append(StringUtils.join(paramterNameTypeMap.entrySet(), ',').replaceAll("=", " : "));
				builder.append(") : " + method.getType());
			}

		}
	}

	/**
	 * To ignore the method if present in parent class/interface
	 * @param methodName 
	 * 
	 * @param classOrInterface
	 * @return
	 */
	private boolean isNotIncludedInParent(String methodName, ClassOrInterfaceDeclaration classOrInterface) {
		NodeList<ClassOrInterfaceType> implementedTypes =  classOrInterface.getImplementedTypes();
		for(ClassOrInterfaceType parent : implementedTypes){
			UMLClass umlClass = getClassOrInterfaceByName(parent.getNameAsString());
			List<MethodDeclaration> parentMethods = umlClass.getMethods();
			for(MethodDeclaration parentMethod : parentMethods){
				if(parentMethod.getNameAsString().equalsIgnoreCase(methodName))
					return false;
			}
		}
		return true;
	}

	/**
	 * Check if the method is either getter or setter method
	 * 
	 * @param classOrInterface
	 * @param methodName
	 * @param fieldName
	 * @return
	 */
	private boolean isGetterOrSetterMethod(UMLClass umlClass, String methodName) {
		String fieldName = "";
		if(methodName.startsWith("get"))
			fieldName = StringUtils.substringAfter(methodName, "get");
		if(methodName.startsWith("set"))
			fieldName = StringUtils.substringAfter(methodName, "set");
		for(FieldDeclaration field : umlClass.getFields()){
			String variable = field.getVariable(0).getNameAsString();
			variable = variable.substring(0, 1).toUpperCase() + variable.substring(1);
			if(variable.equals(fieldName))
				return true;
		}
		return false;
	}

	/**
	 * Check if the corresponding getter method exists for a given field
	 * 
	 * @param umlClass
	 * @param fieldname
	 * @return
	 */
	private boolean hasGetter(UMLClass umlClass, String fieldname) {
		String field = "";
		for(MethodDeclaration method : umlClass.getMethods()){
			String methodName = method.getNameAsString();
			if(methodName.startsWith("get")){
				field = StringUtils.substringAfter(methodName, "get");
				field = field.substring(0, 1).toLowerCase() + field.substring(1);
				if(field.equals(fieldname))
					return true;
			}
		}
		return false;
	}

	/**
	 * Check if the corresponding setter method exists for a given field
	 * 
	 * @param umlClass
	 * @param fieldname
	 * @return
	 */
	private boolean hasSetter(UMLClass umlClass, String fieldname) {
		String field = "";
		for(MethodDeclaration method : umlClass.getMethods()){
			String methodName = method.getNameAsString();
			if(methodName.startsWith("set")){
				field = StringUtils.substringAfter(methodName, "set");
				field = field.substring(0, 1).toLowerCase() + field.substring(1);
				if(field.equals(fieldname))
					return true;
			}
		}
		return false;
	}

	/**
	 * Get the class or interface declaration by name
	 * 
	 * @param classOrInterfaceName
	 * @return
	 */
	public UMLClass getClassOrInterfaceByName(String classOrInterfaceName){
		for(UMLClass umlClass: umlClasses){
			if(umlClass.getClassName().equalsIgnoreCase(classOrInterfaceName))
				return umlClass;
		}
		return null;

	}


	/**
	 * For given two class, checks if one to many relation exists
	 * 
	 * @param containingClass
	 * @param containedClass
	 * @return
	 */
	public boolean OneToManyRelationExists(String containingClass, String containedClass){
		for(UMLClass umlClass : umlClasses){
			if(umlClass.getClassName().equalsIgnoreCase(containingClass)){
				List<FieldDeclaration> fields = umlClass.getFields();
				for(FieldDeclaration field : fields) {
					String fieldClass = field.getVariable(0).getType().toString();
					if(UMLClassBuilder.isACollection(fieldClass)){
						fieldClass = StringUtils.substringBetween(fieldClass, "<", ">");
						if(fieldClass.equalsIgnoreCase(containedClass))
							return true;
					}
				}
			}
		}
		return false;
	}	

	public Map<String, String> getParameterNameTypeMap(NodeList<Parameter> parameters){
		Map<String, String> paramterNameTypeMap = new HashMap<String, String>();
		parameters.stream().forEach(parameter -> {
			String className = parameter.getType().toString();
			paramterNameTypeMap.put(parameter.getNameAsString(), className);
		});
		return paramterNameTypeMap;
	}

}
