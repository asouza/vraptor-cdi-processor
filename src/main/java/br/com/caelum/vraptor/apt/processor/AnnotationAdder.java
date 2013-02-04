package br.com.caelum.vraptor.apt.processor;

import javax.annotation.processing.ProcessingEnvironment;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

public class AnnotationAdder extends TreeTranslator {
	
	private TreeMaker maker;
	private Names names;
	private JavaCUtils utils;

	public AnnotationAdder(ProcessingEnvironment processingEnv) {
		Context context = ((JavacProcessingEnvironment) processingEnv)
				.getContext();
		maker = TreeMaker.instance(context);
		names = Names.instance(context); // ONLY IN JAVA 7....
		utils = new JavaCUtils(maker, names);
	}

	@Override
	public void visitMethodDef(JCMethodDecl method) {
		super.visitMethodDef(method);
		if (method.getName().contentEquals("<init>")
				&& !method.getParameters().isEmpty()) {
			
			boolean containsInject = false;
			boolean containsAutowired = false;
			
			for (JCAnnotation jcAnnotation : method.mods.annotations) {
//				System.out.println("annotationType: " + jcAnnotation.annotationType);
//				System.out.println("type: " + jcAnnotation.type);
				
				if(jcAnnotation.annotationType.toString().endsWith("Inject"))
					containsInject = true;
				
				if(jcAnnotation.annotationType.toString().endsWith("Autowired"))
					containsAutowired = true;
			}
			
			if(!containsInject){
				JCAnnotation injectAnnotation = maker.Annotation(utils.createElement("javax.inject.Inject"),List.<JCExpression>nil());
				method.mods.annotations = method.mods.annotations.append(injectAnnotation);
			}

			if(!containsAutowired){
				JCAnnotation autoWiredAnnotation = maker.Annotation(utils.createElement("org.springframework.beans.factory.annotation.Autowired"),List.<JCExpression>nil());
				method.mods.annotations = method.mods.annotations.append(autoWiredAnnotation); 
			}

		}

	}
	
	
	
	
}
