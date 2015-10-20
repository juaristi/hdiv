package eu.swept.filter;

import org.hdiv.filter.ValidatorError;

public interface WIFCAttack {

	public void printAttack(ValidatorError error);
	public void setAttackRoot(WIFCElement root);
}
