package eu.swept.filter;

import org.hdiv.filter.ValidatorError;

public class WIFCIntegrityAttack extends WIFCAttackBase {

	public WIFCIntegrityAttack() {
		super(XmlTags.INTEGRITY_ATTACK);
	}

	public void printAttack(ValidatorError error) {
		if (this.attackRoot != null) {
			this.attackRoot.appendXmlTag(XmlTags.URL, error.getTarget());
			
			WIFCElement paramRoot = this.attackRoot.appendXmlTag(XmlTags.PARAMETER);
			paramRoot.appendXmlTag(XmlTags.NAME, error.getParameterName());
			paramRoot.appendXmlTag(XmlTags.ORIGINAL_VALUE, error.getOriginalParameterValue());
			paramRoot.appendXmlTag(XmlTags.VALUE, error.getParameterValue());
		}
	}

}
