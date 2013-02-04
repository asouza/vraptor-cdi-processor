package br.com.caelum.vraptor.apt.processor;

import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.util.Names;

public class JavaCUtils {
	
	private TreeMaker maker;
	private Names names;

	public JavaCUtils(TreeMaker maker, Names names) {
		this.maker = maker;
		this.names = names;
	}

	public JCExpression createElement(String name){
		String[] elems = name.split("\\.");
		
		JCExpression e = maker.Ident(names.fromString(elems[0]));
		for (int i = 1 ; i < elems.length ; i++) {
			e = maker.Select(e, names.fromString(elems[i]));
		}
		return e;
	}

}
