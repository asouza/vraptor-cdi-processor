package br.com.caelum.vraptor.apt.processor;

import javax.annotation.processing.ProcessingEnvironment;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import br.com.caelum.vraptor.apt.javac.JavacCompiler;
import br.com.caelum.vraptor.apt.javac.TheCompiler;

public class Compilers {

	public static TheCompiler getCompilerFor(ProcessingEnvironment processingEnv) {
		if (processingEnv instanceof JavacProcessingEnvironment){
			return new JavacCompiler(processingEnv);
		}
		return null;
		
	}
	
	

}
