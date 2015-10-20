package eu.swept.filter;

public abstract class WIFCAttackBase implements WIFCAttack {

	protected WIFCElement attackRoot;
	protected XmlTags attackTag;
	
	protected WIFCAttackBase(XmlTags attackTag) {
		this.attackTag = attackTag;
		this.attackRoot = null;
	}
	
	public void setAttackRoot(WIFCElement root) {
		this.attackRoot = root.appendXmlTag(this.attackTag);
	}
}
