package br.com.caelum.vraptor.apt.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;

public class JavacCompiler {

	private final ProcessingEnvironment processingEnv;
	private Trees trees;

	public JavacCompiler(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
		trees = Trees.instance(processingEnv);
	}

	public void addDIAnnotations(Element element) {
		JCTree tree = (JCTree) trees.getTree(element);
		tree.accept(new AnnotationAdder(processingEnv));		
	}
	
	public void addDefaultConstructor(Element element) {
		JCTree tree = (JCTree) trees.getTree(element);
		tree.accept(new DefaultConstructorAdder(processingEnv));		
	}

}
