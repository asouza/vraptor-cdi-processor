package br.com.caelum.vraptor.apt.eclipse;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.eclipse.jdt.internal.apt.pluggable.core.dispatch.IdeBuildProcessingEnvImpl;

import br.com.caelum.vraptor.apt.TheCompiler;

public class EclipseCompiler implements TheCompiler{

	private final IdeBuildProcessingEnvImpl processingEnv;

	public EclipseCompiler(ProcessingEnvironment processingEnv) {
		this.processingEnv = (IdeBuildProcessingEnvImpl) processingEnv;
	}

	public void addDIAnnotations(Element element) {
		
	}
	
	public void addDefaultConstructor(Element element) {
	}

}
