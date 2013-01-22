package br.com.caelum.vraptor.apt.processor;

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
import javax.tools.Diagnostic;

import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Flags.Flag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCTypeParameter;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;


@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class VraptorProcessor extends AbstractProcessor{
	
	private Trees trees;
	private TreeMaker make;
	private Names names;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		trees = Trees.instance(processingEnv);
		Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
		make = TreeMaker.instance(context);
		names = Names.instance(context); //ONLY IN JAVA 7....
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		
		if(!roundEnv.processingOver()){
			Set<? extends Element> elements = roundEnv.getRootElements();
			for (Element each : elements) {
				if(each.getKind() == ElementKind.CLASS){
					JCTree tree = (JCTree) trees.getTree(each);
					TreeTranslator visitor = new Inliner();
					tree.accept(visitor);
				}
			}
		}
		return true;
	}


	/**
	 * Prints the content of a object as a note
	 * @param obj
	 */
	protected void print(Object obj) {
		if(obj == null){
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "null");
		}else{
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, obj.toString());
		}
	}

	/**
	 * Visitor that modificates the source, adding a contructor
	 * 
	 * @author mariofts
	 *
	 */
	private class Inliner extends TreeTranslator{
		
		@Override
		public void visitClassDef(JCClassDecl classDef) {
			super.visitClassDef(classDef);
			
			List<JCTree> defs = List.nil();
			
			for (JCTree member : classDef.defs) {
				if(isVariable(member) && hasFinal(member)){
					print(member); 
					removeFinal(member);
					print(member);
				}
			}
			JCTree constructor = createAConstructor();
			classDef.defs = classDef.defs.append(constructor);
		}

		private void removeFinal(JCTree member) {
			JCVariableDecl var = (JCVariableDecl) member;
			JCModifiers modifiersWithoutFinal = make.Modifiers(var.mods.flags ^ 1<<4);
			var.mods = modifiersWithoutFinal;
		}

		protected boolean isVariable(JCTree each) {
			return each.getKind() == Kind.VARIABLE;
		}
		
		private boolean hasFinal(JCTree each) {
			JCVariableDecl var = (JCVariableDecl) each;
			return var.getModifiers().getFlags().contains(Modifier.FINAL);
		}

//		@Override
//		public void visitVarDef(JCVariableDecl tree) {
//			super.visitVarDef(tree);
//			print("vardef:");
//			print(tree);
//			
//			print("VARTYPE: " + tree.vartype);
//			print("TYPE: " + tree.type);
//			print("MODS:" + tree.mods);
//			print("SYM: " + tree.sym);
//			print("SYM: " + tree.sym);
//			print("KIND: " + tree.getKind());
//			print("------");
//			
//		}
				
		/**
		 * Creates a default constructor
		 * 
		 * public ClassName(){}
		 * 
		 * @return
		 */
		private JCTree createAConstructor(){
			JCModifiers mod = make.Modifiers(Flags.PUBLIC,List.<JCAnnotation>nil());
			
			return make.MethodDef(mod, names.init, null, List.<JCTypeParameter>nil(), List.<JCVariableDecl>nil(), 
					List.<JCExpression>nil(), make.Block(0L, List.<JCStatement>nil()), null);
						   
		}
	}

}
