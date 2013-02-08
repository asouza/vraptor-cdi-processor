package br.com.caelum.vraptor.apt.javac;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;


import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCTypeParameter;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

/**
 * Visitor that modificates the source, adding a contructor
 * 
 * @author mariofts
 * 
 */
public class DefaultConstructorAdder extends TreeTranslator {

	private TreeMaker maker;
	private Names names;
	private JavaCUtils utils;

	public DefaultConstructorAdder(ProcessingEnvironment processingEnv) {
		Context context = ((JavacProcessingEnvironment) processingEnv)
				.getContext();
		maker = TreeMaker.instance(context);
		names = Names.instance(context); // ONLY IN JAVA 7....
		utils = new JavaCUtils(maker, names);
	}

	@Override
	public void visitClassDef(JCClassDecl classDef) {
		super.visitClassDef(classDef);

		for (JCTree member : classDef.defs) {
			if (isVariable(member) && needToRemoveFinal(member)) {
				removeFinal(member);
			}
		}
		JCTree constructor = createAConstructor();
		classDef.defs = classDef.defs.append(constructor);
	}

	private void removeFinal(JCTree member) {
		JCVariableDecl var = (JCVariableDecl) member;
		JCModifiers modifiersWithoutFinal = maker.Modifiers(var.mods.flags ^ Flags.FINAL);
		var.mods = modifiersWithoutFinal;
	}

	protected boolean isVariable(JCTree each) {
		return each.getKind() == Kind.VARIABLE;
	}

	private boolean needToRemoveFinal(JCTree member) {
		JCVariableDecl var = (JCVariableDecl) member;
		return (isFinal(var) && !isStatic(var) && !isInitialized(var));
	}

	protected boolean isFinal(JCVariableDecl var) {
		return var.getModifiers().getFlags().contains(Modifier.FINAL);
	}

	private boolean isStatic(JCVariableDecl var) {
		return (var.mods.flags & Flags.STATIC) != 0;
	}

	public boolean isInitialized(JCVariableDecl var) {
		return var.init != null;
	}

	/**
	 * Creates a default constructor
	 * 
	 * public ClassName(){}
	 * 
	 * @return
	 */
	private JCTree createAConstructor() {
		JCModifiers mod = maker.Modifiers(Flags.PUBLIC,List.<JCAnnotation> nil());
		
		JCMethodDecl constructor = maker.MethodDef(mod, names.init, null,
				List.<JCTypeParameter> nil(), List.<JCVariableDecl> nil(),
				List.<JCExpression> nil(),
				maker.Block(0L, List.<JCStatement> nil()), null);
		
		JCAnnotation injectAnnotation = maker.Annotation(utils.createElement("java.lang.Deprecated"),List.<JCExpression>nil());
		constructor.mods.annotations = constructor.mods.annotations.append(injectAnnotation);
		
		return constructor;

	}
}