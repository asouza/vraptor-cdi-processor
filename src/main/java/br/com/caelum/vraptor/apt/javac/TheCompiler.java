package br.com.caelum.vraptor.apt.javac;

import javax.lang.model.element.Element;

public interface TheCompiler {

	void addDIAnnotations(Element element);

	void addDefaultConstructor(Element element);

}