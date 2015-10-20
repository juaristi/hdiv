package eu.swept.filter;

import org.hdiv.filter.ValidatorError;

public class WIFCEditableAttack extends WIFCAttackBase {

	public WIFCEditableAttack() {
		super(XmlTags.EDITABLE_ATTACK);
	}

	public void printAttack(ValidatorError error) {
		if (this.attackRoot != null) {
			this.attackRoot.appendXmlTag(XmlTags.URL, error.getTarget());
			this.attackRoot.appendXmlTag(XmlTags.PARAMETER, error.getParameterName());
			this.attackRoot.appendXmlTag(XmlTags.VALUE, error.getParameterValue());
			this.attackRoot.appendXmlTag(XmlTags.REJECTED_PATTERN, error.getValidationRuleName());
		}
	}

}
