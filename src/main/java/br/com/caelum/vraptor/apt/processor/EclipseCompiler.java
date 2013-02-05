package br.com.caelum.vraptor.apt.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import com.sun.source.util.Trees;

public class EclipseCompiler {

	private final ProcessingEnvironment processingEnv;
	private Trees trees;

	public EclipseCompiler(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
		trees = Trees.instance(processingEnv);
	}

	public void addDIAnnotations(Element element) {
		
	}
	
	public void addDefaultConstructor(Element element) {
	}

}
