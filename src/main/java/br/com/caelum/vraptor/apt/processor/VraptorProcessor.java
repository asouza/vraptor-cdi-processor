package br.com.caelum.vraptor.apt.processor;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.tools.Diagnostic;

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;

@SupportedAnnotationTypes("br.com.caelum.vraptor.*")
//@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class VraptorProcessor extends AbstractProcessor {

	private Trees trees;
	private ConstructorVisitor visitor;
	private Set<Element> processedElements = new HashSet<Element>();

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		trees = Trees.instance(processingEnv);
		visitor = new ConstructorVisitor(processingEnv);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		

		if (!roundEnv.processingOver()) {
			System.out.println("Processing Annotations: " + annotations);
			System.out.println();
			
			for (TypeElement typeElement : annotations) {
				Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(typeElement);				
//				Set<? extends Element> elements = roundEnv.getRootElements();				
				System.out.println("Processing Elements: " + elements);
				System.out.println();
				for (Element element : elements) {
					if (element.getKind() == ElementKind.CLASS && !element.getSimpleName().toString().contains("$")) {
						processClass(element);
					}
				}
			}
		}
		return true;
	}

	protected void processClass(Element element) {		
		if(!processedElements.contains(element)){
			if (!haveADefaultContructor(element)) {
				System.out.println("Adding constructor to class " + element.getSimpleName() + "");
				JCTree tree = (JCTree) trees.getTree(element);
				tree.accept(visitor);
				processedElements.add(element);
			}
		}
	}

	private boolean haveADefaultContructor(Element element) {
		boolean findConstructor = false;

		for (Element subelement : element.getEnclosedElements()) {
			if (subelement.getKind() == ElementKind.CONSTRUCTOR ) {
				findConstructor = true;
				TypeMirror mirror = subelement.asType();
				if (mirror.accept(noArgsVisitor, null))
					return true;
			}
		}
		
		return !findConstructor;
	}

	private static final TypeVisitor<Boolean, Void> noArgsVisitor = new SimpleTypeVisitor6<Boolean, Void>() {
		public Boolean visitExecutable(ExecutableType t, Void v) {
			return t.getParameterTypes().isEmpty();
		}
	};

	/**
	 * Prints the content of a object as a note
	 * 
	 * @param obj
	 */
	protected void print(Object obj) {
		if (obj == null) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING,
					"null");
		} else {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING,
					obj.toString());
		}
	}

}
