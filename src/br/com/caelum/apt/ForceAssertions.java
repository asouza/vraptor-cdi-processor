package br.com.caelum.apt;

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
import javax.tools.Diagnostic;

import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCAssert;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCTypeParameter;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;


@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ForceAssertions extends AbstractProcessor{
	
	private int tally;
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
		tally = 0;
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
		}else{
			syso(tally + " assertions inlined.");
		}
		return false;
	}


	protected void syso(Object obj) {
		processingEnv.getMessager().printMessage(
				Diagnostic.Kind.NOTE, obj.toString());
	}
	
	private class Inliner extends TreeTranslator{
		@Override
		public void visitAssert(JCAssert tree) {
			super.visitAssert(tree);
			JCStatement newNode = makeIfThrowException(tree);
			this.result = newNode;
			tally++;
		}
		
		@Override
		public void visitClassDef(JCClassDecl tree) {
			super.visitClassDef(tree);
			List<JCTree> members = tree.getMembers();
			
		}
		
		private void makeAConstructor(JCClassDecl tree){
			JCModifiers mod = make.Modifiers(Flags.PUBLIC,List.<JCAnnotation>nil());
			ListBuffer<JCStatement> nullChecks = ListBuffer.lb();
			
			JCMethodDecl constr = make.MethodDef(new Symbol.MethodSymbol(0l, names.init,null, null), make.Block(0l, nullChecks.toList()));
			tree.getMembers().add(constr);
						   
		}

		private JCStatement makeIfThrowException(JCAssert node) {			
			//Make a if(!condition) throw new AssertionError(detail);
			
			List<JCExpression> args = node.getDetail() == null ? List.<JCExpression>nil() : List.of(node.detail); 
//			JCMethodDecl contr = make.
			JCNewClass expr = make.NewClass(null, null, make.Ident(names.fromString("AssertionError")), args, null);
			return make.If(make.Unary(JCTree.NOT, node.cond), make.Throw(expr), null);
		}
	}

}
