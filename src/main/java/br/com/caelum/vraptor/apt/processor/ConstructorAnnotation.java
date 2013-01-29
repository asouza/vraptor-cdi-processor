package br.com.caelum.vraptor.apt.processor;

import javax.annotation.processing.ProcessingEnvironment;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

public class ConstructorAnnotation extends TreeTranslator {
	
	private TreeMaker maker;
	private Names names;
	private JavacElements elements;

	public ConstructorAnnotation(ProcessingEnvironment processingEnv) {
		Context context = ((JavacProcessingEnvironment) processingEnv)
				.getContext();
		maker = TreeMaker.instance(context);
		names = Names.instance(context); // ONLY IN JAVA 7....
		elements = JavacElements.instance(context);
	}

	@Override
	public void visitMethodDef(JCMethodDecl method) {
		super.visitMethodDef(method);
		if (method.getName().contentEquals("<init>")
				&& !method.getParameters().isEmpty()) {
			
			System.out.println("OLD " + method);
			
			boolean containsInject = false;
			boolean containsAutowired = false;
			
			for (JCAnnotation jcAnnotation : method.mods.annotations) {
				System.out.println("annotationType: " + jcAnnotation.annotationType);
				System.out.println("type: " + jcAnnotation.type);
				
				if(jcAnnotation.annotationType.toString().endsWith("Inject"))
					containsInject = true;
				
				if(jcAnnotation.annotationType.toString().endsWith("Autowired"))
					containsAutowired = true;
			}
			
			if(!containsInject){
				JCAnnotation injectAnnotation = maker.Annotation(createElement("javax.inject.Inject"),List.<JCExpression>nil());
				method.mods.annotations = method.mods.annotations.append(injectAnnotation);
			}

			if(!containsAutowired){
				JCAnnotation autoWiredAnnotation = maker.Annotation(createElement("org.springframework.beans.factory.annotation.Autowired"),List.<JCExpression>nil());
				method.mods.annotations = method.mods.annotations.append(autoWiredAnnotation); 
			}

			System.out.println("NEW " + method);
		}

	}
	
	private JCExpression createElement(String name){
		String[] elems = name.split("\\.");
		
		JCExpression e = maker.Ident(names.fromString(elems[0]));
		for (int i = 1 ; i < elems.length ; i++) {
			e = maker.Select(e, names.fromString(elems[i]));
		}
		return e;
	}
	
	
}
