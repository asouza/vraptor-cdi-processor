package br.com.caelum.vraptor.apt.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

public class ProcessorPrinter {

	ProcessingEnvironment env;

	public ProcessorPrinter(ProcessingEnvironment env) {
		this.env = env;
	}

	public void message(Object obj) {
		print(obj, Kind.NOTE);
	}

	public void warn(Object obj) {
		print(obj, Kind.WARNING);
	}

	public void error(Object obj) {
		print(obj, Kind.ERROR);
	}

	public void print(Object obj, Diagnostic.Kind priority) {
		if (obj == null) {
			env.getMessager().printMessage(priority, "null");
		} else {
			env.getMessager().printMessage(priority, obj.toString());
		}
	}

}
