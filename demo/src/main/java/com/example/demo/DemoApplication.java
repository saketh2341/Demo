package com.example.demo;


import java.lang.reflect.*;
import java.nio.file.Path;

import java.util.*;
import java.io.*;
import com.example.*;
//import javafx.util.Pair;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.*;
import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.body.Parameter;

import java.nio.file.Paths;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SpringBootApplication
public class DemoApplication {
     public static Map<String, Vector<String>> hashmap
     = new HashMap<String, Vector<String>>();
     public static Map<String, Vector<Object>> AllArguments
     = new HashMap<String, Vector<Object>>();
     public static Map<String, Integer> LineNumbers
     = new HashMap<String, Integer>();
     public static Map<String, Object> ReturnTypes
     = new HashMap<String, Object>();
     public static Map<String, Integer> ArgumentCount
     = new HashMap<String, Integer>();
	 public static StringBuilder finalUML = new StringBuilder();

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
//        System.out.println(file);
//        private static final Logger lOGGER = LoggerFactory.getLogger(DemoApplication.class);
 Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
 List<ClassOrInterfaceDeclaration> classOrInterfaces = new ArrayList<ClassOrInterfaceDeclaration>();
 List<UMLClass> umlClasses = new ArrayList<UMLClass>();
 List<String> className = new ArrayList<>();

 String path="/Users/saketh123/Downloads/tictactoe-java-master/src/main/java/ttsu/game/tictactoe";
 Path pathToSource = Paths.get(path);
 
 File[] files = readFileFolder(path);
 
 try {
	 SourceRoot sourceRoot = new SourceRoot(pathToSource);
		for(File file : files) {
			CompilationUnit compilationUnit = sourceRoot.parse("",file.getName());
		
//			if(compilationUnit.getPackageDeclaration() != null && 
//					compilationUnit.getPackageDeclaration().isPresent())
//				throw new Exception("All Java Files should be in Default folder");
		 List<EnumDeclaration> nodes=compilationUnit.findAll(EnumDeclaration.class);
		 List<ClassOrInterfaceDeclaration> classes=compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
		 
		 if(classes.size()>0) {
				ClassOrInterfaceDeclaration classOrInterfaceDeclaration = compilationUnit.getNodesByType(ClassOrInterfaceDeclaration.class).get(0);
				String ClassName=classOrInterfaceDeclaration.getNameAsString();
				MethodVisitor mv=new MethodVisitor(ClassName);
				mv.visit(compilationUnit, null);
//				System.out.println("nodes"+classOrInterfaceDeclaration.getFields());
				
				classOrInterfaces.add(classOrInterfaceDeclaration);
		 }
		 else {
			 for(Node node:nodes) {
				 System.out.println("nodes"+node);
			 }
		 
		 }
		 
		}
		
		
	} catch (Exception e) {
		System.out.println("error");
		e.printStackTrace();
	}
 
 try{
		UMLGenerator generator = new UMLGenerator();
		//finalUML.append(getStaticUML());

		classOrInterfaces.stream().forEach(classOrInterface -> {
			UMLClassBuilder umlClassBuilder = new UMLClassBuilder(classOrInterface, classOrInterfaces);
			UMLClass umlclass=umlClassBuilder.buildUMLClass();
			String ClassName=classOrInterface.getNameAsString();
//			AllMethodsInclass.put(ClassName, null);
			List<MethodDeclaration> methods=umlclass.getMethods();
			
			for(MethodDeclaration method:methods) {
				String MethodName=method.getNameAsString();
				List<Parameter> parameters = method.getParameters();
				 System.out.println(MethodName+" "+parameters.size()+"\n");
				 System.out.println(method.getType()+"\n");
				 ReturnTypes.put(MethodName,method.getType());
				 AllArguments.put(MethodName,new Vector<Object>());
				 
				 for (final Parameter parameter : parameters) {
					 Vector<Object> types=AllArguments.get(MethodName);
				      String type = parameter.getType().toString();
				      String name = parameter.getName().getIdentifier();
				      System.out.println(type+" "+name+"\n");
				      
				     types.add(type);
				     AllArguments.put(MethodName,types);
				     

				    }
            
				LineNumbers.put(method.getNameAsString(), method.getBegin().get().line);
			}
			umlClasses.add(umlclass);
			
		});

		finalUML.append(generator.getClassOrInterfaceUML(umlClasses));
		 for(Map.Entry<String, Object> type : ReturnTypes.entrySet()) {
			 System.out.print(type.getValue()+"\n");
		 }
		 
		 for (Map.Entry<String, Vector<String>> me : hashmap.entrySet()) {
			 System.out.print("\n");
			 System.out.print(me.getKey()+"\n");
	          Vector<String>calls=me.getValue();
	          
	          for(int i=0;i<calls.size();i++) {
	        	  String call=calls.get(i);
	        	  classOrInterfaces.stream().forEach(classOrInterface -> {
	        		
	        		 
	        		  List<MethodDeclaration> methods =classOrInterface.getMethodsByName(call);
	        		  String ClassName=classOrInterface.getNameAsString();
	        		  if(methods.size()>0) {
	        			  String Alltypes="(";
	        			  Vector<Object>types=AllArguments.get(call);
	        			  for(int k=0;k<types.size();k++) {
	        				  Alltypes+=types.get(k).toString()+", ";
	        			  }
	        			  Alltypes+=")";
	        			  if(me.getKey().equals(ClassName)&&ArgumentCount.get(call)==AllArguments.get(call).size()) {
	        				  
	        			  System.out.print(call+" is on same class"+ClassName+ " and in line no " + LineNumbers.get(call)+" and the retturn type is "+ReturnTypes.get(call)+ "and arguments " +Alltypes + " \n");}
	        			  else {
	        				  System.out.print(call+" is on other class"+ClassName+ " and in line no " + LineNumbers.get(call)+" and the retturn type is "+ReturnTypes.get(call)+ "and arguments " +Alltypes + " \n");
	        			  }
	        		  }
	      		});
	         
	          }
	          System.out.print("\n");
	         
		 }
	}
 finally{
	
 }
 
        
//        List<PackageDeclaration> packageDeclarations = cu.findAll(PackageDeclaration.class);
//        List<Comments> commnet=cu.getAllComments(Comments.class)
//        for(PackageDeclaration p:packageDeclarations) {
//        	System.out.println(p);
//        }


    }
    
    private static class MethodVisitor extends VoidVisitorAdapter
    {
    	String ClassName;
        @Override
        public void visit(MethodCallExpr methodCall, Object arg)
        {
        	List<Expression> exs=methodCall.getArguments();
        	for(Expression ex:exs) {
        		System.out.println(ex.getClass());
        	}
        	
            System.out.print("Method call: " + methodCall.getName() + "\n");
            List<Expression> args = methodCall.getArguments();
            Vector<String> v=hashmap.get(ClassName);
            if(v==null) {
            	
            	Vector<String>n=new Vector<String>();
            	n.add(methodCall.getName().toString());
            	hashmap.put(ClassName, n);
            	ArgumentCount.put(methodCall.getName().toString(), args.size());
            }
            else {
            	if(!v.contains(methodCall.getName().toString()))
            	{v.add(methodCall.getName().toString());
            	hashmap.put(ClassName, v);
            	ArgumentCount.put(methodCall.getName().toString(), args.size());
            	}
            }
            
           
            System.out.print(args.size());
            for(Expression ar:args) {
            	System.out.println(ar.getChildNodes()+"\n");
            }
            if (args != null)
                handleExpressions(args);
        }

        private void handleExpressions(List<Expression> expressions)
        {
            for (Expression expr : expressions)
            {
                if (expr instanceof MethodCallExpr)
                    {visit((MethodCallExpr) expr, null);
//                    System.out.println(expr.getParentNode().get().getParentNode()+"\n");
                    }
                else if (expr instanceof BinaryExpr)
                {
                    BinaryExpr binExpr = (BinaryExpr)expr;
                    handleExpressions(Arrays.asList(binExpr.getLeft(), binExpr.getRight()));
                }
            }
        }
        MethodVisitor(String cname){
        	this.ClassName=cname;
        }
        
    }
    
    private static File[] readFileFolder(String folderPath) {
		
		File folder = new File(folderPath);
		
		File[] javaFiles = new File[0];
		try {
			FileFilter fileFilter = new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if(isJavaFile(pathname))
						return true;
					return false;
				}
			};
			javaFiles = folder.listFiles(fileFilter);
			
			if(javaFiles.length == 0)
				throw new Exception("No Java Files Found in specified folder");
		} catch (FileNotFoundException e) {
//			LOGGER.error("Please enter valid source folder location");
			e.printStackTrace();
		} catch (Exception e) {
//			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		return javaFiles;
	}

    
    private static boolean isJavaFile(File pathname) {
		return pathname.getName().toLowerCase().endsWith(".java");
	}

}
