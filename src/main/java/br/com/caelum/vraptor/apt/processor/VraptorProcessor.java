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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.SimpleTypeVisitor6;

import br.com.caelum.vraptor.apt.TheCompiler;


@SuppressWarnings("restriction")
@SupportedAnnotationTypes("br.com.caelum.vraptor.*")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class VraptorProcessor extends AbstractProcessor {

	private Set<Element> processedElements = new HashSet<Element>();
	private TheCompiler compiler;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		compiler = Compilers.getCompilerFor(processingEnv);;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		

		if (!roundEnv.processingOver()) {
//			System.out.println("Processing Annotations: " + annotations);
//			System.out.println();
			
			for (TypeElement typeElement : annotations) {
//				System.out.println("Processing: " + typeElement);
				Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(typeElement);				
//				System.out.println("Processing Elements: " + elements);
//				System.out.println();
				for (Element element : elements) {
					if ( element.getKind() == ElementKind.CLASS && 
						!element.getSimpleName().toString().contains("$") && 
						!element.getModifiers().contains(Modifier.STATIC) &&
						!element.getModifiers().contains(Modifier.PRIVATE)
							) {
						processClass(element);
					}
				}
			}
		}
		return true;
	}

	protected void processClass(Element element) {		
		if(!processedElements.contains(element)){
			compiler.addDIAnnotations(element);
			
			if (!haveADefaultContructor(element)) {
//				System.out.println("Adding constructor to class " + element.getSimpleName());
				compiler.addDefaultConstructor(element);
				processedElements.add(element);
			}else{
				System.out.println("Already have a default constructor: " + element.getSimpleName());
			}
			
		}else{
			System.out.println("Already processed : " + element.getSimpleName());
		}
	}

	private boolean haveADefaultContructor(Element element) {
		int constructorsCount = 0;

		for (Element subelement : element.getEnclosedElements()) {
			if (subelement.getKind() == ElementKind.CONSTRUCTOR ) {
//				System.out.println("Constructor found: " + subelement);
				constructorsCount++;
				TypeMirror mirror = subelement.asType();
				if (mirror.accept(noArgsVisitor, null))
					return true;
			}
		}
//		System.out.println("constructors found for " + element.getSimpleName() + " [" + constructorsCount + "]");
		return constructorsCount == 0;
	}

	private static final TypeVisitor<Boolean, Void> noArgsVisitor = new SimpleTypeVisitor6<Boolean, Void>() {
		public Boolean visitExecutable(ExecutableType t, Void v) {
			return t.getParameterTypes().isEmpty();
		}
	};

}
