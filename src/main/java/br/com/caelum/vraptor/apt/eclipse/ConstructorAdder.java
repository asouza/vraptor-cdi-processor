package br.com.caelum.vraptor.apt.eclipse;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class ConstructorAdder extends ASTVisitor {
	
	@Override
	public boolean visit(MethodDeclaration node) {
		return super.visit(node);
		
	}
	
	

}
